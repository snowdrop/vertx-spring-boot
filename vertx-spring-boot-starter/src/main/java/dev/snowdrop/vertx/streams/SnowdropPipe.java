package dev.snowdrop.vertx.streams;

import reactor.core.publisher.Mono;

class SnowdropPipe<T> implements Pipe<T> {

    private final io.vertx.axle.core.streams.Pipe<T> delegate;

    @SuppressWarnings("unchecked")
    SnowdropPipe(ReadStream<T> readStream) {
        this.delegate = new io.vertx.axle.core.streams.Pipe<>(
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
        return Mono.fromCompletionStage(delegate.to(toAxleWriteStream(destination)));
    }

    @Override
    public void close() {
        delegate.close();
    }

    private io.vertx.axle.core.streams.WriteStream<T> toAxleWriteStream(WriteStream<T> writeStream) {
        return io.vertx.axle.core.streams.WriteStream.newInstance(writeStream.vertxWriteStream());
    }
}
