package dev.snowdrop.vertx.http.utils;

import io.netty.buffer.ByteBufAllocator;
import io.vertx.core.buffer.Buffer;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.NettyDataBufferFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class BufferConverterTest {

    private NettyDataBufferFactory dataBufferFactory = new NettyDataBufferFactory(ByteBufAllocator.DEFAULT);

    private BufferConverter converter = new BufferConverter(dataBufferFactory);

    @Test
    public void shouldGetDataBufferFactory() {
        assertThat(converter.getDataBufferFactory()).isEqualTo(dataBufferFactory);
    }

    @Test
    public void shouldConvertToBuffer() {
        DataBuffer dataBuffer = dataBufferFactory.wrap("test".getBytes());
        Buffer expectedBuffer = Buffer.buffer("test");
        Buffer actualBuffer = converter.toBuffer(dataBuffer);

        assertThat(actualBuffer).isEqualTo(expectedBuffer);
    }

    @Test
    public void shouldConvertToDataBuffer() {
        Buffer buffer = Buffer.buffer("test");
        DataBuffer expectedDataBuffer = dataBufferFactory.wrap("test".getBytes());
        DataBuffer actualDataBuffer = converter.toDataBuffer(buffer);

        assertThat(actualDataBuffer).isEqualTo(expectedDataBuffer);
    }
}
