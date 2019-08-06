package dev.snowdrop.vertx.sample.amqp;

import java.util.HashMap;
import java.util.Map;

import dev.snowdrop.vertx.amqp.AmqpClient;
import dev.snowdrop.vertx.amqp.AmqpProperties;
import dev.snowdrop.vertx.sample.amqp.client.ProcessorClient;
import dev.snowdrop.vertx.sample.amqp.server.UppercaseProcessor;
import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.core.remoting.impl.netty.NettyAcceptorFactory;
import org.apache.activemq.artemis.jms.server.embedded.EmbeddedJMS;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.artemis.ArtemisConfigurationCustomizer;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AmqpSampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(AmqpSampleApplication.class, args);
    }

    /**
     * Instantiate processor service.
     *
     * @param client AmqpClient used to access message broker.
     * @param server Embedded Artemis server instance to make sure that it has starter before this bean instantiation.
     * @return
     */
    @Bean
    public ProcessorClient processorService(AmqpClient client, EmbeddedJMS server) {
        return new ProcessorClient(client);
    }

    /**
     * Instantiate uppercase processor.
     *
     * @param client AmqpClient used to access message broker.
     * @param server Embedded Artemis server instance to make sure that it has starter before this bean instantiation.
     * @return
     */
    @Bean
    public UppercaseProcessor uppercaseProcessor(AmqpClient client, EmbeddedJMS server) {
        return new UppercaseProcessor(client);
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
