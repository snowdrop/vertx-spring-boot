package dev.snowdrop.vertx.http.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.vertx.core.buffer.Buffer;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.NettyDataBufferFactory;

public final class BufferConverter {

    private final NettyDataBufferFactory dataBufferFactory;

    public BufferConverter() {
        this(new NettyDataBufferFactory(ByteBufAllocator.DEFAULT));
    }

    public BufferConverter(NettyDataBufferFactory dataBufferFactory) {
        this.dataBufferFactory = dataBufferFactory;
    }

    public NettyDataBufferFactory getDataBufferFactory() {
        return dataBufferFactory;
    }

    public DataBuffer toDataBuffer(Buffer buffer) {
        ByteBuf byteBuf = buffer.getByteBuf();
        return dataBufferFactory.wrap(byteBuf);
    }

    public Buffer toBuffer(DataBuffer dataBuffer) {
        ByteBuf byteBuf = NettyDataBufferFactory.toByteBuf(dataBuffer);
        return Buffer.buffer(byteBuf);
    }
}
