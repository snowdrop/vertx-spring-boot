package dev.snowdrop.vertx.http.client;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;

import dev.snowdrop.vertx.http.utils.BufferConverter;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClientRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpMethod;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.publisher.TestPublisher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class VertxClientHttpRequestTest {

    @Mock
    private HttpClientRequest mockHttpClientRequest;

    private BufferConverter bufferConverter;

    private VertxClientHttpRequest request;

    @BeforeEach
    public void setUp() {
        bufferConverter = new BufferConverter();
        request = new VertxClientHttpRequest(mockHttpClientRequest, bufferConverter);
    }

    @Test
    public void shouldGetMethod() {
        given(mockHttpClientRequest.getMethod()).willReturn(io.vertx.core.http.HttpMethod.GET);

        HttpMethod method = request.getMethod();

        assertThat(method).isEqualTo(HttpMethod.GET);
    }

    @Test
    public void shouldGetUri() {
        given(mockHttpClientRequest.absoluteURI()).willReturn("http://example.com");

        URI expectedUri = URI.create("http://example.com");
        URI actualUri = request.getURI();

        assertThat(actualUri).isEqualTo(expectedUri);
    }

    @Test
    public void shouldGetBufferFactory() {
        DataBufferFactory dataBufferFactory = request.bufferFactory();

        assertThat(dataBufferFactory).isEqualTo(bufferConverter.getDataBufferFactory());
    }

    @Test
    public void shouldWriteFromPublisher() {
        Buffer firstChunk = Buffer.buffer("chunk 1");
        Buffer secondChunk = Buffer.buffer("chunk 2");

        TestPublisher<DataBuffer> source = TestPublisher.create();
        Mono<Void> result = request.writeWith(source);

        StepVerifier.create(result)
            .expectSubscription()
            .then(() -> source.assertMinRequested(1))
            .then(() -> source.next(bufferConverter.toDataBuffer(firstChunk)))
            .then(() -> source.assertMinRequested(1))
            .then(() -> source.next(bufferConverter.toDataBuffer(secondChunk)))
            .then(() -> source.assertMinRequested(1))
            .then(source::complete)
            .verifyComplete();

        verify(mockHttpClientRequest).write(firstChunk);
        verify(mockHttpClientRequest).write(secondChunk);
        verify(mockHttpClientRequest).end();
    }

    @Test
    public void shouldWriteFromPublisherAndFlush() {
        Buffer firstChunk = Buffer.buffer("chunk 1");
        Buffer secondChunk = Buffer.buffer("chunk 2");

        TestPublisher<DataBuffer> source = TestPublisher.create();
        Mono<Void> result = request.writeAndFlushWith(Flux.just(source));

        StepVerifier.create(result)
            .expectSubscription()
            .then(() -> source.assertMinRequested(1))
            .then(() -> source.next(bufferConverter.toDataBuffer(firstChunk)))
            .then(() -> source.assertMinRequested(1))
            .then(() -> source.next(bufferConverter.toDataBuffer(secondChunk)))
            .then(() -> source.assertMinRequested(1))
            .then(source::complete)
            .verifyComplete();

        verify(mockHttpClientRequest).write(firstChunk);
        verify(mockHttpClientRequest).write(secondChunk);
        verify(mockHttpClientRequest).end();
    }

    @Test
    public void shouldSetComplete() {
        request.setComplete().block();

        verify(mockHttpClientRequest).end();
    }

    @Test
    public void shouldApplyHeaders() {
        request.getHeaders().put("key1", Arrays.asList("value1", "value2"));
        request.getHeaders().add("key2", "value3");

        request.applyHeaders();

        verify(mockHttpClientRequest).putHeader("key1", (Iterable<String>) Arrays.asList("value1", "value2"));
        verify(mockHttpClientRequest).putHeader("key2", (Iterable<String>) Collections.singletonList("value3"));
    }

    @Test
    public void shouldApplyCookies() {
        HttpCookie cookie1 = new HttpCookie("key1", "value1");
        HttpCookie cookie2 = new HttpCookie("key1", "value2");
        HttpCookie cookie3 = new HttpCookie("key2", "value3");
        request.getCookies().put("key1", Arrays.asList(cookie1, cookie2));
        request.getCookies().add("key2", cookie3);

        request.applyCookies();

        verify(mockHttpClientRequest).putHeader("Cookie", cookie1.toString());
        verify(mockHttpClientRequest).putHeader("Cookie", cookie2.toString());
        verify(mockHttpClientRequest).putHeader("Cookie", cookie3.toString());
    }
}
