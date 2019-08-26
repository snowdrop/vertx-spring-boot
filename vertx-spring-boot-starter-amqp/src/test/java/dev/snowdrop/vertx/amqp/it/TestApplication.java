package dev.snowdrop.vertx.amqp.it;

import java.util.HashMap;
import java.util.Map;

import dev.snowdrop.vertx.amqp.AmqpProperties;
import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.core.remoting.impl.netty.NettyAcceptorFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.artemis.ArtemisConfigurationCustomizer;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

    @Bean
    public ArtemisConfigurationCustomizer artemisConfigurationCustomizer(AmqpProperties properties) {
        Map<String, Object> params = new HashMap<>();
        params.put("host", properties.getHost());
        params.put("port", properties.getPort());

        if (properties.isSsl()) {
            params.put("sslEnabled", true);
            params.put("keyStorePath", SslConstants.SERVER_KEYSTORE_PATH);
            params.put("keyStorePassword", SslConstants.SERVER_KEYSTORE_PASSWORD);
            params.put("trustStorePath", SslConstants.SERVER_TRUSTSTORE_PATH);
            params.put("trustStorePassword", SslConstants.SERVER_TRUSTSTORE_PASSWORD);
            params.put("enabledProtocols", String.join(",", properties.getEnabledSecureTransportProtocols()));
        }

        return configuration -> configuration
            .addAcceptorConfiguration(new TransportConfiguration(NettyAcceptorFactory.class.getName(), params));
    }
}
