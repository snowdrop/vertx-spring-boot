package dev.snowdrop.vertx.amqp;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class SnowdropAmqpReceiver implements AmqpReceiver {

    private final io.vertx.axle.amqp.AmqpReceiver delegate;

    private final MessageConverter messageConverter;

    SnowdropAmqpReceiver(io.vertx.axle.amqp.AmqpReceiver delegate, MessageConverter messageConverter) {
        this.delegate = delegate;
        this.messageConverter = messageConverter;
    }

    @Override
    public Mono<AmqpMessage> mono() {
        return Mono.from(delegate.toPublisher())
            .map(messageConverter::toSnowdropMessage);
    }

    @Override
    public Flux<AmqpMessage> flux() {
        return Flux.from(delegate.toPublisher())
            .map(messageConverter::toSnowdropMessage);
    }

    @Override
    public String address() {
        return delegate.address();
    }

    @Override
    public AmqpConnection connection() {
        return new SnowdropAmqpConnection(delegate.connection(), messageConverter);
    }

    @Override
    public Mono<Void> close() {
        return Mono.fromCompletionStage(delegate::close);
    }

    @Override
    public io.vertx.core.streams.ReadStream vertxReadStream() {
        return delegate.getDelegate();
    }
}
