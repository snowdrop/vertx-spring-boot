package me.snowdrop.vertx.http;

import io.vertx.core.streams.WriteStream;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.MonoSink;

public abstract class AbstractWriteStreamSubscriber<T extends WriteStream<?>, U> extends BaseSubscriber<U> {

    private final T delegate;

    private final MonoSink endHook;

    public AbstractWriteStreamSubscriber(T delegate, MonoSink endHook) {
        this.delegate = delegate;
        this.endHook = endHook;

        delegate.drainHandler(event -> requestIfNotFull());
        delegate.exceptionHandler(this::delegateExceptionHandler);
    }

    protected T getDelegate() {
        return delegate;
    }

    @Override
    protected void hookOnSubscribe(Subscription subscription) {
        requestIfNotFull();
    }

    @Override
    protected void hookOnNext(U payload) {
        requestIfNotFull();
    }

    @Override
    protected void hookOnComplete() {
        delegate.end();
        endHook.success();
    }

    @Override
    protected void hookOnCancel() {
        delegate.end();
        endHook.success();
    }

    @Override
    protected void hookOnError(Throwable throwable) {
        endHook.error(throwable);
    }

    private void delegateExceptionHandler(Throwable ignored) {
        cancel();
    }

    private void requestIfNotFull() {
        if (!delegate.writeQueueFull()) {
            request(1);
        }
    }
}
