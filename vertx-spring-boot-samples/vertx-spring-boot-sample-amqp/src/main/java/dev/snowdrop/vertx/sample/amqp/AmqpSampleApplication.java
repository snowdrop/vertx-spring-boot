package dev.snowdrop.vertx.sample.amqp;

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
public class AmqpSampleApplication {

    final static String PROCESSING_REQUESTS_QUEUE = "processing-requests";

    final static String PROCESSING_RESULTS_QUEUE = "processing-results";

    public static void main(String[] args) {
        SpringApplication.run(AmqpSampleApplication.class, args);
    }

    /**
     * Add Netty acceptor to the embedded Artemis server.
     */
    @Bean
    public ArtemisConfigurationCustomizer artemisConfigurationCustomizer(AmqpProperties properties) {
        Map<String, Object> params = new HashMap<>();
        params.put("host", properties.getHost());
        params.put("port", properties.getPort());

        return configuration -> configuration
            .addAcceptorConfiguration(new TransportConfiguration(NettyAcceptorFactory.class.getName(), params));
    }
}
