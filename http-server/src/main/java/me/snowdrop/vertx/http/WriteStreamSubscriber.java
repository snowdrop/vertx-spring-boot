package me.snowdrop.vertx.http;

import io.vertx.core.streams.WriteStream;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.SignalType;

public class WriteStreamSubscriber<T> extends BaseSubscriber<T> {

    private final WriteStream<T> writeStream;

    public WriteStreamSubscriber(WriteStream<T> writeStream) {
        this.writeStream = writeStream;
        writeStream.drainHandler(event -> pull());
        // TODO do we need exception handler?
    }

    @Override
    protected void hookOnSubscribe(Subscription subscription) {
        pull();
    }

    @Override
    protected void hookOnNext(T value) {
        writeStream.write(value);
        pull();
    }

    @Override
    protected void hookFinally(SignalType type) {
        writeStream.end();
    }

    private void pull() {
        if (!writeStream.writeQueueFull()) {
            request(1);
        }
    }
}
