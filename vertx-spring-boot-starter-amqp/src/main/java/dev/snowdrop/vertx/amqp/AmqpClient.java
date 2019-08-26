package dev.snowdrop.vertx.amqp;

import java.util.function.Consumer;

import reactor.core.publisher.Mono;

public interface AmqpClient {

    Mono<AmqpConnection> connect();

    Mono<AmqpSender> createSender(String address);

    Mono<AmqpReceiver> createReceiver(String address);

    Mono<AmqpReceiver> createReceiver(String address, Consumer<AmqpMessage> messageHandler);

    Mono<AmqpReceiver> createReceiver(String address, AmqpReceiverOptions options,
        Consumer<AmqpMessage> messageHandler);

    Mono<Void> close();
}
