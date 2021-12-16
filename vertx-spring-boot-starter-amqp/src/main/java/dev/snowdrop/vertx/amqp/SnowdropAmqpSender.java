package dev.snowdrop.vertx.amqp;

import java.util.function.Consumer;

import io.smallrye.mutiny.converters.uni.UniReactorConverters;
import reactor.core.publisher.Mono;

class SnowdropAmqpSender implements AmqpSender {

    private final io.vertx.mutiny.amqp.AmqpSender delegate;

    private final MessageConverter messageConverter;

    SnowdropAmqpSender(io.vertx.mutiny.amqp.AmqpSender delegate, MessageConverter messageConverter) {
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
        delegate.drainHandler(() -> handler.accept(null));

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
        return delegate.write(messageConverter.toMutinyMessage(message))
            .convert()
            .with(UniReactorConverters.toMono());
    }

    @Override
    public Mono<Void> end() {
        return delegate.end()
            .convert()
            .with(UniReactorConverters.toMono());
    }

    @Override
    public Mono<Void> end(AmqpMessage message) {
        return delegate.end(messageConverter.toMutinyMessage(message))
            .convert()
            .with(UniReactorConverters.toMono());
    }

    @Override
    public AmqpSender send(AmqpMessage message) {
        delegate.send(messageConverter.toMutinyMessage(message));

        return this;
    }

    @Override
    public Mono<Void> sendWithAck(AmqpMessage message) {
        return delegate.sendWithAck(messageConverter.toMutinyMessage(message))
            .convert()
            .with(UniReactorConverters.toMono());
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
        return delegate.close()
            .convert()
            .with(UniReactorConverters.toMono());
    }
}
