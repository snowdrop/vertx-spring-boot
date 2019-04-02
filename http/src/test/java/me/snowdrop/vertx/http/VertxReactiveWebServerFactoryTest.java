package me.snowdrop.vertx.http;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerOptions;
import me.snowdrop.vertx.http.properties.HttpServerOptionsCustomizer;
import me.snowdrop.vertx.http.properties.HttpServerProperties;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.web.server.WebServer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class VertxReactiveWebServerFactoryTest {

    @Mock
    private Vertx mockVertx;

    @Mock
    private HttpServerOptionsCustomizer mockCustomizer;

    @Mock
    private HttpServerProperties mockHttpServerProperties;

    @Mock
    private HttpServerOptions mockHttpServerOptions;

    private VertxReactiveWebServerFactory webServerFactory;

    @Before
    public void setUp() {
        given(mockHttpServerProperties.getHttpServerOptions()).willReturn(mockHttpServerOptions);

        BufferConverter bufferConverter = new BufferConverter();
        webServerFactory = new VertxReactiveWebServerFactory(mockVertx, mockHttpServerProperties, bufferConverter);
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
        verify(mockCustomizer).apply(mockHttpServerOptions);
    }
}
