package dev.snowdrop.vertx.amqp;

import java.util.function.Consumer;

import io.smallrye.mutiny.converters.uni.UniReactorConverters;
import reactor.core.publisher.Mono;

class SnowdropAmqpConnection implements AmqpConnection {

    private final io.vertx.mutiny.amqp.AmqpConnection delegate;

    private final MessageConverter messageConverter;

    SnowdropAmqpConnection(io.vertx.mutiny.amqp.AmqpConnection delegate, MessageConverter messageConverter) {
        this.delegate = delegate;
        this.messageConverter = messageConverter;
    }

    @Override
    public AmqpConnection exceptionHandler(Consumer<Throwable> handler) {
        delegate.exceptionHandler(handler);
        return this;
    }

    @Override
    public Mono<AmqpSender> createSender(String address) {
        return delegate.createSender(address)
            .convert()
            .with(UniReactorConverters.toMono())
            .map(delegateSender -> new SnowdropAmqpSender(delegateSender, messageConverter));
    }

    @Override
    public Mono<AmqpSender> createSender(String address, AmqpSenderOptions options) {
        return delegate.createSender(address, options.toVertxAmqpSenderOptions())
            .convert()
            .with(UniReactorConverters.toMono())
            .map(delegateSender -> new SnowdropAmqpSender(delegateSender, messageConverter));
    }

    @Override
    public Mono<AmqpSender> createAnonymousSender() {
        return delegate.createAnonymousSender()
            .convert()
            .with(UniReactorConverters.toMono())
            .map(delegateSender -> new SnowdropAmqpSender(delegateSender, messageConverter));
    }

    @Override
    public Mono<AmqpReceiver> createReceiver(String address) {
        return delegate.createReceiver(address)
            .convert()
            .with(UniReactorConverters.toMono())
            .map(delegateReceiver -> new SnowdropAmqpReceiver(delegateReceiver, messageConverter));
    }

    @Override
    public Mono<AmqpReceiver> createReceiver(String address, AmqpReceiverOptions options) {
        return delegate.createReceiver(address, options.toVertxAmqpReceiverOptions())
            .convert()
            .with(UniReactorConverters.toMono())
            .map(delegateReceiver -> new SnowdropAmqpReceiver(delegateReceiver, messageConverter));
    }

    @Override
    public Mono<AmqpReceiver> createDynamicReceiver() {
        return delegate.createDynamicReceiver()
            .convert()
            .with(UniReactorConverters.toMono())
            .map(delegateReceiver -> new SnowdropAmqpReceiver(delegateReceiver, messageConverter));
    }

    @Override
    public Mono<Void> close() {
        return delegate.close()
            .convert()
            .with(UniReactorConverters.toMono());
    }
}
