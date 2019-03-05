package me.snowdrop.vertx.http;

import java.io.File;

import io.netty.buffer.ByteBufAllocator;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.RoutingContext;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import reactor.core.publisher.Flux;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static reactor.test.StepVerifier.create;

@RunWith(MockitoJUnitRunner.class)
public class VertxServerHttpResponseTest {

    @Mock
    private RoutingContext mockRoutingContext;

    @Mock
    private HttpServerResponse mockHttpServerResponse;

    private NettyDataBufferFactory nettyDataBufferFactory;

    private VertxServerHttpResponse vertxServerHttpResponse;

    @Before
    public void setUp() throws Exception {
        given(mockRoutingContext.response()).willReturn(mockHttpServerResponse);

        nettyDataBufferFactory = new NettyDataBufferFactory(ByteBufAllocator.DEFAULT);
        vertxServerHttpResponse = new VertxServerHttpResponse(mockRoutingContext, nettyDataBufferFactory);
    }

    @Test
    public void shouldGetNativeResponse() {
        assertThat((HttpServerResponse) vertxServerHttpResponse.getNativeResponse()).isEqualTo(mockHttpServerResponse);
    }

    @Test
    public void shouldWriteFile() {
        create(vertxServerHttpResponse.writeWith(new File("/tmp/test").toPath(), 0, 0))
            .expectComplete();

        verify(mockHttpServerResponse).sendFile("/tmp/test", 0, 0);
    }

    @Test
    public void shouldWriteFromPublisher() {
        Buffer firstChunk = Buffer.buffer("chunk 1");
        Buffer secondChunk = Buffer.buffer("chunk 2");

        Flux<DataBuffer> source = Flux.just(nettyDataBufferFactory.wrap(firstChunk.getByteBuf()),
            nettyDataBufferFactory.wrap(secondChunk.getByteBuf()));

        create(vertxServerHttpResponse.writeWithInternal(source))
            .verifyComplete();

        verify(mockHttpServerResponse).write(firstChunk);
        verify(mockHttpServerResponse).write(secondChunk);
    }

    @Test
    public void shouldWriteFromPublisherAndFlush() {
        Buffer firstChunk = Buffer.buffer("chunk 1");
        Buffer secondChunk = Buffer.buffer("chunk 2");

        Flux<DataBuffer> source = Flux.just(nettyDataBufferFactory.wrap(firstChunk.getByteBuf()),
            nettyDataBufferFactory.wrap(secondChunk.getByteBuf()));

        create(vertxServerHttpResponse.writeAndFlushWithInternal(Flux.just(source)))
            .verifyComplete();

        verify(mockHttpServerResponse).write(firstChunk);
        verify(mockHttpServerResponse).write(secondChunk);
    }

    @Test
    public void shouldApplyStatusCode() {
        vertxServerHttpResponse.setStatusCode(HttpStatus.OK);
        vertxServerHttpResponse.applyStatusCode();
        verify(mockHttpServerResponse).setStatusCode(200);
    }

    @Test
    public void shouldNotApplyNullStatusCode() {
        vertxServerHttpResponse.applyStatusCode();
        verify(mockHttpServerResponse, times(0)).setStatusCode(anyInt());
    }

    @Test
    public void shouldApplyEmptyHeaders() {
        vertxServerHttpResponse.applyHeaders();
        verify(mockHttpServerResponse).setChunked(true);
    }

    @Test
    @Ignore
    public void shouldApplyHeaders() {

    }

    @Test
    @Ignore
    public void shouldApplyHeadersWithContentLength() {

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

        vertxServerHttpResponse.addCookie(firstCookie);
        vertxServerHttpResponse.addCookie(secondCookie);
        vertxServerHttpResponse.applyCookies();

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
