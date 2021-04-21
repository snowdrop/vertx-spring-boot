package dev.snowdrop.vertx.http.server;

import dev.snowdrop.vertx.http.server.properties.HttpServerOptionsCustomizer;
import dev.snowdrop.vertx.http.server.properties.HttpServerProperties;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.server.WebServer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
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

    @BeforeEach
    public void setUp() {
        given(mockHttpServerProperties.getHttpServerOptions()).willReturn(mockHttpServerOptions);

        webServerFactory = new VertxReactiveWebServerFactory(mockVertx, mockHttpServerProperties);
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
