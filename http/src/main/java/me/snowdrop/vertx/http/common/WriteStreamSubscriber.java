package me.snowdrop.vertx.http.common;

import java.util.Objects;
import java.util.function.BiConsumer;

import io.vertx.core.streams.WriteStream;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.MonoSink;

public class WriteStreamSubscriber<T extends WriteStream<?>, U> extends BaseSubscriber<U> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final T writeStream;

    private final BiConsumer<T, U> nextHandler;

    private final MonoSink<Void> endHook;

    private final String logPrefix;

    private WriteStreamSubscriber(T writeStream, BiConsumer<T, U> nextHandler, MonoSink<Void> endHook) {
        this.writeStream = writeStream;
        this.nextHandler = nextHandler;
        this.endHook = endHook;
        this.logPrefix = "[" + ObjectUtils.getIdentityHexString(writeStream) + "] ";

        writeStream.exceptionHandler(this::exceptionHandler);
    }

    @Override
    protected void hookOnSubscribe(Subscription subscription) {
        logger.debug("{}{} subscribed", logPrefix, writeStream);
        requestIfNotFull();
        writeStream.drainHandler(event -> requestIfNotFull());
    }

    @Override
    protected void hookOnNext(U value) {
        logger.debug("{}Next: {}", logPrefix, value);
        nextHandler.accept(writeStream, value);
        requestIfNotFull();
    }

    @Override
    protected void hookOnComplete() {
        logger.debug("{}Completed", logPrefix);
        endHook.success();
    }

    @Override
    protected void hookOnCancel() {
        logger.debug("{}Canceled", logPrefix);
        endHook.success();
    }

    @Override
    protected void hookOnError(Throwable throwable) {
        logger.debug("{}Error: {}", logPrefix, throwable);
        endHook.error(throwable);
    }

    private void exceptionHandler(Throwable ignored) {
        cancel();
    }

    private void requestIfNotFull() {
        if (!writeStream.writeQueueFull()) {
            logger.debug("{}Requesting more data", logPrefix);
            request(1);
        }
    }

    public static class Builder<T extends WriteStream<?>, U> {

        private T writeStream;

        private BiConsumer<T, U> nextHandler;

        private MonoSink<Void> endHook;

        public Builder<T, U> writeStream(T writeStream) {
            this.writeStream = writeStream;
            return this;
        }

        public Builder<T, U> nextHandler(BiConsumer<T, U> nextHandler) {
            this.nextHandler = nextHandler;
            return this;
        }

        public Builder<T, U> endHook(MonoSink<Void> endHook) {
            this.endHook = endHook;
            return this;
        }

        public WriteStreamSubscriber<T, U> build() {
            Objects.requireNonNull(writeStream, "Write stream is required");
            Objects.requireNonNull(nextHandler, "Next handler is required");
            Objects.requireNonNull(endHook, "End hook is required");

            return new WriteStreamSubscriber<>(writeStream, nextHandler, endHook);
        }
    }
}
