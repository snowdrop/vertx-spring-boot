package dev.snowdrop.vertx.amqp.it;

import org.apache.activemq.artemis.core.server.embedded.EmbeddedActiveMQ;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
    "vertx.amqp.ssl=true",
    "vertx.amqp.jks-key-store.enabled=true",
    "vertx.amqp.jks-key-store.path=target/test-classes/tls/client-keystore.jks",
    "vertx.amqp.jks-key-store.password=wibble",
    "vertx.amqp.jks-trust-store.enabled=true",
    "vertx.amqp.jks-trust-store.path=target/test-classes/tls/client-truststore.jks",
    "vertx.amqp.jks-trust-store.password=wibble",
    "vertx.amqp.jdk-ssl-engine.enabled=true"
})
public class AmqpClientSslIT extends AbstractAmqpClientIT {

    private static EmbeddedActiveMQ BROKER;

    @BeforeAll
    public static void beforeClass() throws Exception {
        BROKER = new EmbeddedActiveMQ().setConfigResourcePath("tls-broker.xml").start();
    }

    @AfterAll
    public static void afterClass() throws Exception {
        if (BROKER != null) {
            BROKER.stop();
        }
    }
}
