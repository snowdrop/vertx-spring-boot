package dev.snowdrop.vertx.sample.amqp.server;

import java.time.Duration;

import dev.snowdrop.vertx.amqp.AmqpClient;
import dev.snowdrop.vertx.amqp.AmqpMessage;
import dev.snowdrop.vertx.amqp.AmqpReceiver;
import dev.snowdrop.vertx.amqp.AmqpSender;
import dev.snowdrop.vertx.sample.amqp.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import reactor.core.publisher.Mono;

/**
 * Uppercase processor subscribes to the requests queue, converts each received message to uppercase and send it to the
 * results queue.
 */
public class UppercaseProcessor implements InitializingBean, DisposableBean {

    private final Logger logger = LoggerFactory.getLogger(UppercaseProcessor.class);

    private final AmqpClient client;

    private AmqpSender sender;

    private AmqpReceiver receiver;

    public UppercaseProcessor(AmqpClient client) {
        this.client = client;
    }

    @Override
    public void afterPropertiesSet() {
        sender = initSender();
        receiver = initReceiver();
    }

    @Override
    public void destroy() {
        Mono.zip(sender.close(), receiver.close()).block();
    }

    private AmqpSender initSender() {
        // Create a sender or fail if unable to connect in 2 seconds
        return client.createSender(Constants.PROCESSING_RESULTS_QUEUE)
            .blockOptional(Duration.ofSeconds(2))
            .orElseThrow(() -> new RuntimeException("Unable to create a sender"));
    }

    private AmqpReceiver initReceiver() {
        // Create a receiver or fail if unable to connect in 2 seconds
        return client.createReceiver(Constants.PROCESSING_REQUESTS_QUEUE, this::handleMessage)
            .blockOptional(Duration.ofSeconds(2))
            .orElseThrow(() -> new RuntimeException("Unable to create a receiver"));
    }

    /**
     * Convert message body to uppercase and send to results queue.
     */
    private void handleMessage(AmqpMessage originalMessage) {
        logger.info("Processing '{}'", originalMessage.bodyAsString());

        AmqpMessage processedMessage = AmqpMessage.create()
            .address(Constants.PROCESSING_RESULTS_QUEUE)
            .withBody(originalMessage.bodyAsString().toUpperCase())
            .build();
        sender.send(processedMessage);
    }
}
