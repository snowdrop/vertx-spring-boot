package me.snowdrop.vertx.http;

import io.netty.buffer.ByteBufAllocator;
import io.vertx.core.buffer.Buffer;
import org.junit.Test;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.NettyDataBufferFactory;

import static me.snowdrop.vertx.http.Utils.dataBufferToBuffer;
import static org.assertj.core.api.Assertions.assertThat;

public class UtilsTest {

    private final NettyDataBufferFactory dataBufferFactory = new NettyDataBufferFactory(ByteBufAllocator.DEFAULT);

    @Test
    public void shouldConvertDataBufferToBuffer() {
        DataBuffer dataBuffer = dataBufferFactory.wrap("test".getBytes());

        Buffer buffer = dataBufferToBuffer(dataBuffer);

        assertThat(buffer).isEqualTo(Buffer.buffer("test"));
    }
}
