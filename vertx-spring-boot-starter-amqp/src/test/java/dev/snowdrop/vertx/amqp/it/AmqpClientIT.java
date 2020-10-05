package dev.snowdrop.vertx.amqp.it;

import org.apache.activemq.artemis.core.server.embedded.EmbeddedActiveMQ;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AmqpClientIT extends AbstractAmqpClientIT {

    private static EmbeddedActiveMQ BROKER;

    @BeforeClass
    public static void beforeClass() throws Exception {
        BROKER = new EmbeddedActiveMQ().start();
    }

    @AfterClass
    public static void afterClass() throws Exception {
        if (BROKER != null) {
            BROKER.stop();
        }
    }
}
