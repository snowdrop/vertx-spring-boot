package dev.snowdrop.vertx.amqp;

import java.time.Duration;

import org.springframework.beans.factory.DisposableBean;
import reactor.core.publisher.Mono;

class SnowdropAmqpClient implements AmqpClient, DisposableBean {

    private final io.vertx.axle.amqp.AmqpClient delegate;

    private final MessageConverter messageConverter;

    SnowdropAmqpClient(io.vertx.axle.amqp.AmqpClient delegate, MessageConverter messageConverter) {
        this.delegate = delegate;
        this.messageConverter = messageConverter;
    }

    @Override
    public Mono<AmqpConnection> connect() {
        return Mono.fromCompletionStage(delegate::connect)
            .map(delegateConnection -> new SnowdropAmqpConnection(delegateConnection, messageConverter));
    }

    @Override
    public Mono<AmqpSender> createSender(String address) {
        return Mono.fromCompletionStage(() -> delegate.createSender(address))
            .map(delegateSender -> new SnowdropAmqpSender(delegateSender, messageConverter));
    }

    @Override
    public Mono<AmqpSender> createSender(String address, AmqpSenderOptions options) {
        return Mono.fromCompletionStage(() -> delegate.createSender(address, options.toVertxAmqpSenderOptions()))
            .map(delegateSender -> new SnowdropAmqpSender(delegateSender, messageConverter));
    }

    @Override
    public Mono<AmqpReceiver> createReceiver(String address) {
        return Mono.fromCompletionStage(() -> delegate.createReceiver(address))
            .map(delegateReceiver -> new SnowdropAmqpReceiver(delegateReceiver, messageConverter));
    }

    @Override
    public Mono<AmqpReceiver> createReceiver(String address, AmqpReceiverOptions options) {
        return Mono.fromCompletionStage(() -> delegate.createReceiver(address, options.toVertxAmqpReceiverOptions()))
            .map(delegateReceiver -> new SnowdropAmqpReceiver(delegateReceiver, messageConverter));
    }

    @Override
    public Mono<Void> close() {
        return Mono.fromCompletionStage(delegate::close);
    }

    @Override
    public void destroy() {
        close().block(Duration.ofSeconds(10)); // TODO should this be configurable?
    }
}
