package dev.snowdrop.vertx.amqp;

import java.util.function.Consumer;

import dev.snowdrop.vertx.streams.WriteStream;
import reactor.core.publisher.Mono;

public interface AmqpSender extends WriteStream<AmqpMessage> {

    AmqpSender exceptionHandler(Consumer<Throwable> handler);

    AmqpSender drainHandler(Consumer<Void> handler);

    AmqpSender setWriteQueueMaxSize(int maxSize);

    AmqpSender send(AmqpMessage message);

    Mono<Void> sendWithAck(AmqpMessage message);

    AmqpConnection connection();

    String address();

    Mono<Void> close();
}
