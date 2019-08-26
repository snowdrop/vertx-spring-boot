package dev.snowdrop.vertx.streams;

import reactor.core.publisher.Mono;

public interface Pipe<T> {

    Pipe<T> endOnFailure(boolean end);

    Pipe<T> endOnSuccess(boolean end);

    Pipe<T> endOnComplete(boolean end);

    Mono<Void> to(WriteStream<T> destination);

    void close();
}
