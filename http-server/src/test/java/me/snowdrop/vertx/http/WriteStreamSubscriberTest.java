package me.snowdrop.vertx.http;

import io.vertx.core.streams.WriteStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.test.publisher.TestPublisher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class WriteStreamSubscriberTest {

    @Mock
    private WriteStream<String> mockWriteStream;

    @Test
    public void shouldPullOnSubscribe() {
        TestPublisher<String> publisher = TestPublisher.create();
        publisher.subscribe(new WriteStreamSubscriber<>(mockWriteStream));

        publisher.assertMinRequested(1);
    }

    @Test
    public void shouldWriteAndPullOnNext() {
        TestPublisher<String> publisher = TestPublisher.create();
        publisher.subscribe(new WriteStreamSubscriber<>(mockWriteStream));
        publisher.next("test");

        verify(mockWriteStream).write("test");
        publisher.assertMinRequested(1);
    }

    @Test
    public void shouldNotPullIfFull() {
        given(mockWriteStream.writeQueueFull()).willReturn(true);

        TestPublisher<String> publisher = TestPublisher.create();
        publisher.subscribe(new WriteStreamSubscriber<>(mockWriteStream));

        publisher.assertMinRequested(0);
    }

    @Test
    public void shouldEnd() {
        TestPublisher<String> publisher = TestPublisher.create();
        publisher.subscribe(new WriteStreamSubscriber<>(mockWriteStream));
        publisher.complete();

        verify(mockWriteStream).end();
    }

    @Test
    public void verifyCompleteFlow() {
        TestWriteStream<String> writeStream = new TestWriteStream<>();
        TestPublisher<String> publisher = TestPublisher.create();

        writeStream.setWriteQueueMaxSize(2);

        publisher.subscribe(new WriteStreamSubscriber<>(writeStream));
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
    }
}
