package dev.snowdrop.vertx.amqp;

import java.util.function.Consumer;

import reactor.core.publisher.Mono;

public interface AmqpConnection {

    AmqpConnection exceptionHandler(Consumer<Throwable> handler);

    Mono<AmqpSender> createSender(String address);

    Mono<AmqpSender> createSender(String address, AmqpSenderOptions options);

    Mono<AmqpSender> createAnonymousSender();

    Mono<AmqpReceiver> createReceiver(String address);

    Mono<AmqpReceiver> createReceiver(String address, Consumer<AmqpMessage> messageHandler);

    Mono<AmqpReceiver> createReceiver(String address, AmqpReceiverOptions options,
        Consumer<AmqpMessage> messageHandler);

    Mono<AmqpReceiver> createDynamicReceiver();

    Mono<Void> close();
}
