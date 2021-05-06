package dev.snowdrop.vertx.http.common;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;

import io.vertx.core.streams.WriteStream;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.MonoSink;
import reactor.core.publisher.SignalType;

public class WriteStreamSubscriber<T extends WriteStream<?>, U> extends BaseSubscriber<U> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final T writeStream;

    private final BiConsumer<T, U> nextHandler;

    private final MonoSink<Void> endHook;

    private final long requestLimit;

    private final AtomicLong pendingCount = new AtomicLong();

    private final AtomicBoolean isActive = new AtomicBoolean(false);

    private final String logPrefix;

    private WriteStreamSubscriber(T writeStream, BiConsumer<T, U> nextHandler, MonoSink<Void> endHook,
        long requestLimit) {
        this.writeStream = writeStream;
        this.nextHandler = nextHandler;
        this.endHook = endHook;
        this.requestLimit = requestLimit;
        this.logPrefix = "[" + ObjectUtils.getIdentityHexString(writeStream) + "] ";

        writeStream.exceptionHandler(this::exceptionHandler);
        writeStream.drainHandler(this::drainHandler);
    }

    @Override
    protected void hookOnSubscribe(Subscription subscription) {
        logger.debug("{}{} subscribed", logPrefix, writeStream);
        isActive.set(true);
        requestIfNotFull();
    }

    @Override
    protected void hookOnNext(U value) {
        logger.debug("{}Next: {}", logPrefix, value);
        nextHandler.accept(writeStream, value);
        pendingCount.decrementAndGet();
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

    @Override
    protected void hookFinally(SignalType type) {
        isActive.set(false);
    }

    private void exceptionHandler(Throwable ignored) {
        cancel();
    }

    private void drainHandler(Void event) {
        logger.debug("{} drain", logPrefix);
        requestIfNotFull();
    }

    private void requestIfNotFull() {
        if (isActive.get() && !writeStream.writeQueueFull() && pendingCount.get() < requestLimit) {
            logger.debug("{}Requesting more data pendingCount={} requestLimit={}", logPrefix, pendingCount.get(),
                requestLimit);
            request(requestLimit - pendingCount.getAndSet(requestLimit));
        }
    }

    public static class Builder<T extends WriteStream<?>, U> {

        private T writeStream;

        private BiConsumer<T, U> nextHandler;

        private MonoSink<Void> endHook;

        private long requestLimit = 1L;

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

        public Builder<T, U> requestLimit(long requestLimit) {
            this.requestLimit = requestLimit;
            return this;
        }

        public WriteStreamSubscriber<T, U> build() {
            Objects.requireNonNull(writeStream, "Write stream is required");
            Objects.requireNonNull(nextHandler, "Next handler is required");
            Objects.requireNonNull(endHook, "End hook is required");

            return new WriteStreamSubscriber<>(writeStream, nextHandler, endHook, requestLimit);
        }
    }
}
