package dev.snowdrop.vertx.amqp.it;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {
    "vertx.amqp.port=61616",
    "vertx.amqp.ssl=true",
    "vertx.amqp.jks-key-store.enabled=true",
    "vertx.amqp.jks-key-store.path=" + SslConstants.CLIENT_KEYSTORE_PATH,
    "vertx.amqp.jks-key-store.password=" + SslConstants.CLIENT_KEYSTORE_PASSWORD,
    "vertx.amqp.jks-trust-store.enabled=true",
    "vertx.amqp.jks-trust-store.path=" + SslConstants.CLIENT_TRUSTSTORE_PATH,
    "vertx.amqp.jks-trust-store.password=" + SslConstants.CLIENT_TRUSTSTORE_PASSWORD,
    "vertx.amqp.jdk-ssl-engine.enabled=true"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class AmqpClientSslIT extends AmqpClientIT {

}
