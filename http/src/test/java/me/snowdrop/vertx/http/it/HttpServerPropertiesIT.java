package me.snowdrop.vertx.http.it;

import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import io.vertx.core.http.ClientAuth;
import io.vertx.core.http.HttpVersion;
import me.snowdrop.vertx.http.server.properties.HttpServerProperties;
import org.junit.After;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HttpServerPropertiesIT extends TestBase {

    @After
    public void tearDown() {
        stopServer();
    }

    @Test
    public void verifyHttpServerProperties() {
        Properties originalProperties = new Properties();
        originalProperties.setProperty("vertx.http.server.host", "localhost");
        originalProperties.setProperty("vertx.http.server.port", "8082");
        originalProperties.setProperty("vertx.http.server.client-auth", "REQUIRED");
        originalProperties.setProperty("vertx.http.server.sni", "true");
        originalProperties.setProperty("vertx.http.server.alpn-versions", "HTTP_1_1,HTTP_2");
        originalProperties.setProperty("vertx.http.server.http2-extra-settings.1", "10");
        originalProperties.setProperty("vertx.http.server.http2-extra-settings.2", "20");
        originalProperties.setProperty("vertx.http.server.idle-timeout-unit", "HOURS");
        originalProperties.setProperty("vertx.http.server.enabled-cipher-suites", "cipher1,cipher2");
        startServerWithoutSecurity(originalProperties);

        HttpServerProperties expectedProperties = getBean(HttpServerProperties.class);

        assertThat(expectedProperties.getPort()).isEqualTo(8082);
        assertThat(expectedProperties.getHost()).isEqualTo("localhost");
        assertThat(expectedProperties.getClientAuth()).isEqualTo(ClientAuth.REQUIRED);
        assertThat(expectedProperties.isSni()).isTrue();
        assertThat(expectedProperties.getAlpnVersions()).containsOnly(HttpVersion.HTTP_1_1, HttpVersion.HTTP_2);
        assertThat(expectedProperties.getHttp2ExtraSettings())
            .containsOnly(new HashMap.SimpleEntry<>(1, 10L), new HashMap.SimpleEntry<>(2, 20L));
        assertThat(expectedProperties.getIdleTimeoutUnit()).isEqualTo(TimeUnit.HOURS);
        assertThat(expectedProperties.getEnabledCipherSuites()).containsOnly("cipher1", "cipher2");
    }
}
