package me.snowdrop.vertx.http;

import io.netty.buffer.ByteBufAllocator;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.ServerWebSocket;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.web.reactive.socket.WebSocketMessage;
import reactor.core.publisher.MonoSink;
import reactor.test.publisher.TestPublisher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ServerWebSocketSubscriberTest {

    @Mock
    private ServerWebSocket mockServerWebSocket;

    @Mock
    private MonoSink<Void> mockMonoSink;

    private NettyDataBufferFactory dataBufferFactory = new NettyDataBufferFactory(ByteBufAllocator.DEFAULT);

    @SuppressWarnings("unchecked")
    @Test
    public void shouldRegisterHandlersInConstructor() {
        new ServerWebSocketSubscriber(mockServerWebSocket, mockMonoSink);

        verify(mockServerWebSocket).drainHandler(any(Handler.class));
        verify(mockServerWebSocket).exceptionHandler(any(Handler.class));
    }

    @Test
    public void shouldGetDelegate() {
        ServerWebSocketSubscriber subscriber = new ServerWebSocketSubscriber(mockServerWebSocket, mockMonoSink);

        assertThat(subscriber.getDelegate()).isEqualTo(mockServerWebSocket);
    }

    @Test
    public void shouldRequestOnSubscribe() {
        ServerWebSocketSubscriber subscriber = new ServerWebSocketSubscriber(mockServerWebSocket, mockMonoSink);
        TestPublisher<WebSocketMessage> publisher = TestPublisher.create();

        publisher.subscribe(subscriber);

        publisher.assertMinRequested(1);
    }

    @Test
    public void shouldWriteTextMessageAndPullOnNext() {
        ServerWebSocketSubscriber subscriber = new ServerWebSocketSubscriber(mockServerWebSocket, mockMonoSink);
        TestPublisher<WebSocketMessage> publisher = TestPublisher.create();
        publisher.subscribe(subscriber);

        publisher.next(getWebSocketMessage(WebSocketMessage.Type.TEXT, "test"));

        verify(mockServerWebSocket).writeTextMessage("test");
        publisher.assertMinRequested(1);
    }

    @Test
    public void shouldWriteBinaryMessageAndPullOnNext() {
        ServerWebSocketSubscriber subscriber = new ServerWebSocketSubscriber(mockServerWebSocket, mockMonoSink);
        TestPublisher<WebSocketMessage> publisher = TestPublisher.create();
        publisher.subscribe(subscriber);

        publisher.next(getWebSocketMessage(WebSocketMessage.Type.BINARY, "test"));

        verify(mockServerWebSocket).writeBinaryMessage(Buffer.buffer("test"));
        publisher.assertMinRequested(1);
    }

    @Test
    public void shouldWritePingMessageAndPullOnNext() {
        ServerWebSocketSubscriber subscriber = new ServerWebSocketSubscriber(mockServerWebSocket, mockMonoSink);
        TestPublisher<WebSocketMessage> publisher = TestPublisher.create();
        publisher.subscribe(subscriber);

        publisher.next(getWebSocketMessage(WebSocketMessage.Type.PING, "test"));

        verify(mockServerWebSocket).writePing(Buffer.buffer("test"));
        publisher.assertMinRequested(1);
    }

    @Test
    public void shouldWritePongMessageAndPullOnNext() {
        ServerWebSocketSubscriber subscriber = new ServerWebSocketSubscriber(mockServerWebSocket, mockMonoSink);
        TestPublisher<WebSocketMessage> publisher = TestPublisher.create();
        publisher.subscribe(subscriber);

        publisher.next(getWebSocketMessage(WebSocketMessage.Type.PONG, "test"));

        verify(mockServerWebSocket).writePong(Buffer.buffer("test"));
        publisher.assertMinRequested(1);
    }

    @Test
    public void shouldNotPullIfFull() {
        given(mockServerWebSocket.writeQueueFull()).willReturn(true);

        ServerWebSocketSubscriber subscriber = new ServerWebSocketSubscriber(mockServerWebSocket, mockMonoSink);
        TestPublisher<WebSocketMessage> publisher = TestPublisher.create();
        publisher.subscribe(subscriber);

        publisher.assertMinRequested(0);
    }

    @Test
    public void shouldHandleComplete() {
        ServerWebSocketSubscriber subscriber = new ServerWebSocketSubscriber(mockServerWebSocket, mockMonoSink);
        TestPublisher<WebSocketMessage> publisher = TestPublisher.create();
        publisher.subscribe(subscriber);

        publisher.complete();

        verify(mockServerWebSocket).end();
        verify(mockMonoSink).success();
    }

    @Test
    public void shouldHandleCancel() {
        ServerWebSocketSubscriber subscriber = new ServerWebSocketSubscriber(mockServerWebSocket, mockMonoSink);
        TestPublisher<WebSocketMessage> publisher = TestPublisher.create();
        publisher.subscribe(subscriber);

        subscriber.cancel();

        verify(mockServerWebSocket).end();
        verify(mockMonoSink).success();
    }

    @Test
    public void shouldHandleError() {
        ServerWebSocketSubscriber subscriber = new ServerWebSocketSubscriber(mockServerWebSocket, mockMonoSink);
        TestPublisher<WebSocketMessage> publisher = TestPublisher.create();
        publisher.subscribe(subscriber);

        RuntimeException exception = new RuntimeException("test");
        publisher.error(exception);

        verify(mockMonoSink).error(exception);
    }

    private WebSocketMessage getWebSocketMessage(WebSocketMessage.Type type, String data) {
        DataBuffer dataBuffer = dataBufferFactory.wrap(data.getBytes());

        return new WebSocketMessage(type, dataBuffer);
    }
}
