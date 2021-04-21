package dev.snowdrop.vertx.amqp;

import io.vertx.core.buffer.Buffer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SnowdropAmqpMessageBuilderTest {

    @Mock
    private io.vertx.amqp.AmqpMessageBuilder mockDelegate;

    @Test
    public void shouldAddDataBufferBody() {
        DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
        DataBuffer originalBuffer = dataBufferFactory.wrap("test".getBytes());
        Buffer expectedBuffer = Buffer.buffer("test".getBytes());

        new SnowdropAmqpMessageBuilder(mockDelegate).withBufferAsBody(originalBuffer);

        verify(mockDelegate).withBufferAsBody(expectedBuffer);
    }
}
