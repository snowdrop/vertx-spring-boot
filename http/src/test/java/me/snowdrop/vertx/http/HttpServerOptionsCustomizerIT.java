package me.snowdrop.vertx.http;

import me.snowdrop.vertx.http.properties.HttpServerOptionsCustomizer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.server.WebServer;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HttpServerOptionsCustomizerIT {

    @Autowired
    private VertxReactiveWebServerFactory factory;

    @Test
    public void shouldSetCorrectPort() {
        WebServer server = factory.getWebServer(null);
        assertThat(server.getPort()).isEqualTo(8081);
    }

    @TestConfiguration
    static class Customizers {

        @Bean
        public HttpServerOptionsCustomizer portCustomizer() {
            return options -> options.setPort(8081);
        }

    }

}
