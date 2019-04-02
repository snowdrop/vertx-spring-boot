package me.snowdrop.vertx.http.it;

import java.time.Duration;

import io.restassured.path.xml.XmlPath;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClientOptions;
import me.snowdrop.vertx.http.client.VertxClientHttpConnector;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static io.restassured.path.xml.XmlPath.CompatibilityMode.HTML;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.ACCEPT_ENCODING;
import static org.springframework.web.reactive.function.server.RouterFunctions.resources;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.noContent;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@RunWith(SpringRunner.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
    properties = {
        "server.port=" + HttpIT.PORT,
        "server.compression.enabled=true",
        "logging.level.me.snowdrop=DEBUG"
    }
)
public class HttpIT {

    static final int PORT = 8080;

    @Autowired
    private Vertx vertx;

    private WebClient client;

    @Before
    public void setUp() {
        client = WebClient.builder()
            .clientConnector(new VertxClientHttpConnector(vertx))
            .baseUrl("http://localhost:" + PORT)
            .build();
    }

    @Test
    public void shouldGet404Response() {
        Mono<HttpStatus> statusMono = client.get()
            .uri("/wrong")
            .exchange()
            .map(ClientResponse::statusCode);

        StepVerifier.create(statusMono)
            .expectNext(HttpStatus.NOT_FOUND)
            .expectComplete()
            .verify(Duration.ofSeconds(2));
    }

    @Test
    public void shouldGetEmptyResponse() {
        Mono<HttpStatus> statusMono = client.get()
            .uri("/noop")
            .exchange()
            .map(ClientResponse::statusCode);

        StepVerifier.create(statusMono)
            .expectNext(HttpStatus.NO_CONTENT)
            .expectComplete()
            .verify(Duration.ofSeconds(2));
    }

    @Test
    public void shouldExchangeBodies() {
        Flux<String> bodyFlux = client.post()
            .uri("/upper")
            .syncBody("test")
            .retrieve()
            .bodyToFlux(String.class);

        StepVerifier.create(bodyFlux)
            .expectNext("TEST")
            .expectComplete()
            .verify(Duration.ofSeconds(2));
    }

    @Test
    public void shouldExchangeCompressedBodies() {
        HttpClientOptions options = new HttpClientOptions()
            .setTryUseCompression(true);
        WebClient clientWithCompression = WebClient.builder()
            .clientConnector(new VertxClientHttpConnector(vertx, options))
            .baseUrl("http://localhost:" + PORT)
            .build();

        Flux<String> bodyFlux = clientWithCompression.post()
            .uri("/upper")
            .header(ACCEPT_ENCODING, "gzip")
            .syncBody("test")
            .exchange()
            .flatMapMany(response -> response.bodyToFlux(String.class));

        StepVerifier.create(bodyFlux)
            .expectNext("TEST")
            .expectComplete()
            .verify(Duration.ofSeconds(2));
    }

    @Test
    public void shouldGetStaticContent() {
        Mono<XmlPath> xmlMono = client.get()
            .uri("static/index.html")
            .retrieve()
            .bodyToMono(String.class)
            .map(body -> new XmlPath(HTML, body));

        StepVerifier.create(xmlMono)
            .assertNext(xml -> assertThat(xml.getString("html.body.div")).isEqualTo("Test div"))
            .expectComplete()
            .verify(Duration.ofSeconds(2));
    }

    @Test
    public void shouldExchangeHeaders() {
        Mono<String> textMono = client.get()
            .uri("/header")
            .header("text", "test")
            .exchange()
            .map(ClientResponse::headers)
            .map(headers -> headers.header("text"))
            .map(values -> values.get(0));

        StepVerifier.create(textMono)
            .expectNext("TEST")
            .expectComplete()
            .verify(Duration.ofSeconds(2));
    }

    @Test
    public void shouldExchangeCookies() {
        Mono<String> textMono = client.get()
            .uri("/cookie")
            .cookie("text", "test")
            .exchange()
            .map(ClientResponse::cookies)
            .map(cookies -> cookies.getFirst("text"))
            .map(ResponseCookie::getValue);

        StepVerifier.create(textMono)
            .expectNext("TEST")
            .expectComplete()
            .verify(Duration.ofSeconds(2));
    }

    @TestConfiguration
    static class Routers {

        @Bean
        public RouterFunction<ServerResponse> testRouter() {
            return route()
                .GET("/noop", request -> noContent().build())
                .POST("/upper", request -> {
                    Flux<String> body = request.bodyToFlux(String.class)
                        .map(String::toUpperCase);

                    return ok().body(body, String.class);
                })
                .GET("/cookie", request -> {
                    String text = request.cookies()
                        .getFirst("text")
                        .getValue()
                        .toUpperCase();
                    ResponseCookie cookie = ResponseCookie.from("text", text).build();

                    return noContent().cookie(cookie).build();
                })
                .GET("/header", request -> {
                    String text = request.headers()
                        .header("text")
                        .get(0)
                        .toUpperCase();

                    return noContent().header("text", text).build();
                })
                .build();
        }

        @Bean
        public RouterFunction<ServerResponse> staticRouter() {
            return resources("/**", new ClassPathResource("static"));
        }
    }
}
