package me.snowdrop.vertx.http;

import io.netty.buffer.ByteBufAllocator;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.streams.WriteStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
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

    private NettyDataBufferFactory dataBufferFactory = new NettyDataBufferFactory(ByteBufAllocator.DEFAULT);

    @SuppressWarnings("unchecked")
    @Test
    public void shouldRegisterHandlersInConstructor() {
        new PublisherToHttpBodyConnector(mockWriteStream, mockMonoSink);

        verify(mockWriteStream).drainHandler(any(Handler.class));
        verify(mockWriteStream).exceptionHandler(any(Handler.class));
    }

    @Test
    public void shouldGetDelegate() {
        PublisherToHttpBodyConnector subscriber = new PublisherToHttpBodyConnector(mockWriteStream, mockMonoSink);

        assertThat(subscriber.getDelegate()).isEqualTo(mockWriteStream);
    }

    @Test
    public void shouldRequestOnSubscribe() {
        PublisherToHttpBodyConnector subscriber = new PublisherToHttpBodyConnector(mockWriteStream, mockMonoSink);
        TestPublisher<DataBuffer> publisher = TestPublisher.create();

        publisher.subscribe(subscriber);

        publisher.assertMinRequested(1);
    }

    @Test
    public void shouldWriteAndRequestOnNext() {
        PublisherToHttpBodyConnector subscriber = new PublisherToHttpBodyConnector(mockWriteStream, mockMonoSink);
        TestPublisher<DataBuffer> publisher = TestPublisher.create();
        publisher.subscribe(subscriber);

        publisher.next(getDataBuffer("test"));

        verify(mockWriteStream).write(Buffer.buffer("test"));
        publisher.assertMinRequested(1);
    }

    @Test
    public void shouldNotRequestIfFull() {
        given(mockWriteStream.writeQueueFull()).willReturn(true);

        PublisherToHttpBodyConnector subscriber = new PublisherToHttpBodyConnector(mockWriteStream, mockMonoSink);
        TestPublisher<DataBuffer> publisher = TestPublisher.create();
        publisher.subscribe(subscriber);

        publisher.assertMinRequested(0);
    }

    @Test
    public void shouldHandleComplete() {
        PublisherToHttpBodyConnector subscriber = new PublisherToHttpBodyConnector(mockWriteStream, mockMonoSink);
        TestPublisher<DataBuffer> publisher = TestPublisher.create();
        publisher.subscribe(subscriber);

        publisher.complete();

        verify(mockMonoSink).success();
    }

    @Test
    public void shouldHandleCancel() {
        PublisherToHttpBodyConnector subscriber = new PublisherToHttpBodyConnector(mockWriteStream, mockMonoSink);
        TestPublisher<DataBuffer> publisher = TestPublisher.create();
        publisher.subscribe(subscriber);

        subscriber.cancel();

        verify(mockMonoSink).success();
    }

    @Test
    public void shouldHandleError() {
        PublisherToHttpBodyConnector subscriber = new PublisherToHttpBodyConnector(mockWriteStream, mockMonoSink);
        TestPublisher<DataBuffer> publisher = TestPublisher.create();
        publisher.subscribe(subscriber);

        RuntimeException exception = new RuntimeException("test");
        publisher.error(exception);

        verify(mockMonoSink).error(exception);
    }

    @Test
    public void verifyCompleteFlow() {
        TestWriteStream<Buffer> writeStream = new TestWriteStream<>();
        TestPublisher<DataBuffer> publisher = TestPublisher.create();

        PublisherToHttpBodyConnector subscriber = new PublisherToHttpBodyConnector(writeStream, mockMonoSink);

        writeStream.setWriteQueueMaxSize(2);

        publisher.subscribe(subscriber);
        publisher.assertMinRequested(1);

        publisher.next(getDataBuffer("first"));
        publisher.assertMinRequested(1);

        publisher.next(getDataBuffer("second"));
        publisher.assertMinRequested(0);
        assertThat(writeStream.getReceived()).containsOnly(Buffer.buffer("first"), Buffer.buffer("second"));

        writeStream.clearReceived();
        publisher.assertMinRequested(1);

        publisher.next(getDataBuffer("third"));
        assertThat(writeStream.getReceived()).containsOnly(Buffer.buffer("third"));
    }

    private DataBuffer getDataBuffer(String data) {
        return dataBufferFactory.wrap(data.getBytes());
    }
}
