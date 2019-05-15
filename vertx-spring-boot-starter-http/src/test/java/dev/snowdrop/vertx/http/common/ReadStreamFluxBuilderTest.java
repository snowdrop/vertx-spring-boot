package dev.snowdrop.vertx.http.common;

import java.util.function.Function;

import io.vertx.core.Handler;
import io.vertx.core.streams.ReadStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ReadStreamFluxBuilderTest {

    @Mock
    private ReadStream<String> mockReadStream;

    @Test(expected = NullPointerException.class)
    public void shouldNotAcceptNullReadStream() {
        new ReadStreamFluxBuilder<String, String>()
            .dataConverter(Function.identity())
            .build();
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAcceptNullDataConverter() {
        new ReadStreamFluxBuilder<String, String>()
            .readStream(mockReadStream)
            .build();
    }

    @Test
    public void shouldHandleData() {
        given(mockReadStream.handler(any())).will(invocation -> {
            Handler<String> handler = invocation.getArgument(0);
            handler.handle("first");
            handler.handle("second");
            return mockReadStream;
        });
        given(mockReadStream.exceptionHandler(any())).willReturn(mockReadStream);
        given(mockReadStream.endHandler(any())).will(invocation -> {
            Handler<Void> handler = invocation.getArgument(0);
            handler.handle(null);
            return mockReadStream;
        });

        Flux<String> flux = new ReadStreamFluxBuilder<String, String>()
            .readStream(mockReadStream)
            .dataConverter(String::toUpperCase)
            .build();

        StepVerifier.create(flux)
            .expectNext("FIRST")
            .expectNext("SECOND")
            .verifyComplete();
        verify(mockReadStream).pause();
        verify(mockReadStream).fetch(Long.MAX_VALUE);
    }

    @Test
    public void shouldHandleException() {
        given(mockReadStream.handler(any())).willReturn(mockReadStream);
        given(mockReadStream.exceptionHandler(any())).will(invocation -> {
            Handler<Throwable> handler = invocation.getArgument(0);
            handler.handle(new RuntimeException("test"));
            return mockReadStream;
        });
        given(mockReadStream.endHandler(any())).willReturn(mockReadStream);

        Flux<String> flux = new ReadStreamFluxBuilder<String, String>()
            .readStream(mockReadStream)
            .dataConverter(String::toUpperCase)
            .build();

        StepVerifier.create(flux)
            .verifyErrorMessage("test");
        verify(mockReadStream).pause();
        verify(mockReadStream).fetch(Long.MAX_VALUE);
    }
}
