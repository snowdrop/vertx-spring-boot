package dev.snowdrop.vertx.sample.amqp;

import org.apache.activemq.artemis.core.server.embedded.EmbeddedActiveMQ;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AmqpSampleApplication {

    final static String PROCESSING_REQUESTS_QUEUE = "processing-requests";

    final static String PROCESSING_RESULTS_QUEUE = "processing-results";

    public static void main(String[] args) {
        SpringApplication.run(AmqpSampleApplication.class, args);
    }

    @Bean(destroyMethod = "stop")
    public EmbeddedActiveMQ embeddedBroker() throws Exception {
        return new EmbeddedActiveMQ().start();
    }
}
