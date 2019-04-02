package me.snowdrop.vertx.http.properties;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import io.vertx.core.http.ClientAuth;
import io.vertx.core.http.HttpVersion;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {
    "vertx.http.server.port=1234",
    "vertx.http.server.host=example.com",
    "vertx.http.server.client-auth=REQUIRED",
    "vertx.http.server.sni=true",
    "vertx.http.server.alpn-versions=HTTP_1_1,HTTP_2",
    "vertx.http.server.http2-extra-settings.1=10",
    "vertx.http.server.http2-extra-settings.2=20",
    "vertx.http.server.idle-timeout-unit=HOURS",
    "vertx.http.server.enabled-cipher-suites=cipher1,cipher2"
})
public class HttpServerPropertiesIT {

    @Autowired
    private HttpServerProperties properties;

    @Test
    public void verifyHttpServerProperties() {
        assertThat(properties.getPort()).isEqualTo(1234);
        assertThat(properties.getHost()).isEqualTo("example.com");
        assertThat(properties.getClientAuth()).isEqualTo(ClientAuth.REQUIRED);
        assertThat(properties.isSni()).isTrue();
        assertThat(properties.getAlpnVersions())
            .containsOnly(HttpVersion.HTTP_1_1, HttpVersion.HTTP_2);
        assertThat(properties.getHttp2ExtraSettings())
            .containsOnly(new HashMap.SimpleEntry<>(1, 10L), new HashMap.SimpleEntry<>(2, 20L));
        assertThat(properties.getIdleTimeoutUnit()).isEqualTo(TimeUnit.HOURS);
        assertThat(properties.getEnabledCipherSuites())
            .containsOnly("cipher1", "cipher2");
    }
}
