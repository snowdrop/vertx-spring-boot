package dev.snowdrop.vertx.amqp;

import io.vertx.core.buffer.Buffer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class SnowdropAmqpMessageTest {

    @Mock
    private io.vertx.amqp.AmqpMessage mockDelegate;

    private DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();

    @Test
    public void shouldGetBodyAsDataBuffer() {
        Buffer originalBuffer = Buffer.buffer("test".getBytes());
        DataBuffer expectedBuffer = dataBufferFactory.wrap("test".getBytes());

        given(mockDelegate.bodyAsBinary()).willReturn(originalBuffer);

        assertThat(new SnowdropAmqpMessage(mockDelegate).bodyAsBinary()).isEqualTo(expectedBuffer);
    }
}
