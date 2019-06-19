package dev.snowdrop.vertx.http.common;

import java.util.ArrayList;
import java.util.List;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.streams.WriteStream;

public class TestWriteStream<T> implements WriteStream<T> {

    private int maxSize;

    private List<T> received = new ArrayList<>();

    private Handler<Void> drainHandler;

    public List<T> getReceived() {
        return received;
    }

    public void clearReceived() {
        boolean callDrain = writeQueueFull();
        received = new ArrayList<>();
        if (callDrain && drainHandler != null) {
            drainHandler.handle(null);
        }
    }

    @Override
    public TestWriteStream<T> setWriteQueueMaxSize(int maxSize) {
        this.maxSize = maxSize;
        return this;
    }

    @Override
    public boolean writeQueueFull() {
        return received.size() >= maxSize;
    }

    @Override
    public TestWriteStream<T> drainHandler(Handler<Void> handler) {
        this.drainHandler = handler;
        return this;
    }

    @Override
    public TestWriteStream<T> write(T data) {
        received.add(data);
        return this;
    }

    @Override
    public WriteStream<T> write(T data, Handler<AsyncResult<Void>> handler) {
        received.add(data);
        handler.handle(Future.succeededFuture());
        return this;
    }

    @Override
    public TestWriteStream<T> exceptionHandler(Handler<Throwable> handler) {
        return this;
    }

    @Override
    public void end() {
    }

    @Override
    public void end(Handler<AsyncResult<Void>> handler) {
        handler.handle(Future.succeededFuture());
    }
}
