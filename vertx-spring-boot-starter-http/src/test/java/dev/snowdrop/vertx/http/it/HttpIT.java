package dev.snowdrop.vertx.http.it;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.WebSession;
import org.springframework.web.server.adapter.ForwardedHeaderTransformer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.springframework.http.HttpHeaders.ACCEPT_ENCODING;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
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
        startServerWithoutSecurity();

        getWebTestClient()
            .get()
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    public void shouldGetEmptyResponse() {
        startServerWithoutSecurity(NoopRouter.class);

        getWebTestClient()
            .get()
            .exchange()
            .expectStatus()
            .isNoContent();
    }

    @Test
    public void shouldExchangeBodies() {
        startServerWithoutSecurity(UpperBodyRouter.class);

        getWebTestClient()
            .post()
            .syncBody("test")
            .exchange()
            .expectBody(String.class)
            .isEqualTo("TEST");
    }

    @Test
    public void shouldGetStaticContent() {
        startServerWithoutSecurity(StaticRouter.class);

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
        startServerWithoutSecurity(properties, StaticRouter.class);

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
        startServerWithoutSecurity(UpperHeaderRouter.class);

        getWebTestClient()
            .get()
            .header("text", "test")
            .exchange()
            .expectHeader()
            .valueEquals("text", "TEST");
    }

    @Test
    public void shouldExchangeCookies() {
        startServerWithoutSecurity(UpperCookieRouter.class);

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
        startServerWithoutSecurity();

        getWebTestClient()
            .get()
            .uri("/actuator/health")
            .exchange()
            .expectBody(String.class)
            .isEqualTo("{\"status\":\"UP\"}");
    }

    @Test
    public void shouldExtractBodyAfterRequestEnded() {
        startServerWithoutSecurity(UpperBodyRouter.class);

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
    public void testBasicAuth() {
        startServer(SessionController.class, AuthConfiguration.class);

        getWebTestClient()
            .get()
            .exchange()
            .expectStatus()
            .isUnauthorized();

        String authHash = Base64Utils.encodeToString("user:password".getBytes(StandardCharsets.UTF_8));

        getWebTestClient()
            .get()
            .header(HttpHeaders.AUTHORIZATION, "Basic " + authHash)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(String.class)
            .value(not(isEmptyOrNullString()));
    }

    @Test
    public void testFormData() {
        startServerWithoutSecurity(UpperFormRouter.class);

        getWebTestClient()
            .post()
            .body(BodyInserters.fromFormData("text", "test"))
            .exchange()
            .expectBody(String.class)
            .isEqualTo("TEST");
    }

    @Test
    public void testForwardedHeaders() {
        startServerWithoutSecurity(ForwardedHeadersRouter.class);

        getWebTestClient()
            .get()
            .header("Forwarded", "host=127.0.0.1:1234;proto=http")
            .exchange()
            .expectBody(String.class)
            .isEqualTo("http://127.0.0.1:1234/");

        getWebTestClient()
            .get()
            .header("X-Forwarded-Host", "127.0.0.2")
            .header("X-Forwarded-Port", "4321")
            .header("X-Forwarded-Proto", "https")
            .exchange()
            .expectBody(String.class)
            .isEqualTo("https://127.0.0.2:4321/");
    }

    @Test
    public void testSse() {
        startServerWithoutSecurity(SseController.class);

        getWebTestClient()
            .get()
            .exchange()
            .returnResult(String.class)
            .consumeWith(result -> StepVerifier.create(result.getResponseBody())
                .expectNext("first")
                .expectNext("second")
                .expectNext("third")
                .verifyComplete()
            );
    }

    @Test
    public void testClientWithInfiniteSse() {
        Properties properties = new Properties();
        properties.setProperty("vertx.http.client.max-pool-size", "1");
        startServerWithoutSecurity(properties, InfiniteSseController.class);

        Flux<Long> firstFlux = getWebClient()
            .get()
            .retrieve()
            .bodyToFlux(Long.class)
            .log("first client");
        Flux<Long> secondFlux = getWebClient()
            .get()
            .retrieve()
            .bodyToFlux(Long.class)
            .log("second client");

        StepVerifier.create(firstFlux)
            .expectNext(0L)
            .expectNext(1L)
            .thenCancel()
            .verify();
        StepVerifier.create(secondFlux)
            .expectNext(0L)
            .expectNext(1L)
            .thenCancel()
            .verify();
    }

    @Test
    public void testExceptionHandler() {
        startServerWithoutSecurity(ExceptionController.class);

        getWebTestClient()
            .get()
            .exchange()
            .expectStatus()
            .isNoContent();
    }

    @Test
    public void testCache() {
        startServerWithoutSecurity(CacheController.class);

        getWebTestClient()
            .get()
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .valueEquals(HttpHeaders.ETAG, "\"test\"")
            .expectHeader()
            .cacheControl(CacheControl.maxAge(1, TimeUnit.MINUTES))
            .expectBody(String.class)
            .isEqualTo("test");

        getWebTestClient()
            .get()
            .header(HttpHeaders.IF_NONE_MATCH, "\"test\"")
            .exchange()
            .expectStatus()
            .isNotModified();
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
        startServerWithoutSecurity(sources);

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
    static class UpperFormRouter {
        @Bean
        public RouterFunction<ServerResponse> upperFormRouter() {
            return route()
                .POST("/", accept(MediaType.APPLICATION_FORM_URLENCODED), request -> {
                    Mono<String> body = request.exchange().getFormData()
                        .map(map -> map.get("text"))
                        .map(list -> list.get(0))
                        .map(String::toUpperCase);

                    return ok().body(body, String.class);
                })
                .build();
        }
    }

    @Configuration
    static class ForwardedHeadersRouter {
        @Bean
        public RouterFunction<ServerResponse> forwardedHeadersRouter() {
            return route()
                .GET("/", request -> ok().syncBody(request.exchange().getRequest().getURI().toASCIIString()))
                .build();
        }

        @Bean
        public ForwardedHeaderTransformer forwardedHeaderTransformer() {
            return new ForwardedHeaderTransformer();
        }
    }

    @RestController
    static class SseController {
        @GetMapping
        public Flux<ServerSentEvent<String>> sse() {
            return Flux.just("first", "second", "third")
                .map(s -> ServerSentEvent.<String>builder()
                    .data(s)
                    .build());
        }
    }

    @RestController
    static class InfiniteSseController {
        @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
        public Flux<Long> infiniteSse() {
            return Flux.interval(Duration.ofSeconds(1))
                .log(InfiniteSseController.class.getSimpleName());
        }
    }

    @RestController
    static class ExceptionController implements WebFluxConfigurer {
        @GetMapping
        public Flux<String> exceptionController() {
            throw new RuntimeException("test");
        }

        @ExceptionHandler
        public ResponseEntity<String> runtimeExceptionHandler(RuntimeException e) {
            if ("test".equals(e.getMessage())) {
                return ResponseEntity.noContent().build();
            }
            throw e;
        }
    }

    @RestController
    static class CacheController {
        @GetMapping
        public ResponseEntity<String> getString() {
            return ResponseEntity
                .ok()
                .cacheControl(CacheControl.maxAge(1, TimeUnit.MINUTES))
                .eTag("test")
                .body("test");
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

    @RestController
    static class SessionController {
        @GetMapping
        public Mono<String> getSessionId(WebSession session) {
            return Mono.just(session.getId());
        }
    }

    @Configuration
    static class AuthConfiguration {
        @Bean
        public MapReactiveUserDetailsService userDetailsService() {
            UserDetails user = User.withDefaultPasswordEncoder()
                .username("user")
                .password("password")
                .roles("USER")
                .build();
            return new MapReactiveUserDetailsService(user);
        }
    }
}
