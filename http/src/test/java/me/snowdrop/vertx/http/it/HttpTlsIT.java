package me.snowdrop.vertx.http.it;

import java.time.Duration;

import javax.net.ssl.SSLHandshakeException;

import io.vertx.core.Vertx;
import io.vertx.core.http.ClientAuth;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.net.JksOptions;
import me.snowdrop.vertx.http.client.VertxClientHttpConnector;
import me.snowdrop.vertx.http.server.properties.HttpServerOptionsCustomizer;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.noContent;

@Category(FastTests.class)
@RunWith(SpringRunner.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
    properties = "server.port=" + Ports.HTTP_TLS_IT
)
public class HttpTlsIT {

    private static final JksOptions SERVER_KEYSTORE = new JksOptions()
        .setPath("target/test-classes/tls/server-keystore.jks")
        .setPassword("wibble");

    private static final JksOptions SERVER_TRUSTSTORE = new JksOptions()
        .setPath("target/test-classes/tls/server-truststore.jks")
        .setPassword("wibble");

    private static final JksOptions CLIENT_KEYSTORE = new JksOptions()
        .setPath("target/test-classes/tls/client-keystore.jks")
        .setPassword("wibble");

    private static final JksOptions CLIENT_TRUSTSTORE = new JksOptions()
        .setPath("target/test-classes/tls/client-truststore.jks")
        .setPassword("wibble");

    private static final String BASE_URL = String.format("https://localhost:%d", Ports.HTTP_TLS_IT);

    @Autowired
    private Vertx vertx;

    @Test
    public void shouldMakeSecureRequest() {
        HttpClientOptions options = new HttpClientOptions()
            .setSsl(true)
            .setKeyStoreOptions(CLIENT_KEYSTORE)
            .setTrustOptions(CLIENT_TRUSTSTORE);
        WebClient client = WebClient.builder()
            .clientConnector(new VertxClientHttpConnector(vertx, options))
            .baseUrl(BASE_URL)
            .build();

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
    public void shouldRejectUntrustedClient() {
        HttpClientOptions options = new HttpClientOptions()
            .setSsl(true)
            .setKeyStoreOptions(SERVER_KEYSTORE) // Use different keystore than the one expected by the server
            .setTrustOptions(CLIENT_TRUSTSTORE);
        WebClient client = WebClient.builder()
            .clientConnector(new VertxClientHttpConnector(vertx, options))
            .baseUrl(BASE_URL)
            .build();

        Mono<ClientResponse> responseMono = client.get()
            .uri("/noop")
            .exchange();

        StepVerifier.create(responseMono)
            .expectError(SSLHandshakeException.class)
            .verify(Duration.ofSeconds(2));
    }

    @TestConfiguration
    static class Beans {

        @Bean
        public RouterFunction<ServerResponse> testRouter() {
            return route()
                .GET("/noop", request -> noContent().build())
                .build();
        }

        @Bean
        public HttpServerOptionsCustomizer tlsCustomizer() {
            return options -> options.setSsl(true)
                .setClientAuth(ClientAuth.REQUIRED)
                .setKeyStoreOptions(SERVER_KEYSTORE)
                .setTrustOptions(SERVER_TRUSTSTORE);
        }
    }
}
