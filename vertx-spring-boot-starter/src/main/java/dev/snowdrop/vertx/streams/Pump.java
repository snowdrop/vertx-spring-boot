package dev.snowdrop.vertx.streams;

public interface Pump {

    static <T> Pump pump(ReadStream<T> readStream, WriteStream<T> writeStream) {
        return new SnowdropPump<>(readStream, writeStream);
    }

    static <T> Pump pump(ReadStream<T> readStream, WriteStream<T> writeStream, int writeQueueMaxSize) {
        return new SnowdropPump<>(readStream, writeStream, writeQueueMaxSize);
    }

    Pump setWriteQueueMaxSize(int maxSize);

    Pump start();

    Pump stop();

    int numberPumped();
}
