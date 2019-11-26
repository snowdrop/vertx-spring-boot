package dev.snowdrop.vertx.sample.amqp;

import java.util.HashMap;
import java.util.Map;

import dev.snowdrop.vertx.amqp.AmqpClient;
import dev.snowdrop.vertx.amqp.AmqpProperties;
import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.core.remoting.impl.netty.NettyAcceptorFactory;
import org.apache.activemq.artemis.core.server.embedded.EmbeddedActiveMQ;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.artemis.ArtemisConfigurationCustomizer;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AmqpSampleApplication {

    final static String QUEUE = "messages";

    public static void main(String[] args) {
        SpringApplication.run(AmqpSampleApplication.class, args);
    }

    // Injecting EmbeddedActiveMQ to make sure it has started before creating this bean.
    @Bean
    public AmqpLogger amqpLogger(AmqpClient client, EmbeddedActiveMQ server) {
        return new AmqpLogger(client);
    }

    // Injecting EmbeddedActiveMQ to make sure it has started before creating this bean.
    @Bean
    public AmqpLog amqpLog(AmqpClient client, EmbeddedActiveMQ server) {
        return new AmqpLog(client);
    }

    /**
     * Add Netty acceptor to the embedded Artemis server.
     */
    @Bean
    public ArtemisConfigurationCustomizer artemisConfigurationCustomizer(AmqpProperties properties) {
        Map<String, Object> params = new HashMap<>();
        params.put("host", properties.getHost());
        params.put("port", properties.getPort());
        params.put("sslEnabled", true);
        params.put("needClientAuth", true);
        params.put("keyStorePath", "tls/server-keystore.jks");
        params.put("keyStorePassword", "wibble");
        params.put("trustStorePath", "tls/server-truststore.jks");
        params.put("trustStorePassword", "wibble");

        return configuration -> configuration
            .addAcceptorConfiguration(new TransportConfiguration(NettyAcceptorFactory.class.getName(), params));
    }
}
