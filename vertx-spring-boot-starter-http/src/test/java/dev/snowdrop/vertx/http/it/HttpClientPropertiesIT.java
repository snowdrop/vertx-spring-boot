package dev.snowdrop.vertx.http.it;

import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import dev.snowdrop.vertx.http.client.properties.HttpClientProperties;
import io.vertx.core.http.HttpVersion;
import org.junit.After;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HttpClientPropertiesIT extends TestBase {

    @After
    public void tearDown() {
        stopServer();
    }

    @Test
    public void verifyHttpClientProperties() {
        Properties originalProperties = new Properties();
        originalProperties.setProperty("vertx.http.client.default-host", "localhost");
        originalProperties.setProperty("vertx.http.client.default-port", "8082");
        originalProperties.setProperty("vertx.http.client.protocol-version", "HTTP_2");
        originalProperties.setProperty("vertx.http.client.force-sni", "true");
        originalProperties.setProperty("vertx.http.client.http2-extra-settings.1", "10");
        originalProperties.setProperty("vertx.http.client.http2-extra-settings.2", "20");
        originalProperties.setProperty("vertx.http.client.idle-timeout-unit", "HOURS");
        originalProperties.setProperty("vertx.http.client.enabled-cipher-suites", "cipher1,cipher2");
        startServerWithoutSecurity(originalProperties);

        HttpClientProperties expectedProperties = getBean(HttpClientProperties.class);

        assertThat(expectedProperties.getDefaultPort()).isEqualTo(8082);
        assertThat(expectedProperties.getDefaultHost()).isEqualTo("localhost");
        assertThat(expectedProperties.getProtocolVersion()).isEqualTo(HttpVersion.HTTP_2);
        assertThat(expectedProperties.isForceSni()).isTrue();
        assertThat(expectedProperties.getHttp2ExtraSettings())
            .containsOnly(new HashMap.SimpleEntry<>(1, 10L), new HashMap.SimpleEntry<>(2, 20L));
        assertThat(expectedProperties.getIdleTimeoutUnit()).isEqualTo(TimeUnit.HOURS);
        assertThat(expectedProperties.getEnabledCipherSuites()).containsOnly("cipher1", "cipher2");
    }
}
