package dev.snowdrop.vertx.http.test;

import dev.snowdrop.vertx.http.client.VertxClientHttpConnector;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClientConfigurer;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = { TestApplication.class, CustomWebTestClientIT.CustomWebTestClientConfiguration.class }
)
public class CustomWebTestClientIT {

    @Autowired
    private WebTestClient client;

    @Test
    public void shouldInjectCustomClient() {
        assertThat(client).isInstanceOf(CustomWebTestClient.class);
    }

    @Configuration
    static class CustomWebTestClientConfiguration {
        @Bean
        public WebTestClient customWebTestClient(VertxClientHttpConnector connector) {
            return new CustomWebTestClient();
        }
    }

    private static class CustomWebTestClient implements WebTestClient {
        @Override
        public RequestHeadersUriSpec<?> get() {
            return null;
        }

        @Override
        public RequestHeadersUriSpec<?> head() {
            return null;
        }

        @Override
        public RequestBodyUriSpec post() {
            return null;
        }

        @Override
        public RequestBodyUriSpec put() {
            return null;
        }

        @Override
        public RequestBodyUriSpec patch() {
            return null;
        }

        @Override
        public RequestHeadersUriSpec<?> delete() {
            return null;
        }

        @Override
        public RequestHeadersUriSpec<?> options() {
            return null;
        }

        @Override
        public RequestBodyUriSpec method(HttpMethod method) {
            return null;
        }

        @Override
        public Builder mutate() {
            return null;
        }

        @Override
        public WebTestClient mutateWith(WebTestClientConfigurer configurer) {
            return null;
        }
    }
}
