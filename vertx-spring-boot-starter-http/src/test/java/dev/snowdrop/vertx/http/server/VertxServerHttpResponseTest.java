package dev.snowdrop.vertx.http.server;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

import dev.snowdrop.vertx.http.utils.BufferConverter;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.Cookie;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.http.impl.headers.VertxHttpHeaders;
import io.vertx.ext.web.RoutingContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.publisher.TestPublisher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class VertxServerHttpResponseTest {

    @Mock
    private RoutingContext mockRoutingContext;

    @Mock
    private HttpServerResponse mockHttpServerResponse;

    private BufferConverter bufferConverter;

    private VertxServerHttpResponse response;

    @BeforeEach
    public void setUp() {
        given(mockRoutingContext.response()).willReturn(mockHttpServerResponse);
        given(mockHttpServerResponse.headers()).willReturn(new VertxHttpHeaders());

        bufferConverter = new BufferConverter();
        response = new VertxServerHttpResponse(mockRoutingContext, bufferConverter);
    }

    @Test
    public void shouldInitHeaders() {
        MultiMap originalHeaders = new VertxHttpHeaders()
            .add("key1", "value1")
            .add("key1", "value2")
            .add("key2", "value3");
        given(mockHttpServerResponse.headers()).willReturn(originalHeaders);

        response = new VertxServerHttpResponse(mockRoutingContext, bufferConverter);

        HttpHeaders expectedHeaders = new HttpHeaders();
        expectedHeaders.add("key1", "value1");
        expectedHeaders.add("key1", "value2");
        expectedHeaders.add("key2", "value3");

        assertThat(response.getHeaders()).isEqualTo(expectedHeaders);
    }

    @Test
    public void shouldGetNativeResponse() {
        assertThat((HttpServerResponse) response.getNativeResponse()).isEqualTo(mockHttpServerResponse);
    }

    @Test
    public void shouldWriteFile() {
        given(mockHttpServerResponse.sendFile(any(), anyLong(), anyLong(), any())).will(invocation -> {
            Handler<AsyncResult<Void>> handler = invocation.getArgument(3);
            handler.handle(Future.succeededFuture());
            return mockHttpServerResponse;
        });

        response.writeWith(Paths.get("/tmp/test"), 0, 0)
            .block();

        verify(mockHttpServerResponse).sendFile(eq("/tmp/test"), eq(0L), eq(0L), any());
    }

    @Test
    public void shouldWriteFromPublisher() {
        Buffer firstChunk = Buffer.buffer("chunk 1");
        Buffer secondChunk = Buffer.buffer("chunk 2");

        TestPublisher<DataBuffer> source = TestPublisher.create();
        Mono<Void> result = response.writeWithInternal(source);

        StepVerifier.create(result)
            .expectSubscription()
            .then(() -> source.assertMinRequested(1))
            .then(() -> source.next(bufferConverter.toDataBuffer(firstChunk)))
            .then(() -> source.assertMinRequested(1))
            .then(() -> source.next(bufferConverter.toDataBuffer(secondChunk)))
            .then(() -> source.assertMinRequested(1))
            .then(source::complete)
            .verifyComplete();

        verify(mockHttpServerResponse).write(firstChunk);
        verify(mockHttpServerResponse).write(secondChunk);
    }

    @Test
    public void shouldWriteFromPublisherAndFlush() {
        Buffer firstChunk = Buffer.buffer("chunk 1");
        Buffer secondChunk = Buffer.buffer("chunk 2");

        TestPublisher<DataBuffer> source = TestPublisher.create();
        Mono<Void> result = response.writeAndFlushWithInternal(Flux.just(source));

        StepVerifier.create(result)
            .expectSubscription()
            .then(() -> source.assertMinRequested(1))
            .then(() -> source.next(bufferConverter.toDataBuffer(firstChunk)))
            .then(() -> source.assertMinRequested(1))
            .then(() -> source.next(bufferConverter.toDataBuffer(secondChunk)))
            .then(() -> source.assertMinRequested(1))
            .then(source::complete)
            .verifyComplete();

        verify(mockHttpServerResponse).write(firstChunk);
        verify(mockHttpServerResponse).write(secondChunk);
    }

    @Test
    public void shouldApplyStatusCode() {
        response.setStatusCode(HttpStatus.OK);
        response.applyStatusCode();
        verify(mockHttpServerResponse).setStatusCode(200);
    }

    @Test
    public void shouldNotApplyNullStatusCode() {
        response.applyStatusCode();
        verify(mockHttpServerResponse, times(0)).setStatusCode(anyInt());
    }

    @Test
    public void shouldApplyHeaders() {
        response.getHeaders().put("key1", Arrays.asList("value1", "value2"));
        response.getHeaders().add("key2", "value3");

        response.applyHeaders();
        verify(mockHttpServerResponse).putHeader("key1", (Iterable<String>) Arrays.asList("value1", "value2"));
        verify(mockHttpServerResponse).putHeader("key2", (Iterable<String>) Collections.singletonList("value3"));
    }

    @Test
    public void shouldApplyCookies() {
        response.addCookie(
            ResponseCookie.from("cookie1", "value1")
                .domain("domain1")
                .path("path1")
                .maxAge(1)
                .httpOnly(true)
                .secure(true)
                .build()
        );
        response.addCookie(
            ResponseCookie.from("cookie1", "value2")
                .domain("domain1")
                .path("path1")
                .maxAge(1)
                .httpOnly(true)
                .secure(true)
                .build()
        );
        response.addCookie(
            ResponseCookie.from("cookie2", "value3")
                .domain("domain2")
                .path("path2")
                .maxAge(2)
                .httpOnly(false)
                .secure(false)
                .build()
        );

        response.applyCookies();

        // Cookie implementation doesn't override equals, so need to work around to be able to assert values
        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(mockRoutingContext, times(3)).addCookie(cookieCaptor.capture());

        assertThat(cookieCaptor.getAllValues().get(0)).isEqualToComparingFieldByField(
            Cookie.cookie("cookie1", "value1")
                .setDomain("domain1")
                .setPath("path1")
                .setMaxAge(1)
                .setHttpOnly(true)
                .setSecure(true)
        );
        assertThat(cookieCaptor.getAllValues().get(1)).isEqualToComparingFieldByField(
            Cookie.cookie("cookie1", "value2")
                .setDomain("domain1")
                .setPath("path1")
                .setMaxAge(1)
                .setHttpOnly(true)
                .setSecure(true)
        );
        assertThat(cookieCaptor.getAllValues().get(2)).isEqualToComparingFieldByField(
            Cookie.cookie("cookie2", "value3")
                .setDomain("domain2")
                .setPath("path2")
                .setMaxAge(2)
                .setHttpOnly(false)
                .setSecure(false)
        );
    }
}
