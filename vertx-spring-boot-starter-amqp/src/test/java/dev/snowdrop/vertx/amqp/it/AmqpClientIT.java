package dev.snowdrop.vertx.amqp.it;

import org.apache.activemq.artemis.core.server.embedded.EmbeddedActiveMQ;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AmqpClientIT extends AbstractAmqpClientIT {

    private static EmbeddedActiveMQ BROKER;

    @BeforeAll
    public static void beforeClass() throws Exception {
        BROKER = new EmbeddedActiveMQ().start();
    }

    @AfterAll
    public static void afterClass() throws Exception {
        if (BROKER != null) {
            BROKER.stop();
        }
    }
}
