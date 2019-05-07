package me.snowdrop.vertx.http.it;

import java.time.Duration;
import java.util.Properties;

import org.junit.After;
import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.ACCEPT_ENCODING;
import static org.springframework.web.reactive.function.server.RouterFunctions.resources;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.noContent;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

public class HttpIT extends TestBase {

    @After
    public void tearDown() {
        stopServer();
    }

    @Test
    public void shouldGet404Response() {
        startServer();

        getWebTestClient()
            .get()
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    public void shouldGetEmptyResponse() {
        startServer(NoopRouter.class);

        getWebTestClient()
            .get()
            .exchange()
            .expectStatus()
            .isNoContent();
    }

    @Test
    public void shouldExchangeBodies() {
        startServer(UpperBodyRouter.class);

        getWebTestClient()
            .post()
            .syncBody("test")
            .exchange()
            .expectBody(String.class)
            .isEqualTo("TEST");
    }

    @Test
    public void shouldGetStaticContent() {
        startServer(StaticRouter.class);

        getWebTestClient()
            .get()
            .uri("static/index.html")
            .exchange()
            .expectBody(String.class)
            .isEqualTo("<html><body><div>Test div</div></body></html>\n");
    }

    @Test
    public void shouldGetCompressedStaticContent() {
        Properties properties = new Properties();
        properties.setProperty("server.compression.enabled", "true");
        properties.setProperty("vertx.http.client.try-use-compression", "true");
        startServer(properties, StaticRouter.class);

        getWebTestClient()
            .get()
            .uri("static/index.html")
            .header(ACCEPT_ENCODING, "gzip")
            .exchange()
            .expectBody(String.class)
            .isEqualTo("<html><body><div>Test div</div></body></html>\n");
    }

    @Test
    public void shouldExchangeHeaders() {
        startServer(UpperHeaderRouter.class);

        getWebTestClient()
            .get()
            .header("text", "test")
            .exchange()
            .expectHeader()
            .valueEquals("text", "TEST");
    }

    @Test
    public void shouldExchangeCookies() {
        startServer(UpperCookieRouter.class);

        String text = getWebClient()
            .get()
            .cookie("text", "test")
            .exchange()
            .map(ClientResponse::cookies)
            .map(cookies -> cookies.getFirst("text"))
            .map(ResponseCookie::getValue)
            .block(Duration.ofSeconds(2));

        assertThat(text).isEqualTo("TEST");
    }

    @Test
    public void shouldGetActuatorHealth() {
        startServer();

        getWebTestClient()
            .get()
            .uri("/actuator/health")
            .exchange()
            .expectBody(String.class)
            .isEqualTo("{\"status\":\"UP\"}");
    }

    @Test
    public void shouldExtractBodyAfterRequestEnded() {
        startServer(UpperBodyRouter.class);

        ClientResponse response = getWebClient()
            .post()
            .syncBody("test")
            .exchange()
            .blockOptional(Duration.ofSeconds(2))
            .orElseThrow(() -> new AssertionError("Did not receive a response"));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK);

        String body = response.bodyToMono(String.class)
            .block(Duration.ofSeconds(2));

        assertThat(body).isEqualTo("TEST");
    }

    @Test
    public void testCorsAnnotation() {
        testCors(AnnotatedCorsController.class);
    }

    @Test
    public void testCorsFilter() {
        testCors(UpperBodyRouter.class, CorsWebFilterConfiguration.class);
    }

    private void testCors(Class<?>... sources) {
        startServer(sources);

        WebTestClient client = getWebTestClient();

        client.options()
            .header(HttpHeaders.ORIGIN, "http://snowdrop.dev")
            .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST")
            .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "TEST")
            .exchange()
            .expectHeader()
            .valueEquals(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://snowdrop.dev")
            .expectHeader()
            .valueEquals(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "POST")
            .expectHeader()
            .valueEquals(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "TEST")
            .expectHeader()
            .valueEquals(HttpHeaders.ACCESS_CONTROL_MAX_AGE, "1000");

        client.post()
            .header(HttpHeaders.ORIGIN, "http://snowdrop.dev")
            .syncBody("test")
            .exchange()
            .expectBody(String.class)
            .isEqualTo("TEST");

        client.post()
            .header(HttpHeaders.ORIGIN, "http://example.com")
            .syncBody("test")
            .exchange()
            .expectStatus()
            .isForbidden();
    }

    @Configuration
    static class StaticRouter {
        @Bean
        public RouterFunction<ServerResponse> staticRouter() {
            return resources("/**", new ClassPathResource("static"));
        }
    }

    @Configuration
    static class NoopRouter {
        @Bean
        public RouterFunction<ServerResponse> noopRouter() {
            return route()
                .GET("/", request -> noContent().build())
                .build();
        }
    }

    @Configuration
    static class UpperBodyRouter {
        @Bean
        public RouterFunction<ServerResponse> upperBodyRouter() {
            return route()
                .POST("/", request -> {
                    Flux<String> body = request.bodyToFlux(String.class)
                        .map(String::toUpperCase);

                    return ok().body(body, String.class);
                })
                .build();
        }
    }

    @Configuration
    static class UpperCookieRouter {
        @Bean
        public RouterFunction<ServerResponse> upperCookieRouter() {
            return route()
                .GET("/", request -> {
                    String text = request.cookies()
                        .getFirst("text")
                        .getValue()
                        .toUpperCase();
                    ResponseCookie cookie = ResponseCookie.from("text", text).build();

                    return noContent().cookie(cookie).build();
                })
                .build();
        }
    }

    @Configuration
    static class UpperHeaderRouter {
        @Bean
        public RouterFunction<ServerResponse> upperHeaderRouter() {
            return route()
                .GET("/", request -> {
                    String text = request.headers()
                        .header("text")
                        .get(0)
                        .toUpperCase();

                    return noContent().header("text", text).build();
                })
                .build();
        }
    }

    @Configuration
    static class CorsWebFilterConfiguration {
        @Bean
        public CorsWebFilter corsWebFilter() {
            CorsConfiguration config = new CorsConfiguration();

            config.addAllowedOrigin("http://snowdrop.dev");
            config.addAllowedHeader("TEST");
            config.addAllowedMethod(HttpMethod.POST);
            config.setMaxAge(1000L);

            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/", config);

            return new CorsWebFilter(source);
        }
    }

    @RestController
    static class AnnotatedCorsController {
        @CrossOrigin(origins = "http://snowdrop.dev", allowedHeaders = "TEST", maxAge = 1000)
        @PostMapping
        public Mono<String> toUpper(@RequestBody String body) {
            return Mono.just(body.toUpperCase());
        }
    }
}
