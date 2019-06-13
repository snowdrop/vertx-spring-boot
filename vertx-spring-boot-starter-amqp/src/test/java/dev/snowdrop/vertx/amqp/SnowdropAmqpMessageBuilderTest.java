package dev.snowdrop.vertx.amqp;

import io.vertx.core.buffer.Buffer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
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
