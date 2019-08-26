package dev.snowdrop.vertx.sample.amqp.client;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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
 * Processor client submits messages to the requests queue and subscribes to the results queue for processed messages.
 */
public class ProcessorClient implements InitializingBean, DisposableBean {

    private final Logger logger = LoggerFactory.getLogger(ProcessorClient.class);

    private final List<String> processedMessages = new CopyOnWriteArrayList<>();

    private final AmqpClient client;

    private AmqpSender sender;

    private AmqpReceiver receiver;

    public ProcessorClient(AmqpClient client) {
        this.client = client;
    }

    /**
     * Get messages which were processed up to this moment.
     *
     * @return List of processed messages.
     */
    public List<String> getProcessedMessages() {
        return processedMessages;
    }

    /**
     * Send message to the requests queue.
     *
     * @param body Message body to be processed.
     * @return Mono which is completed once message is acknowledged.
     */
    public Mono<Void> processMessage(String body) {
        logger.info("Sending message '{}' for processing", body);

        AmqpMessage message = AmqpMessage.create()
            .withBody(body)
            .build();

        return sender.sendWithAck(message);
    }

    /**
     * Initialise sender and receiver.
     */
    @Override
    public void afterPropertiesSet() {
        sender = initSender();
        receiver = initReceiver();
    }

    /**
     * Close sender and receiver.
     */
    @Override
    public void destroy() {
        Mono.zip(sender.close(), receiver.close()).block();
    }

    private AmqpSender initSender() {
        // Create a sender or fail if unable to connect in 2 seconds
        return client.createSender(Constants.PROCESSING_REQUESTS_QUEUE)
            .blockOptional(Duration.ofSeconds(2))
            .orElseThrow(() -> new RuntimeException("Unable to create a sender"));
    }

    private AmqpReceiver initReceiver() {
        // Create a receiver or fail if unable to connect in 2 seconds
        return client.createReceiver(Constants.PROCESSING_RESULTS_QUEUE, this::handleMessage)
            .blockOptional(Duration.ofSeconds(2))
            .orElseThrow(() -> new RuntimeException("Unable to create a receiver"));
    }

    private void handleMessage(AmqpMessage message) {
        logger.info("Received processed message '{}'", message.bodyAsString());

        processedMessages.add(message.bodyAsString());
    }
}
