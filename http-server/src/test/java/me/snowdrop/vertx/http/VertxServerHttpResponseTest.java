package me.snowdrop.vertx.http;

import java.io.File;

import io.netty.buffer.ByteBufAllocator;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerResponse;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Flux;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static reactor.test.StepVerifier.create;

@RunWith(MockitoJUnitRunner.class)
public class VertxServerHttpResponseTest {

    @Mock
    private HttpServerResponse mockHttpServerResponse;

    private NettyDataBufferFactory nettyDataBufferFactory;

    private VertxServerHttpResponse vertxServerHttpResponse;

    @Before
    public void setUp() throws Exception {
        nettyDataBufferFactory = new NettyDataBufferFactory(ByteBufAllocator.DEFAULT);
        vertxServerHttpResponse = new VertxServerHttpResponse(mockHttpServerResponse, nettyDataBufferFactory);
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
    @Ignore
    public void shouldApplyCookies() {

    }
}
