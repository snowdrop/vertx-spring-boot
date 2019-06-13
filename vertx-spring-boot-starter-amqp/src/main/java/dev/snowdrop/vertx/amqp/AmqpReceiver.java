package dev.snowdrop.vertx.amqp;

import dev.snowdrop.vertx.streams.ReadStream;
import reactor.core.publisher.Mono;

public interface AmqpReceiver extends ReadStream<AmqpMessage> {

    AmqpConnection connection();

    String address();

    Mono<Void> close();
}
