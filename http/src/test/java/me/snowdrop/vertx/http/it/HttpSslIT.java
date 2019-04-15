package me.snowdrop.vertx.http.it;

import java.time.Duration;
import java.util.Properties;

import javax.net.ssl.SSLHandshakeException;

import io.vertx.core.http.ClientAuth;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.net.JksOptions;
import org.junit.After;
import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.noContent;

public class HttpSslIT extends TestBase {

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

    @After
    public void tearDown() {
        stopServer();
    }

    @Test
    public void shouldMakeSecureRequest() {
        Properties serverProperties = new Properties();
        serverProperties.setProperty("vertx.http.server.ssl", "true");
        serverProperties.setProperty("vertx.http.server.client-auth", ClientAuth.REQUIRED.name());
        serverProperties.setProperty("server.ssl.key-store-type", "JKS");
        serverProperties.setProperty("server.ssl.key-store", SERVER_KEYSTORE.getPath());
        serverProperties.setProperty("server.ssl.key-store-password", SERVER_KEYSTORE.getPassword());
        serverProperties.setProperty("server.ssl.trust-store-type", "JKS");
        serverProperties.setProperty("server.ssl.trust-store", SERVER_TRUSTSTORE.getPath());
        serverProperties.setProperty("server.ssl.trust-store-password", SERVER_TRUSTSTORE.getPassword());

        startServer(serverProperties, NoopRouter.class);

        HttpClientOptions clientOptions = new HttpClientOptions()
            .setSsl(true)
            .setKeyStoreOptions(CLIENT_KEYSTORE)
            .setTrustOptions(CLIENT_TRUSTSTORE);

        HttpStatus status = getWebClient(clientOptions)
            .get()
            .exchange()
            .map(ClientResponse::statusCode)
            .block(Duration.ofSeconds(2));

        assertThat(status).isEqualTo(status);
    }

    @Test
    public void shouldRejectUntrustedClient() {
        Properties serverProperties = new Properties();
        serverProperties.setProperty("vertx.http.server.ssl", "true");
        serverProperties.setProperty("vertx.http.server.client-auth", ClientAuth.REQUIRED.name());
        serverProperties.setProperty("server.ssl.key-store-type", "JKS");
        serverProperties.setProperty("server.ssl.key-store", SERVER_KEYSTORE.getPath());
        serverProperties.setProperty("server.ssl.key-store-password", SERVER_KEYSTORE.getPassword());

        startServer(serverProperties, NoopRouter.class);

        HttpClientOptions options = new HttpClientOptions()
            .setSsl(true)
            .setKeyStoreOptions(CLIENT_KEYSTORE)
            .setTrustOptions(CLIENT_TRUSTSTORE);

        try {
            getWebClient(options)
                .get()
                .exchange()
                .block(Duration.ofSeconds(2));
            fail("SSLHandshakeException expected");
        } catch (RuntimeException e) {
            assertThat(e.getCause()).isInstanceOf(SSLHandshakeException.class);
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
}
