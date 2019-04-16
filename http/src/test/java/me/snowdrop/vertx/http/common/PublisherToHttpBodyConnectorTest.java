package me.snowdrop.vertx.http.common;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.streams.WriteStream;
import me.snowdrop.vertx.http.utils.BufferConverter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.buffer.DataBuffer;
import reactor.core.publisher.MonoSink;
import reactor.test.publisher.TestPublisher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PublisherToHttpBodyConnectorTest {

    @Mock
    private WriteStream<Buffer> mockWriteStream;

    @Mock
    private MonoSink<Void> mockMonoSink;

    private BufferConverter bufferConverter = new BufferConverter();

    private PublisherToHttpBodyConnector connector;

    @Before
    public void setUp() {
        connector = new PublisherToHttpBodyConnector(mockWriteStream, mockMonoSink, bufferConverter);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldRegisterHandlersInConstructor() {
        verify(mockWriteStream).exceptionHandler(any(Handler.class));
    }

    @Test
    public void shouldGetDelegate() {
        assertThat(connector.getDelegate()).isEqualTo(mockWriteStream);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldHandleOnSubscribe() {
        TestPublisher<DataBuffer> publisher = TestPublisher.create();

        publisher.subscribe(connector);

        publisher.assertMinRequested(1);
        verify(mockWriteStream).drainHandler(any(Handler.class));
    }

    @Test
    public void shouldWriteAndRequestOnNext() {
        TestPublisher<DataBuffer> publisher = TestPublisher.create();
        publisher.subscribe(connector);

        Buffer buffer = Buffer.buffer("test");
        publisher.next(bufferConverter.toDataBuffer(buffer));

        verify(mockWriteStream).write(buffer);
        publisher.assertMinRequested(1);
    }

    @Test
    public void shouldNotRequestIfFull() {
        given(mockWriteStream.writeQueueFull()).willReturn(true);

        TestPublisher<DataBuffer> publisher = TestPublisher.create();
        publisher.subscribe(connector);

        publisher.assertMinRequested(0);
    }

    @Test
    public void shouldHandleComplete() {
        TestPublisher<DataBuffer> publisher = TestPublisher.create();
        publisher.subscribe(connector);

        publisher.complete();

        verify(mockMonoSink).success();
    }

    @Test
    public void shouldHandleCancel() {
        TestPublisher<DataBuffer> publisher = TestPublisher.create();
        publisher.subscribe(connector);

        connector.cancel();

        verify(mockMonoSink).success();
    }

    @Test
    public void shouldHandleError() {
        TestPublisher<DataBuffer> publisher = TestPublisher.create();
        publisher.subscribe(connector);

        RuntimeException exception = new RuntimeException("test");
        publisher.error(exception);

        verify(mockMonoSink).error(exception);
    }

    @Test
    public void verifyCompleteFlow() {
        TestWriteStream<Buffer> writeStream = new TestWriteStream<>();
        TestPublisher<DataBuffer> publisher = TestPublisher.create();

        Buffer firstBuffer = Buffer.buffer("first");
        Buffer secondBuffer = Buffer.buffer("second");
        Buffer thirdBuffer = Buffer.buffer("third");

        PublisherToHttpBodyConnector connector =
            new PublisherToHttpBodyConnector(writeStream, mockMonoSink, bufferConverter);

        writeStream.setWriteQueueMaxSize(2);

        publisher.subscribe(connector);
        publisher.assertMinRequested(1);

        publisher.next(bufferConverter.toDataBuffer(firstBuffer));
        publisher.assertMinRequested(1);

        publisher.next(bufferConverter.toDataBuffer(secondBuffer));
        publisher.assertMinRequested(0);
        assertThat(writeStream.getReceived()).containsOnly(firstBuffer, secondBuffer);

        writeStream.clearReceived();
        publisher.assertMinRequested(1);

        publisher.next(bufferConverter.toDataBuffer(thirdBuffer));
        assertThat(writeStream.getReceived()).containsOnly(thirdBuffer);
    }
}
