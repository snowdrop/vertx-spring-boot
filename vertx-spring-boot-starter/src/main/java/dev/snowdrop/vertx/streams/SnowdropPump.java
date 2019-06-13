package dev.snowdrop.vertx.streams;

class SnowdropPump<T> implements Pump {

    private final io.vertx.core.streams.Pump delegate;

    @SuppressWarnings("unchecked")
    SnowdropPump(ReadStream<T> readStream, WriteStream<T> writeStream) {
        delegate = io.vertx.core.streams.Pump.pump(readStream.vertxReadStream(), writeStream.vertxWriteStream());
    }

    @SuppressWarnings("unchecked")
    SnowdropPump(ReadStream<T> readStream, WriteStream<T> writeStream, int writeQueueMaxSize) {
        delegate = io.vertx.core.streams.Pump.pump(readStream.vertxReadStream(), writeStream.vertxWriteStream(),
            writeQueueMaxSize);
    }

    @Override
    public Pump setWriteQueueMaxSize(int maxSize) {
        delegate.setWriteQueueMaxSize(maxSize);
        return this;
    }

    @Override
    public Pump start() {
        delegate.start();
        return this;
    }

    @Override
    public Pump stop() {
        delegate.stop();
        return this;
    }

    @Override
    public int numberPumped() {
        return delegate.numberPumped();
    }
}
