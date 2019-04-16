package me.snowdrop.vertx.http.common;

import io.vertx.core.streams.WriteStream;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.MonoSink;

public abstract class AbstractPublisherToWriteStreamConnector<T extends WriteStream<?>, U> extends BaseSubscriber<U> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final T delegate;

    private final MonoSink endHook;

    private final String logPrefix;

    public AbstractPublisherToWriteStreamConnector(T delegate, MonoSink endHook) {
        this.delegate = delegate;
        this.endHook = endHook;
        this.logPrefix = "[" + ObjectUtils.getIdentityHexString(delegate) + "] ";

        delegate.exceptionHandler(this::delegateExceptionHandler);
    }

    protected T getDelegate() {
        return delegate;
    }

    @Override
    protected void hookOnSubscribe(Subscription subscription) {
        logger.debug("{}{} subscribed", logPrefix, delegate);
        requestIfNotFull();
        delegate.drainHandler(event -> requestIfNotFull());
    }

    @Override
    protected void hookOnNext(U payload) {
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

    protected String getLogPrefix() {
        return logPrefix;
    }

    private void delegateExceptionHandler(Throwable ignored) {
        cancel();
    }

    private void requestIfNotFull() {
        if (!delegate.writeQueueFull()) {
            logger.debug("{}Requesting more data", logPrefix);
            request(1);
        }
    }
}
