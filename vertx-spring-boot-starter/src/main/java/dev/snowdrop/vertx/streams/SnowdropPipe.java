package dev.snowdrop.vertx.streams;

import io.smallrye.mutiny.converters.uni.UniReactorConverters;
import reactor.core.publisher.Mono;

class SnowdropPipe<T> implements Pipe<T> {

    private final io.vertx.mutiny.core.streams.Pipe<T> delegate;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    SnowdropPipe(ReadStream<T> readStream) {
        this.delegate = new io.vertx.mutiny.core.streams.Pipe<>(
            new io.vertx.core.streams.impl.PipeImpl<>(readStream.vertxReadStream()));
    }

    @Override
    public Pipe<T> endOnFailure(boolean end) {
        delegate.endOnFailure(end);
        return this;
    }

    @Override
    public Pipe<T> endOnSuccess(boolean end) {
        delegate.endOnSuccess(end);
        return this;
    }

    @Override
    public Pipe<T> endOnComplete(boolean end) {
        delegate.endOnComplete(end);
        return this;
    }

    @Override
    public Mono<Void> to(WriteStream<T> destination) {
        return delegate.to(toMutinyWriteStream(destination))
            .convert()
            .with(UniReactorConverters.toMono());
    }

    @Override
    public void close() {
        delegate.close();
    }

    private io.vertx.mutiny.core.streams.WriteStream<T> toMutinyWriteStream(WriteStream<T> writeStream) {
        return io.vertx.mutiny.core.streams.WriteStream.newInstance(writeStream.vertxWriteStream());
    }
}
