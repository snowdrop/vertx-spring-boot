package me.snowdrop.vertx.http.it;

import me.snowdrop.vertx.http.server.VertxReactiveWebServerFactory;
import me.snowdrop.vertx.http.server.properties.HttpServerOptionsCustomizer;
import org.junit.After;
import org.junit.Test;
import org.springframework.boot.web.server.WebServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

public class HttpServerOptionsCustomizerIT extends TestBase {

    @After
    public void tearDown() {
        stopServer();
    }

    @Test
    public void shouldSetCorrectPort() {
        startServer(PortCustomizer.class);

        VertxReactiveWebServerFactory factory = getBean(VertxReactiveWebServerFactory.class);
        WebServer server = factory.getWebServer(null);

        assertThat(server.getPort()).isEqualTo(8081);
    }

    @Configuration
    static class PortCustomizer {
        @Bean
        public HttpServerOptionsCustomizer portCustomizer() {
            return options -> options.setPort(8081);
        }
    }
}
