package dev.snowdrop.vertx.sample.amqp;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import dev.snowdrop.vertx.amqp.AmqpClient;
import dev.snowdrop.vertx.amqp.AmqpMessage;
import dev.snowdrop.vertx.amqp.AmqpSender;
import org.apache.activemq.artemis.core.server.embedded.EmbeddedActiveMQ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

import static dev.snowdrop.vertx.sample.amqp.AmqpSampleApplication.PROCESSING_REQUESTS_QUEUE;
import static dev.snowdrop.vertx.sample.amqp.AmqpSampleApplication.PROCESSING_RESULTS_QUEUE;

/**
 * Processor client submits messages to the requests queue and subscribes to the results queue for processed messages.
 */
@Component
public class MessagesManager implements InitializingBean, DisposableBean {

    private final Logger logger = LoggerFactory.getLogger(MessagesManager.class);

    private final List<String> processedMessages = new CopyOnWriteArrayList<>();

    private final AmqpClient client;

    private Disposable receiverDisposer;

    // Injecting EmbeddedActiveMQ to make sure it has started before creating this component.
    public MessagesManager(AmqpClient client, EmbeddedActiveMQ server) {
        this.client = client;
    }

    /**
     * Create a processed messages receiver and subscribe to its messages publisher.
     */
    @Override
    public void afterPropertiesSet() {
        receiverDisposer = client.createReceiver(PROCESSING_RESULTS_QUEUE)
            .flatMapMany(receiver -> receiver.flux()
                .doOnCancel(() -> receiver.close().block())) // Close the receiver once subscription is disposed
            .subscribe(this::handleMessage);
    }

    /**
     * Cancel processed messages publisher subscription.
     */
    @Override
    public void destroy() {
        if (receiverDisposer != null) {
            receiverDisposer.dispose();
        }
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
     * Submit a message for processing by publishing it to a processing requests queue.
     *
     * @param body Message body to be processed.
     * @return Mono which is completed once message is sent.
     */
    public Mono<Void> processMessage(String body) {
        logger.info("Sending message '{}' for processing", body);

        AmqpMessage message = AmqpMessage.create()
            .withBody(body)
            .build();

        return client.createSender(PROCESSING_REQUESTS_QUEUE)
            .map(sender -> sender.send(message))
            .flatMap(AmqpSender::close);
    }

    private void handleMessage(AmqpMessage message) {
        String body = message.bodyAsString();

        logger.info("Received processed message '{}'", body);
        processedMessages.add(body);
    }
}
