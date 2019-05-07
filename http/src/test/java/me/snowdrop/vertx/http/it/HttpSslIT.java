package me.snowdrop.vertx.http.it;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.net.ssl.SSLHandshakeException;

import io.vertx.core.http.ClientAuth;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpVersion;
import io.vertx.core.net.JdkSSLEngineOptions;
import io.vertx.core.net.JksOptions;
import io.vertx.core.net.OpenSSLEngineOptions;
import io.vertx.core.net.PemKeyCertOptions;
import me.snowdrop.vertx.http.client.properties.HttpClientOptionsCustomizer;
import me.snowdrop.vertx.http.server.VertxServerHttpRequest;
import me.snowdrop.vertx.http.server.properties.HttpServerOptionsCustomizer;
import org.junit.After;
import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.Assume.assumeTrue;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.noContent;
import static org.springframework.web.reactive.function.server.ServerResponse.status;

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

    private static final String KEY_PATH = "target/test-classes/tls/server-key.pem";

    private static final String CERT_PATH = "target/test-classes/tls/server-cert.pem";

    @After
    public void tearDown() {
        stopServer();
    }

    @Test
    public void testSecureRequest() {
        testSecureRequest(false);
    }

    @Test
    public void testSecureRequestWithAlpn() {
        assumeTrue("Neither OpenSSL nor Java 9 or higher is in a classpath", isOpenSsl() || isJava9());
        testSecureRequest(true);
    }

    @Test
    public void testUntrustedClient() {
        testUntrustedClient(false);
    }

    @Test
    public void testUntrustedClientWithAlpn() {
        assumeTrue("Neither OpenSSL nor Java 9 or higher is in a classpath", isOpenSsl() || isJava9());
        testUntrustedClient(true);
    }

    @Test
    public void testDefaultEngine() {
        testEngine(false, Engine.NONE);
    }

    @Test
    public void testDefaultEngineWithAlpn() {
        assumeTrue("Neither OpenSSL nor Java 9 or higher is in a classpath", isOpenSsl() || isJava9());
        testEngine(true, Engine.NONE);
    }

    @Test
    public void testOpenSslEngine() {
        assumeTrue("OpenSSL is not in a classpath", isOpenSsl());
        testEngine(false, Engine.OPENSSL);
    }

    @Test
    public void testOpenSslEngineWithAlpn() {
        assumeTrue("OpenSSL is not in a classpath", isOpenSsl());
        testEngine(true, Engine.OPENSSL);
    }

    @Test
    public void testJdkEngine() {
        testEngine(false, Engine.JDK);
    }

    @Test
    public void testJdkEngineWithAlpn() {
        assumeTrue("Java 9 or higher is not in a classpath", isJava9());
        testEngine(true, Engine.JDK);
    }

    @Override
    protected boolean isSsl() {
        return true;
    }

    private void testSecureRequest(boolean useAlpn) {
        Properties properties = new Properties();
        properties.setProperty("vertx.http.client.ssl", "true");
        properties.setProperty("vertx.http.client.use-alpn", String.valueOf(useAlpn));
        properties.setProperty("vertx.http.client.protocol-version",
            useAlpn ? HttpVersion.HTTP_2.name() : HttpVersion.HTTP_1_1.name());
        properties.setProperty("vertx.http.server.ssl", "true");
        properties.setProperty("vertx.http.server.useAlpn", Boolean.toString(useAlpn));
        properties.setProperty("vertx.http.server.client-auth", ClientAuth.REQUIRED.name());
        properties.setProperty("server.ssl.key-store-type", "JKS");
        properties.setProperty("server.ssl.key-store", SERVER_KEYSTORE.getPath());
        properties.setProperty("server.ssl.key-store-password", SERVER_KEYSTORE.getPassword());
        properties.setProperty("server.ssl.trust-store-type", "JKS");
        properties.setProperty("server.ssl.trust-store", SERVER_TRUSTSTORE.getPath());
        properties.setProperty("server.ssl.trust-store-password", SERVER_TRUSTSTORE.getPassword());


        startServer(properties, ClientStoresCustomizer.class, useAlpn ? NoopHttp2Router.class : NoopHttp11Router.class);

        getWebTestClient()
            .get()
            .exchange()
            .expectStatus()
            .isNoContent();
    }

    private void testUntrustedClient(boolean useAlpn) {
        Properties properties = new Properties();
        properties.setProperty("vertx.http.client.ssl", "true");
        properties.setProperty("vertx.http.client.use-alpn", String.valueOf(useAlpn));
        properties.setProperty("vertx.http.client.protocol-version",
            useAlpn ? HttpVersion.HTTP_2.name() : HttpVersion.HTTP_1_1.name());
        properties.setProperty("vertx.http.server.ssl", "true");
        properties.setProperty("vertx.http.server.useAlpn", Boolean.toString(useAlpn));
        properties.setProperty("vertx.http.server.client-auth", ClientAuth.REQUIRED.name());
        properties.setProperty("server.ssl.key-store-type", "JKS");
        properties.setProperty("server.ssl.key-store", SERVER_KEYSTORE.getPath());
        properties.setProperty("server.ssl.key-store-password", SERVER_KEYSTORE.getPassword());

        startServer(properties, ClientStoresCustomizer.class, useAlpn ? NoopHttp2Router.class : NoopHttp11Router.class);

        try {
            getWebTestClient()
                .get()
                .exchange();
            fail("SSLHandshakeException expected");
        } catch (RuntimeException e) {
            assertThat(e.getCause()).isInstanceOf(SSLHandshakeException.class);
        }
    }

    private void testEngine(boolean useAlpn, Engine engine) {
        Properties properties = new Properties();
        properties.setProperty("vertx.http.client.ssl", "true");
        properties.setProperty("vertx.http.client.use-alpn", String.valueOf(useAlpn));
        properties.setProperty("vertx.http.client.trust-all", "true");
        properties.setProperty("vertx.http.client.protocol-version",
            useAlpn ? HttpVersion.HTTP_2.name() : HttpVersion.HTTP_1_1.name());
        properties.setProperty("vertx.http.server.ssl", "true");
        properties.setProperty("vertx.http.server.useAlpn", Boolean.toString(useAlpn));

        List<Class> classes = new LinkedList<>();
        classes.add(ServerKeyCertCustomizer.class);
        classes.add(useAlpn ? NoopHttp2Router.class : NoopHttp11Router.class);

        switch (engine) {
            case JDK:
                classes.add(JdkSslEngineOptionsCustomizers.class);
                break;
            case OPENSSL:
                classes.add(OpenSslEngineOptionsCustomizers.class);
        }

        startServer(properties, classes.toArray(new Class[]{}));

        getWebTestClient()
            .get()
            .exchange()
            .expectStatus()
            .isNoContent();
    }

    private boolean isJava9() {
        try {
            HttpSslIT.class.getClassLoader().loadClass("java.lang.invoke.VarHandle");
            return true;
        } catch (Throwable ignore) {
            return false;
        }
    }

    private boolean isOpenSsl() {
        try {
            HttpSslIT.class.getClassLoader().loadClass("io.netty.internal.tcnative.SSL");
            return true;
        } catch (Throwable ignore) {
            return false;
        }
    }

    private enum Engine {
        NONE,
        JDK,
        OPENSSL
    }

    @Configuration
    static class ClientStoresCustomizer {
        @Bean
        public HttpClientOptionsCustomizer clientStoresCustomizer() {
            return options -> options
                .setKeyStoreOptions(CLIENT_KEYSTORE)
                .setTrustStoreOptions(CLIENT_TRUSTSTORE);
        }
    }

    @Configuration
    static class OpenSslEngineOptionsCustomizers {
        @Bean
        public HttpClientOptionsCustomizer clientOpenSslEngineOptionsCustomizer() {
            return options -> options.setSslEngineOptions(new OpenSSLEngineOptions());
        }

        @Bean
        public HttpServerOptionsCustomizer serverOpenSslEngineOptionsCustomizer() {
            return options -> options.setSslEngineOptions(new OpenSSLEngineOptions());
        }
    }

    @Configuration
    static class JdkSslEngineOptionsCustomizers {
        @Bean
        public HttpClientOptionsCustomizer clientJdkSslEngineOptionsCustomizer() {
            return options -> options.setSslEngineOptions(new JdkSSLEngineOptions());
        }

        @Bean
        public HttpServerOptionsCustomizer serverJdkSslEngineOptionsCustomizer() {
            return options -> options.setSslEngineOptions(new JdkSSLEngineOptions());
        }
    }

    @Configuration
    static class ServerKeyCertCustomizer {
        @Bean
        public HttpServerOptionsCustomizer serverKeyCertCustomizer() {
            return options -> {
                PemKeyCertOptions cert = new PemKeyCertOptions()
                    .setKeyPath(KEY_PATH)
                    .setCertPath(CERT_PATH);

                options.setKeyCertOptions(cert);

                return options;
            };
        }
    }

    @Configuration
    static class NoopHttp2Router {
        @Bean
        public RouterFunction<ServerResponse> noopHttp2Router() {
            return route()
                .GET("/", request -> {
                    HttpServerRequest vertxRequest = getHttpServerRequest(request);

                    System.out.println(vertxRequest.sslSession());
                    if (!HttpVersion.HTTP_2.equals(vertxRequest.version())) {
                        return status(HttpStatus.BAD_REQUEST).syncBody("Not HTTP2 request");
                    }
                    if (!vertxRequest.isSSL()) {
                        return status(HttpStatus.BAD_REQUEST).syncBody("Not SSL request");
                    }
                    return noContent().build();
                })
                .build();
        }

        private HttpServerRequest getHttpServerRequest(ServerRequest request) {
            return ((VertxServerHttpRequest) request.exchange().getRequest()).getNativeRequest();
        }
    }

    @Configuration
    static class NoopHttp11Router {
        @Bean
        public RouterFunction<ServerResponse> noopHttp11Router() {
            return route()
                .GET("/", request -> {
                    HttpServerRequest vertxRequest = getHttpServerRequest(request);

                    if (!HttpVersion.HTTP_1_1.equals(vertxRequest.version())) {
                        return status(HttpStatus.BAD_REQUEST).syncBody("Not HTTP1.1 request");
                    }
                    if (!vertxRequest.isSSL()) {
                        return status(HttpStatus.BAD_REQUEST).syncBody("Not SSL request");
                    }
                    return noContent().build();
                })
                .build();
        }

        private HttpServerRequest getHttpServerRequest(ServerRequest request) {
            return ((VertxServerHttpRequest) request.exchange().getRequest()).getNativeRequest();
        }
    }
}
