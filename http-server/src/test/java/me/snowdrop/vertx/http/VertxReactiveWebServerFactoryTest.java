package me.snowdrop.vertx.http;

import io.netty.buffer.ByteBufAllocator;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerOptions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.web.server.WebServer;
import org.springframework.core.io.buffer.NettyDataBufferFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class VertxReactiveWebServerFactoryTest {

    @Mock
    private Vertx mockVertx;

    @Mock
    private HttpServerOptionsCustomizer mockCustomizer;

    private VertxReactiveWebServerFactory webServerFactory;

    @Before
    public void setUp() {
        NettyDataBufferFactory nettyDataBufferFactory = new NettyDataBufferFactory(ByteBufAllocator.DEFAULT);
        webServerFactory = new VertxReactiveWebServerFactory(mockVertx, nettyDataBufferFactory);
    }

    @Test
    public void shouldCreateWebServer() {
        WebServer webServer = webServerFactory.getWebServer(null);
        assertThat(webServer).isNotNull();
        assertThat(webServer).isInstanceOf(VertxWebServer.class);
    }

    @Test
    public void shouldCustomizeHttpServerOptions() {
        webServerFactory.registerHttpServerOptionsCustomizer(mockCustomizer);
        WebServer webServer = webServerFactory.getWebServer(null);
        assertThat(webServer).isNotNull();
        verify(mockCustomizer).apply(any(HttpServerOptions.class));
    }
}
