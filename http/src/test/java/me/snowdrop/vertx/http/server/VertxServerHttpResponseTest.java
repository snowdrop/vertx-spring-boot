package me.snowdrop.vertx.http.server;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;

import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.http.impl.headers.VertxHttpHeaders;
import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.RoutingContext;
import me.snowdrop.vertx.http.utils.BufferConverter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.publisher.TestPublisher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class VertxServerHttpResponseTest {

    @Mock
    private RoutingContext mockRoutingContext;

    @Mock
    private HttpServerResponse mockHttpServerResponse;

    private BufferConverter bufferConverter;

    private VertxServerHttpResponse response;

    @Before
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
        StepVerifier.create(response.writeWith(new File("/tmp/test").toPath(), 0, 0))
            .expectComplete();

        verify(mockHttpServerResponse).sendFile("/tmp/test", 0, 0);
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
        ResponseCookie firstCookie = ResponseCookie.from("cookie1", "value1")
            .domain("domain1")
            .path("path1")
            .maxAge(1)
            .httpOnly(true)
            .secure(true)
            .build();
        ResponseCookie secondCookie = ResponseCookie.from("cookie2", "value2")
            .domain("domain2")
            .path("path2")
            .maxAge(2)
            .httpOnly(false)
            .secure(false)
            .build();

        response.addCookie(firstCookie);
        response.addCookie(secondCookie);
        response.applyCookies();

        Cookie expectedFirstCookie = Cookie.cookie("cookie1", "value1")
            .setDomain("domain1")
            .setPath("path1")
            .setMaxAge(1)
            .setHttpOnly(true)
            .setSecure(true);
        Cookie expectedSecondCookie = Cookie.cookie("cookie2", "value2")
            .setDomain("domain2")
            .setPath("path2")
            .setMaxAge(2)
            .setHttpOnly(false)
            .setSecure(false);

        // Cookie implementation doesn't override equals, so need to work around to be able to assert values
        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(mockRoutingContext, times(2)).addCookie(cookieCaptor.capture());

        assertThat(cookieCaptor.getAllValues().get(0)).isEqualToComparingFieldByField(expectedFirstCookie);
        assertThat(cookieCaptor.getAllValues().get(1)).isEqualToComparingFieldByField(expectedSecondCookie);
    }
}
