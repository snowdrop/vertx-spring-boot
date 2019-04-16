package me.snowdrop.vertx.http.common;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.ServerWebSocket;
import me.snowdrop.vertx.http.utils.BufferConverter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.web.reactive.socket.WebSocketMessage;
import reactor.core.publisher.MonoSink;
import reactor.test.publisher.TestPublisher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PublisherToWebSocketConnectorTest {

    @Mock
    private ServerWebSocket mockServerWebSocket;

    @Mock
    private MonoSink<Void> mockMonoSink;

    private BufferConverter bufferConverter = new BufferConverter();

    private PublisherToWebSocketConnector connector;

    @Before
    public void setUp() {
        connector = new PublisherToWebSocketConnector(mockServerWebSocket, mockMonoSink, bufferConverter);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldRegisterHandlersInConstructor() {
        verify(mockServerWebSocket).exceptionHandler(any(Handler.class));
    }

    @Test
    public void shouldGetDelegate() {
        assertThat(connector.getDelegate()).isEqualTo(mockServerWebSocket);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldHandleOnSubscribe() {
        TestPublisher<WebSocketMessage> publisher = TestPublisher.create();

        publisher.subscribe(connector);

        publisher.assertMinRequested(1);
        verify(mockServerWebSocket).drainHandler(any(Handler.class));
    }

    @Test
    public void shouldWriteTextMessageAndPullOnNext() {
        TestPublisher<WebSocketMessage> publisher = TestPublisher.create();
        publisher.subscribe(connector);

        publisher.next(getWebSocketMessage(WebSocketMessage.Type.TEXT, "test"));

        verify(mockServerWebSocket).writeTextMessage("test");
        publisher.assertMinRequested(1);
    }

    @Test
    public void shouldWriteBinaryMessageAndPullOnNext() {
        TestPublisher<WebSocketMessage> publisher = TestPublisher.create();
        publisher.subscribe(connector);

        publisher.next(getWebSocketMessage(WebSocketMessage.Type.BINARY, "test"));

        verify(mockServerWebSocket).writeBinaryMessage(Buffer.buffer("test"));
        publisher.assertMinRequested(1);
    }

    @Test
    public void shouldWritePingMessageAndPullOnNext() {
        TestPublisher<WebSocketMessage> publisher = TestPublisher.create();
        publisher.subscribe(connector);

        publisher.next(getWebSocketMessage(WebSocketMessage.Type.PING, "test"));

        verify(mockServerWebSocket).writePing(Buffer.buffer("test"));
        publisher.assertMinRequested(1);
    }

    @Test
    public void shouldWritePongMessageAndPullOnNext() {
        TestPublisher<WebSocketMessage> publisher = TestPublisher.create();
        publisher.subscribe(connector);

        publisher.next(getWebSocketMessage(WebSocketMessage.Type.PONG, "test"));

        verify(mockServerWebSocket).writePong(Buffer.buffer("test"));
        publisher.assertMinRequested(1);
    }

    @Test
    public void shouldNotPullIfFull() {
        given(mockServerWebSocket.writeQueueFull()).willReturn(true);

        TestPublisher<WebSocketMessage> publisher = TestPublisher.create();
        publisher.subscribe(connector);

        publisher.assertMinRequested(0);
    }

    @Test
    public void shouldHandleComplete() {
        TestPublisher<WebSocketMessage> publisher = TestPublisher.create();
        publisher.subscribe(connector);

        publisher.complete();

        verify(mockMonoSink).success();
    }

    @Test
    public void shouldHandleCancel() {
        TestPublisher<WebSocketMessage> publisher = TestPublisher.create();
        publisher.subscribe(connector);

        connector.cancel();

        verify(mockMonoSink).success();
    }

    @Test
    public void shouldHandleError() {
        TestPublisher<WebSocketMessage> publisher = TestPublisher.create();
        publisher.subscribe(connector);

        RuntimeException exception = new RuntimeException("test");
        publisher.error(exception);

        verify(mockMonoSink).error(exception);
    }

    private WebSocketMessage getWebSocketMessage(WebSocketMessage.Type type, String data) {
        DataBuffer dataBuffer = bufferConverter.toDataBuffer(Buffer.buffer(data));

        return new WebSocketMessage(type, dataBuffer);
    }
}
