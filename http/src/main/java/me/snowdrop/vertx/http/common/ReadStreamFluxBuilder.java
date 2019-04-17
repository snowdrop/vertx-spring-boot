package me.snowdrop.vertx.http.common;

import java.util.Objects;
import java.util.function.Function;

import io.vertx.core.streams.ReadStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Flux;

public class ReadStreamFluxBuilder<T, R> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private ReadStream<T> readStream;

    private Function<T, R> dataConverter;

    public ReadStreamFluxBuilder<T, R> readStream(ReadStream<T> readStream) {
        this.readStream = readStream;
        return this;
    }

    public ReadStreamFluxBuilder<T, R> dataConverter(Function<T, R> dataConverter) {
        this.dataConverter = dataConverter;
        return this;
    }

    public Flux<R> build() {
        Objects.requireNonNull(readStream, "Read stream is required");
        Objects.requireNonNull(dataConverter, "Data converter is required");

        readStream.pause();

        return Flux.create(sink -> {
            String logPrefix = "[" + ObjectUtils.getIdentityHexString(readStream) + "] ";
            readStream
                .handler(data -> {
                    logger.debug("{}Received '{}'", logPrefix, data);
                    sink.next(dataConverter.apply(data));
                })
                .exceptionHandler(throwable -> {
                    logger.debug("{}Received exception '{}'", logPrefix, throwable.toString());
                    sink.error(throwable);
                })
                .endHandler(v -> {
                    logger.debug("{}Read stream ended", logPrefix);
                    sink.complete();
                });
            sink.onRequest(i -> {
                logger.debug("{} Fetching '{}' entries from a read stream", logPrefix, i);
                readStream.fetch(i);
            });
        });
    }

}
