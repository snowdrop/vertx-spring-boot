package dev.snowdrop.vertx.amqp;

import io.smallrye.mutiny.converters.multi.MultiReactorConverters;
import io.smallrye.mutiny.converters.uni.UniReactorConverters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class SnowdropAmqpReceiver implements AmqpReceiver {

    private final io.vertx.mutiny.amqp.AmqpReceiver delegate;

    private final MessageConverter messageConverter;

    SnowdropAmqpReceiver(io.vertx.mutiny.amqp.AmqpReceiver delegate, MessageConverter messageConverter) {
        this.delegate = delegate;
        this.messageConverter = messageConverter;
    }

    @Override
    public Mono<AmqpMessage> mono() {
        return delegate.toMulti()
            .convert()
            .with(MultiReactorConverters.toMono())
            .map(messageConverter::toSnowdropMessage);
    }

    @Override
    public Flux<AmqpMessage> flux() {
        return delegate.toMulti()
            .convert()
            .with(MultiReactorConverters.toFlux())
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
        return delegate.close()
            .convert()
            .with(UniReactorConverters.toMono());
    }

    @Override
    public io.vertx.core.streams.ReadStream vertxReadStream() {
        return delegate.getDelegate();
    }
}
