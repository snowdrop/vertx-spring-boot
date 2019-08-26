package dev.snowdrop.vertx.amqp;

import java.util.function.Consumer;

import reactor.core.publisher.Mono;

class SnowdropAmqpSender implements AmqpSender {

    private final io.vertx.axle.amqp.AmqpSender delegate;

    private final MessageConverter messageConverter;

    SnowdropAmqpSender(io.vertx.axle.amqp.AmqpSender delegate, MessageConverter messageConverter) {
        this.delegate = delegate;
        this.messageConverter = messageConverter;
    }

    @Override
    public io.vertx.core.streams.WriteStream vertxWriteStream() {
        return delegate.getDelegate();
    }

    @Override
    public AmqpSender exceptionHandler(Consumer<Throwable> handler) {
        delegate.exceptionHandler(handler);

        return this;
    }

    @Override
    public AmqpSender drainHandler(Consumer<Void> handler) {
        delegate.drainHandler(handler);

        return this;
    }

    @Override
    public AmqpSender setWriteQueueMaxSize(int maxSize) {
        delegate.setWriteQueueMaxSize(maxSize);

        return this;
    }

    @Override
    public boolean writeQueueFull() {
        return delegate.writeQueueFull();
    }

    @Override
    public Mono<Void> write(AmqpMessage message) {
        return Mono.fromCompletionStage(delegate.write(messageConverter.toAxleMessage(message)));
    }

    @Override
    public Mono<Void> end() {
        return Mono.fromCompletionStage(delegate.end());
    }

    @Override
    public Mono<Void> end(AmqpMessage message) {
        return Mono.fromCompletionStage(delegate.end(messageConverter.toAxleMessage(message)));
    }

    @Override
    public AmqpSender send(AmqpMessage message) {
        delegate.send(messageConverter.toAxleMessage(message));

        return this;
    }

    @Override
    public Mono<Void> sendWithAck(AmqpMessage message) {
        return Mono.fromCompletionStage(delegate.sendWithAck(messageConverter.toAxleMessage(message)));
    }

    @Override
    public AmqpConnection connection() {
        return new SnowdropAmqpConnection(delegate.connection(), messageConverter);
    }

    @Override
    public String address() {
        return delegate.address();
    }

    @Override
    public Mono<Void> close() {
        return Mono.fromCompletionStage(delegate.close());
    }
}
