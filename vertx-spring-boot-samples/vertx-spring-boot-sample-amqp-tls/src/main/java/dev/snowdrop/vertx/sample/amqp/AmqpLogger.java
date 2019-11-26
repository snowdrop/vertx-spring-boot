package dev.snowdrop.vertx.sample.amqp;

import dev.snowdrop.vertx.amqp.AmqpClient;
import dev.snowdrop.vertx.amqp.AmqpMessage;
import dev.snowdrop.vertx.amqp.AmqpSender;
import reactor.core.publisher.Mono;

import static dev.snowdrop.vertx.sample.amqp.AmqpSampleApplication.QUEUE;

final class AmqpLogger {

    private final AmqpClient client;

    AmqpLogger(AmqpClient client) {
        this.client = client;
    }

    public Mono<Void> logMessage(String body) {
        System.out.println("Sending message '" + body + "' to AMQP log");

        AmqpMessage message = AmqpMessage.create()
            .withBody(body)
            .build();

        return client.createSender(QUEUE)
            .map(sender -> sender.send(message))
            .flatMap(AmqpSender::close);
    }
}
