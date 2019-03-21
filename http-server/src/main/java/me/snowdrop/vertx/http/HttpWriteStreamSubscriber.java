package me.snowdrop.vertx.http;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.streams.WriteStream;
import org.springframework.core.io.buffer.DataBuffer;
import reactor.core.publisher.MonoSink;

import static me.snowdrop.vertx.http.Utils.dataBufferToBuffer;

public class HttpWriteStreamSubscriber extends AbstractWriteStreamSubscriber<WriteStream<Buffer>, DataBuffer> {

    public HttpWriteStreamSubscriber(WriteStream<Buffer> delegate, MonoSink endHook) {
        super(delegate, endHook);
    }

    @Override
    protected void hookOnNext(DataBuffer payload) {
        Buffer buffer = dataBufferToBuffer(payload);
        getDelegate().write(buffer);
        super.hookOnNext(payload);
    }
}
