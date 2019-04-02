package me.snowdrop.vertx.http;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;

import io.netty.buffer.ByteBufAllocator;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.http.impl.headers.VertxHttpHeaders;
import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.RoutingContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
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

    private NettyDataBufferFactory nettyDataBufferFactory;

    @Before
    public void setUp() {
        given(mockRoutingContext.response()).willReturn(mockHttpServerResponse);
        given(mockHttpServerResponse.headers()).willReturn(new VertxHttpHeaders());

        nettyDataBufferFactory = new NettyDataBufferFactory(ByteBufAllocator.DEFAULT);
    }

    @Test
    public void shouldInitHeaders() {
        MultiMap originalHeaders = new VertxHttpHeaders()
            .add("key1", "value1")
            .add("key1", "value2")
            .add("key2", "value3");
        given(mockHttpServerResponse.headers()).willReturn(originalHeaders);

        VertxServerHttpResponse response = new VertxServerHttpResponse(mockRoutingContext, nettyDataBufferFactory);

        HttpHeaders expectedHeaders = new HttpHeaders();
        expectedHeaders.add("key1", "value1");
        expectedHeaders.add("key1", "value2");
        expectedHeaders.add("key2", "value3");

        assertThat(response.getHeaders()).isEqualTo(expectedHeaders);
    }

    @Test
    public void shouldGetNativeResponse() {
        VertxServerHttpResponse response = new VertxServerHttpResponse(mockRoutingContext, nettyDataBufferFactory);
        assertThat((HttpServerResponse) response.getNativeResponse()).isEqualTo(mockHttpServerResponse);
    }

    @Test
    public void shouldWriteFile() {
        VertxServerHttpResponse response = new VertxServerHttpResponse(mockRoutingContext, nettyDataBufferFactory);

        StepVerifier.create(response.writeWith(new File("/tmp/test").toPath(), 0, 0))
            .expectComplete();

        verify(mockHttpServerResponse).sendFile("/tmp/test", 0, 0);
    }

    @Test
    public void shouldWriteFromPublisher() {
        VertxServerHttpResponse response = new VertxServerHttpResponse(mockRoutingContext, nettyDataBufferFactory);
        Buffer firstChunk = Buffer.buffer("chunk 1");
        Buffer secondChunk = Buffer.buffer("chunk 2");

        TestPublisher<DataBuffer> source = TestPublisher.create();
        Mono<Void> result = response.writeWithInternal(source);

        StepVerifier.create(result)
            .expectSubscription()
            .then(() -> source.assertMinRequested(1))
            .then(() -> source.next(nettyDataBufferFactory.wrap(firstChunk.getByteBuf())))
            .then(() -> source.assertMinRequested(1))
            .then(() -> source.next(nettyDataBufferFactory.wrap(secondChunk.getByteBuf())))
            .then(() -> source.assertMinRequested(1))
            .then(source::complete)
            .verifyComplete();

        verify(mockHttpServerResponse).write(firstChunk);
        verify(mockHttpServerResponse).write(secondChunk);
    }

    @Test
    public void shouldWriteFromPublisherWithoutChunks() {
        given(mockHttpServerResponse.headers()).willReturn(new VertxHttpHeaders().add(HttpHeaders.CONTENT_LENGTH, "4"));

        VertxServerHttpResponse response = new VertxServerHttpResponse(mockRoutingContext, nettyDataBufferFactory);
        Buffer data = Buffer.buffer("data");

        TestPublisher<DataBuffer> source = TestPublisher.create();
        Mono<Void> result = response.writeWithInternal(source);

        StepVerifier.create(result)
            .expectSubscription()
            .then(() -> source.assertMinRequested(1))
            .then(() -> source.next(nettyDataBufferFactory.wrap(data.getByteBuf())))
            .then(() -> source.assertMinRequested(1))
            .then(source::complete)
            .verifyComplete();

        verify(mockHttpServerResponse).write(data);
    }

    @Test
    public void shouldWriteFromPublisherAndFlush() {
        VertxServerHttpResponse response = new VertxServerHttpResponse(mockRoutingContext, nettyDataBufferFactory);
        Buffer firstChunk = Buffer.buffer("chunk 1");
        Buffer secondChunk = Buffer.buffer("chunk 2");

        TestPublisher<DataBuffer> source = TestPublisher.create();
        Mono<Void> result = response.writeAndFlushWithInternal(Flux.just(source));

        StepVerifier.create(result)
            .expectSubscription()
            .then(() -> source.assertMinRequested(1))
            .then(() -> source.next(nettyDataBufferFactory.wrap(firstChunk.getByteBuf())))
            .then(() -> source.assertMinRequested(1))
            .then(() -> source.next(nettyDataBufferFactory.wrap(secondChunk.getByteBuf())))
            .then(() -> source.assertMinRequested(1))
            .then(source::complete)
            .verifyComplete();

        verify(mockHttpServerResponse).write(firstChunk);
        verify(mockHttpServerResponse).write(secondChunk);
    }

    @Test
    public void shouldApplyStatusCode() {
        VertxServerHttpResponse response = new VertxServerHttpResponse(mockRoutingContext, nettyDataBufferFactory);

        response.setStatusCode(HttpStatus.OK);
        response.applyStatusCode();
        verify(mockHttpServerResponse).setStatusCode(200);
    }

    @Test
    public void shouldNotApplyNullStatusCode() {
        VertxServerHttpResponse response = new VertxServerHttpResponse(mockRoutingContext, nettyDataBufferFactory);

        response.applyStatusCode();
        verify(mockHttpServerResponse, times(0)).setStatusCode(anyInt());
    }

    @Test
    public void shouldApplyHeaders() {
        VertxServerHttpResponse response = new VertxServerHttpResponse(mockRoutingContext, nettyDataBufferFactory);
        response.getHeaders().put("key1", Arrays.asList("value1", "value2"));
        response.getHeaders().add("key2", "value3");

        response.applyHeaders();
        verify(mockHttpServerResponse).putHeader("key1", (Iterable<String>) Arrays.asList("value1", "value2"));
        verify(mockHttpServerResponse).putHeader("key2", (Iterable<String>) Collections.singletonList("value3"));
    }

    @Test
    public void shouldApplyCookies() {
        VertxServerHttpResponse response = new VertxServerHttpResponse(mockRoutingContext, nettyDataBufferFactory);

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
