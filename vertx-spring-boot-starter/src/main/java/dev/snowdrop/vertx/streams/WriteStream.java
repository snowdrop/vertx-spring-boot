package dev.snowdrop.vertx.streams;

import java.util.function.Consumer;

import reactor.core.publisher.Mono;

public interface WriteStream<T> {

    WriteStream<T> exceptionHandler(Consumer<Throwable> handler);

    WriteStream<T> drainHandler(Consumer<Void> handler);

    WriteStream<T> setWriteQueueMaxSize(int maxSize);

    boolean writeQueueFull();

    Mono<Void> write(T data);

    Mono<Void> end();

    default Mono<Void> end(T data) {
        return write(data).then(end());
    }

    io.vertx.core.streams.WriteStream vertxWriteStream();
}
