package me.snowdrop.vertx.http;

import java.net.InetSocketAddress;

import io.netty.buffer.ByteBufAllocator;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.impl.headers.VertxHttpHeaders;
import io.vertx.core.net.SocketAddress;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class VertxServerHttpRequestTest {

    @Mock
    private HttpServerRequest mockHttpServerRequest;

    private NettyDataBufferFactory nettyDataBufferFactory;

    private VertxServerHttpRequest vertxServerHttpRequest;

    @Before
    public void setUp() {
        given(mockHttpServerRequest.uri()).willReturn("http://localhost:8080");
        given(mockHttpServerRequest.headers()).willReturn(new VertxHttpHeaders());

        nettyDataBufferFactory = new NettyDataBufferFactory(ByteBufAllocator.DEFAULT);
        vertxServerHttpRequest = new VertxServerHttpRequest(mockHttpServerRequest, nettyDataBufferFactory);
    }

    @Test
    public void shouldGetNativeRequest() {
        assertThat((HttpServerRequest) vertxServerHttpRequest.getNativeRequest()).isEqualTo(mockHttpServerRequest);
    }

    @Test
    public void shouldGetMethodValue() {
        given(mockHttpServerRequest.method()).willReturn(HttpMethod.GET);

        assertThat(vertxServerHttpRequest.getMethodValue()).isEqualTo("GET");
    }

    @Test
    public void shouldGetBody() {
        given(mockHttpServerRequest.handler(any())).will(invocation -> {
            Handler<Buffer> handler = invocation.getArgument(0);
            handler.handle(Buffer.buffer("chunk 1"));
            handler.handle(Buffer.buffer("chunk 2"));
            return mockHttpServerRequest;
        });
        given(mockHttpServerRequest.endHandler(any())).will(invocation -> {
            Handler<Void> handler = invocation.getArgument(0);
            handler.handle(null);
            return mockHttpServerRequest;
        });

        StepVerifier.create(vertxServerHttpRequest.getBody())
            .expectNext(nettyDataBufferFactory.wrap("chunk 1".getBytes()))
            .expectNext(nettyDataBufferFactory.wrap("chunk 2".getBytes()))
            .verifyComplete();
    }

    @Test
    public void shouldGetRemoteAddress() {
        SocketAddress original = SocketAddress.inetSocketAddress(8080, "localhost");
        given(mockHttpServerRequest.remoteAddress()).willReturn(original);

        InetSocketAddress expected = InetSocketAddress.createUnresolved("localhost", 8080);
        assertThat(vertxServerHttpRequest.getRemoteAddress()).isEqualTo(expected);
    }
}
