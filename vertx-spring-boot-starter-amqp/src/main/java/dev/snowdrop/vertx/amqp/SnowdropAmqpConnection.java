package dev.snowdrop.vertx.amqp;

import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;

import reactor.core.publisher.Mono;

class SnowdropAmqpConnection implements AmqpConnection {

    private final io.vertx.axle.amqp.AmqpConnection delegate;

    private final MessageConverter messageConverter;

    SnowdropAmqpConnection(io.vertx.axle.amqp.AmqpConnection delegate, MessageConverter messageConverter) {
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
        return Mono.fromCompletionStage(delegate.createSender(address))
            .map(delegateSender -> new SnowdropAmqpSender(delegateSender, messageConverter));
    }

    @Override
    public Mono<AmqpSender> createSender(String address, AmqpSenderOptions options) {
        return Mono.fromCompletionStage(delegate.createSender(address, options.toVertxAmqpSenderOptions()))
            .map(delegateSender -> new SnowdropAmqpSender(delegateSender, messageConverter));
    }

    @Override
    public Mono<AmqpSender> createAnonymousSender() {
        return Mono.fromCompletionStage(delegate.createAnonymousSender())
            .map(delegateSender -> new SnowdropAmqpSender(delegateSender, messageConverter));
    }

    @Override
    public Mono<AmqpReceiver> createReceiver(String address) {
        return Mono.fromCompletionStage(delegate.createReceiver(address))
            .map(delegateReceiver -> new SnowdropAmqpReceiver(delegateReceiver, messageConverter));
    }

    @Override
    public Mono<AmqpReceiver> createReceiver(String address, Consumer<AmqpMessage> messageHandler) {
        CompletionStage<AmqpReceiver> future = delegate
            .createReceiver(address, message -> messageHandler.accept(messageConverter.toSnowdropMessage(message)))
            .thenApply(delegateReceiver -> new SnowdropAmqpReceiver(delegateReceiver, messageConverter));

        return Mono.fromCompletionStage(future);
    }

    @Override
    public Mono<AmqpReceiver> createReceiver(String address, AmqpReceiverOptions options,
        Consumer<AmqpMessage> messageHandler) {

        CompletionStage<AmqpReceiver> future = delegate
            .createReceiver(address, options.toVertxAmqpReceiverOptions(),
                message -> messageHandler.accept(messageConverter.toSnowdropMessage(message)))
            .thenApply(delegateReceiver -> new SnowdropAmqpReceiver(delegateReceiver, messageConverter));

        return Mono.fromCompletionStage(future);
    }

    @Override
    public Mono<AmqpReceiver> createDynamicReceiver() {
        return Mono.fromCompletionStage(delegate.createDynamicReceiver())
            .map(delegateReceiver -> new SnowdropAmqpReceiver(delegateReceiver, messageConverter));
    }

    @Override
    public Mono<Void> close() {
        return Mono.fromCompletionStage(delegate.close());
    }
}
