package me.snowdrop.vertx.http;

import io.netty.buffer.ByteBuf;
import io.vertx.core.buffer.Buffer;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.NettyDataBufferFactory;

public final class Utils {

    public static Buffer dataBufferToBuffer(DataBuffer dataBuffer) {
        ByteBuf byteBuf = NettyDataBufferFactory.toByteBuf(dataBuffer);
        return Buffer.buffer(byteBuf);
    }
}
