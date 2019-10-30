package dev.snowdrop.vertx.sample.amqp;

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
 * Uppercase processor subscribes to the requests queue, converts each received message to uppercase and send it to the
 * results queue.
 */
@Component
public class UppercaseProcessor implements InitializingBean, DisposableBean {

    private final Logger logger = LoggerFactory.getLogger(UppercaseProcessor.class);

    private final AmqpClient client;

    private Disposable receiverDisposer;

    // Injecting EmbeddedActiveMQ to make sure it has started before creating this component.
    public UppercaseProcessor(AmqpClient client, EmbeddedActiveMQ server) {
        this.client = client;
    }

    /**
     * Create a processing requests receiver and subscribe to its messages publisher.
     */
    @Override
    public void afterPropertiesSet() {
        receiverDisposer = client.createReceiver(PROCESSING_REQUESTS_QUEUE)
            .flatMapMany(receiver -> receiver.flux()
                .doOnCancel(() -> receiver.close().block())) // Close the receiver once subscription is disposed
            .flatMap(this::handleMessage)
            .subscribe();
    }

    /**
     * Cancel processing requests publisher subscription.
     */
    @Override
    public void destroy() {
        if (receiverDisposer != null) {
            receiverDisposer.dispose();
        }
    }

    /**
     * Convert message body to an uppercase and send it to a results queue.
     */
    private Mono<Void> handleMessage(AmqpMessage originalMessage) {
        logger.info("Processing '{}'", originalMessage.bodyAsString());

        AmqpMessage processedMessage = AmqpMessage.create()
            .withBody(originalMessage.bodyAsString().toUpperCase())
            .build();

        return client.createSender(PROCESSING_RESULTS_QUEUE)
            .map(sender -> sender.send(processedMessage))
            .flatMap(AmqpSender::close);
    }
}
