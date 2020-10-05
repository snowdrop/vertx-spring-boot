package dev.snowdrop.vertx.sample.amqp;

import dev.snowdrop.vertx.amqp.AmqpClient;
import org.apache.activemq.artemis.core.server.embedded.EmbeddedActiveMQ;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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

    @Bean(destroyMethod = "stop")
    public EmbeddedActiveMQ embeddedBroker() throws Exception {
        return new EmbeddedActiveMQ().start();
    }
}
