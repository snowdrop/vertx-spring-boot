package me.snowdrop.vertx.http.common;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.streams.ReadStream;
import me.snowdrop.vertx.http.utils.BufferConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Flux;

public class HttpBodyToFluxConnector {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final BufferConverter bufferConverter;

    public HttpBodyToFluxConnector(BufferConverter bufferConverter) {
        this.bufferConverter = bufferConverter;
    }

    public Flux<DataBuffer> connect(ReadStream<Buffer> stream) {
        String logPrefix = "[" + ObjectUtils.getIdentityHexString(stream) + "] ";

        return Flux.create(sink -> {
            logger.debug("{}Connecting to a body read stream", logPrefix);
            stream.pause()
                .handler(chunk -> {
                    logger.debug("{}Received '{}'", logPrefix, chunk);
                    DataBuffer dataBuffer = bufferConverter.toDataBuffer(chunk);
                    sink.next(dataBuffer);
                })
                .exceptionHandler(throwable -> {
                    logger.debug("{}Received exception '{}'", logPrefix, throwable);
                    sink.error(throwable);
                })
                .endHandler(v -> {
                    logger.debug("{}Body read stream ended", logPrefix);
                    sink.complete();
                });
            sink.onRequest(i -> {
                logger.debug("{} Fetching '{}' entries from a body read stream", logPrefix, i);
                stream.fetch(i);
            });
        });
    }
}
