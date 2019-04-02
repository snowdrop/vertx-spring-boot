package me.snowdrop.vertx.http.common;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.streams.WriteStream;
import me.snowdrop.vertx.http.utils.BufferConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import reactor.core.publisher.MonoSink;

public class PublisherToHttpBodyConnector
    extends AbstractPublisherToWriteStreamConnector<WriteStream<Buffer>, DataBuffer> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final BufferConverter bufferConverter;

    public PublisherToHttpBodyConnector(WriteStream<Buffer> delegate, MonoSink endHook,
        BufferConverter bufferConverter) {
        super(delegate, endHook);
        this.bufferConverter = bufferConverter;
    }

    @Override
    protected void hookOnNext(DataBuffer payload) {
        Buffer buffer = bufferConverter.toBuffer(payload);
        logger.debug("{}Next entry: {}", getLogPrefix(), buffer);
        getDelegate().write(buffer);
        super.hookOnNext(payload);
    }
}
