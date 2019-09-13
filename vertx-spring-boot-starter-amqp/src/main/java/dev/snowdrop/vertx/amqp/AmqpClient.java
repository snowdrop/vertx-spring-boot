package dev.snowdrop.vertx.amqp;

import reactor.core.publisher.Mono;

public interface AmqpClient {

    Mono<AmqpConnection> connect();

    Mono<AmqpSender> createSender(String address);

    Mono<AmqpSender> createSender(String address, AmqpSenderOptions options);

    Mono<AmqpReceiver> createReceiver(String address);

    Mono<AmqpReceiver> createReceiver(String address, AmqpReceiverOptions options);

    Mono<Void> close();
}
