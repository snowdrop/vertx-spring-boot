package me.snowdrop.vertx.http;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import javax.net.ssl.SSLHandshakeException;

import io.vertx.core.Vertx;
import io.vertx.core.http.ClientAuth;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.net.JksOptions;
import me.snowdrop.vertx.http.properties.HttpServerOptionsCustomizer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.fail;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.noContent;

@RunWith(SpringRunner.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
    properties = "server.port=" + HttpServerTlsIT.PORT
)
public class HttpServerTlsIT {

    static final int PORT = 8081;

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

    @Autowired
    private Vertx vertx;

    @Test
    public void shouldMakeSecureRequest() {
        HttpClientOptions options = new HttpClientOptions()
            .setDefaultPort(PORT)
            .setSsl(true)
            .setKeyStoreOptions(CLIENT_KEYSTORE)
            .setTrustOptions(CLIENT_TRUSTSTORE);
        HttpClient client = vertx.createHttpClient(options);

        AtomicInteger expectedStatus = new AtomicInteger();
        client.getNow("/noop", response -> expectedStatus.set(response.statusCode()));

        await("Wait for the response with status 204")
            .atMost(2, SECONDS)
            .untilAtomic(expectedStatus, equalTo(204));
    }

    @Test
    public void shouldRejectUntrustedClient() {
        HttpClientOptions options = new HttpClientOptions()
            .setDefaultPort(PORT)
            .setSsl(true)
            .setKeyStoreOptions(SERVER_KEYSTORE) // Use different keystore than the one expected by the server
            .setTrustOptions(CLIENT_TRUSTSTORE);
        HttpClient client = vertx.createHttpClient(options);

        AtomicReference<Throwable> expectedException = new AtomicReference<>();

        client.get("/noop")
            .handler(response -> fail("SSL handshake exception expected"))
            .exceptionHandler(expectedException::set)
            .end();

        await("Wait for the SSLHandshakeException")
            .atMost(2, SECONDS)
            .untilAtomic(expectedException, instanceOf(SSLHandshakeException.class));
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
