package dev.snowdrop.vertx.http.common;

import io.vertx.core.Handler;
import io.vertx.core.streams.WriteStream;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.reactivestreams.Subscriber;
import reactor.core.publisher.MonoSink;
import reactor.test.publisher.TestPublisher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class WriteStreamSubscriberTest {

    @Mock
    private WriteStream<String> mockWriteStream;

    @Mock
    private MonoSink<Void> mockMonoSink;

    private WriteStreamSubscriber<WriteStream<String>, String> subscriber;

    @Before
    public void setUp() {
        subscriber = new WriteStreamSubscriber.Builder<WriteStream<String>, String>()
            .writeStream(mockWriteStream)
            .nextHandler(WriteStream::write)
            .endHook(mockMonoSink)
            .build();
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullWriteStream() {
        new WriteStreamSubscriber.Builder<WriteStream<String>, String>()
            .nextHandler((stream, value) -> {})
            .endHook(mockMonoSink)
            .build();
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullNextHandler() {
        new WriteStreamSubscriber.Builder<WriteStream<String>, String>()
            .writeStream(mockWriteStream)
            .endHook(mockMonoSink)
            .build();
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullEndHook() {
        new WriteStreamSubscriber.Builder<WriteStream<String>, String>()
            .writeStream(mockWriteStream)
            .nextHandler((stream, value) -> {})
            .build();
    }

    @Test
    public void shouldRegisterExceptionHandlerInConstructor() {
        verify(mockWriteStream).exceptionHandler(any(Handler.class));
    }

    @Test
    public void shouldHandleOnSubscribe() {
        TestPublisher<String> publisher = TestPublisher.create();

        publisher.subscribe(subscriber);

        publisher.assertMinRequested(1);
        verify(mockWriteStream).drainHandler(any(Handler.class));
    }

    @Test
    public void shouldWriteAndRequestOnNext() {
        TestPublisher<String> publisher = TestPublisher.create();
        publisher.subscribe(subscriber);

        publisher.next("test");

        verify(mockWriteStream).write("test");
        publisher.assertMinRequested(1);
    }

    @Test
    public void shouldNotRequestIfFull() {
        given(mockWriteStream.writeQueueFull()).willReturn(true);

        TestPublisher<String> publisher = TestPublisher.create();
        publisher.subscribe(subscriber);

        publisher.assertMinRequested(0);
    }

    @Test
    public void shouldWriteMultipleAndNotRequestIfFull() {
        given(mockWriteStream.writeQueueFull()).willReturn(false, false, true);
        subscriber = new WriteStreamSubscriber.Builder<WriteStream<String>, String>()
            .writeStream(mockWriteStream)
            .nextHandler(WriteStream::write)
            .endHook(mockMonoSink)
            .requestLimit(2)
            .build();

        TestPublisher<String> publisher = TestPublisher.create();
        publisher.subscribe(subscriber);

        publisher.assertMinRequested(2);
        publisher.assertMaxRequested(2);
        publisher.next("test1");
        publisher.assertMinRequested(2);
        publisher.assertMaxRequested(2);
        publisher.next("test2");
        publisher.assertMinRequested(1);
        publisher.assertMaxRequested(1);
        publisher.next("test3");
        publisher.assertMinRequested(0);
        publisher.assertMaxRequested(0);

        verify(mockWriteStream).write("test1");
        verify(mockWriteStream).write("test2");
        verify(mockWriteStream).write("test3");
    }

    @Test
    public void shouldHandleComplete() {
        TestPublisher<String> publisher = TestPublisher.create();
        publisher.subscribe(subscriber);

        publisher.complete();

        verify(mockMonoSink).success();
    }

    @Test
    public void shouldHandleCancel() {
        TestPublisher<String> publisher = TestPublisher.create();
        publisher.subscribe(subscriber);

        subscriber.cancel();

        verify(mockMonoSink).success();
    }

    @Test
    public void shouldHandleError() {
        TestPublisher<String> publisher = TestPublisher.create();
        publisher.subscribe(subscriber);

        RuntimeException exception = new RuntimeException("test");
        publisher.error(exception);

        verify(mockMonoSink).error(exception);
    }

    @Test
    public void verifyCompleteFlow() {
        TestWriteStream<String> writeStream = new TestWriteStream<>();
        TestPublisher<String> publisher = TestPublisher.create();

        Subscriber<String> subscriber = new WriteStreamSubscriber.Builder<WriteStream<String>, String>()
            .writeStream(writeStream)
            .nextHandler(WriteStream::write)
            .endHook(mockMonoSink)
            .build();

        writeStream.setWriteQueueMaxSize(2);

        publisher.subscribe(subscriber);
        publisher.assertMinRequested(1);

        publisher.next("first");
        publisher.assertMinRequested(1);

        publisher.next("second");
        publisher.assertMinRequested(0);
        assertThat(writeStream.getReceived()).containsOnly("first", "second");

        writeStream.clearReceived();
        publisher.assertMinRequested(1);

        publisher.next("third");
        assertThat(writeStream.getReceived()).containsOnly("third");

        publisher.complete();
        verify(mockMonoSink).success();
    }
}
