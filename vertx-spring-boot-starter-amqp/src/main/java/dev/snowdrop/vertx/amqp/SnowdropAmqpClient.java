package dev.snowdrop.vertx.amqp;

import java.time.Duration;

import io.smallrye.mutiny.converters.uni.UniReactorConverters;
import org.springframework.beans.factory.DisposableBean;
import reactor.core.publisher.Mono;

class SnowdropAmqpClient implements AmqpClient, DisposableBean {

    private final io.vertx.mutiny.amqp.AmqpClient delegate;

    private final MessageConverter messageConverter;

    SnowdropAmqpClient(io.vertx.mutiny.amqp.AmqpClient delegate, MessageConverter messageConverter) {
        this.delegate = delegate;
        this.messageConverter = messageConverter;
    }

    @Override
    public Mono<AmqpConnection> connect() {
        return delegate.connect()
            .convert()
            .with(UniReactorConverters.toMono())
            .map(delegateConnection -> new SnowdropAmqpConnection(delegateConnection, messageConverter));
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
    public Mono<Void> close() {
        return delegate.close()
            .convert()
            .with(UniReactorConverters.toMono());
    }

    @Override
    public void destroy() {
        close().block(Duration.ofSeconds(10)); // TODO should this be configurable?
    }
}
