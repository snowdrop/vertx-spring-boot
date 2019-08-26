package dev.snowdrop.vertx.streams;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReadStream<T> {

    Mono<T> mono();

    Flux<T> flux();

    default Pipe<T> pipe() {
        vertxReadStream().pause();
        return new SnowdropPipe<>(this);
    }

    default Mono<Void> pipeTo(WriteStream<T> destination) {
        return new SnowdropPipe<>(this).to(destination);
    }

    io.vertx.core.streams.ReadStream vertxReadStream();
}
