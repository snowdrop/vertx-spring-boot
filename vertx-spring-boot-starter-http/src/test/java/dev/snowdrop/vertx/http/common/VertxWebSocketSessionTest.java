package dev.snowdrop.vertx.http.common;

import dev.snowdrop.vertx.http.utils.BufferConverter;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.ServerWebSocket;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.web.reactive.socket.CloseStatus;
import org.springframework.web.reactive.socket.HandshakeInfo;
import org.springframework.web.reactive.socket.WebSocketMessage;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.publisher.TestPublisher;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class VertxWebSocketSessionTest {

    @Mock
    private ServerWebSocket mockServerWebSocket;

    @Mock
    private HandshakeInfo mockHandshakeInfo;

    private final BufferConverter bufferConverter = new BufferConverter();

    @Test
    public void shouldReceiveTextMessages() {
        initMockSocketHandlers();
        given(mockServerWebSocket.textMessageHandler(any())).will(invocation -> {
            Handler<String> handler = invocation.getArgument(0);
            handler.handle("test1");
            handler.handle("test2");
            return mockServerWebSocket;
        });

        StepVerifier.create(getWebSocketSession().receive())
            .expectNext(getWebSocketMessage(WebSocketMessage.Type.TEXT, "test1"))
            .expectNext(getWebSocketMessage(WebSocketMessage.Type.TEXT, "test2"))
            .verifyComplete();
    }

    @Test
    public void shouldReceiveBinaryMessages() {
        initMockSocketHandlers();
        given(mockServerWebSocket.binaryMessageHandler(any())).will(invocation -> {
            Handler<Buffer> handler = invocation.getArgument(0);
            handler.handle(Buffer.buffer("test1"));
            handler.handle(Buffer.buffer("test2"));
            return mockServerWebSocket;
        });

        StepVerifier.create(getWebSocketSession().receive())
            .expectNext(getWebSocketMessage(WebSocketMessage.Type.BINARY, "test1"))
            .expectNext(getWebSocketMessage(WebSocketMessage.Type.BINARY, "test2"))
            .verifyComplete();
    }

    @Test
    public void shouldReceivePongMessages() {
        initMockSocketHandlers();
        given(mockServerWebSocket.pongHandler(any())).will(invocation -> {
            Handler<Buffer> handler = invocation.getArgument(0);
            handler.handle(Buffer.buffer("test1"));
            handler.handle(Buffer.buffer("test2"));
            return mockServerWebSocket;
        });

        StepVerifier.create(getWebSocketSession().receive())
            .expectNext(getWebSocketMessage(WebSocketMessage.Type.PONG, "test1"))
            .expectNext(getWebSocketMessage(WebSocketMessage.Type.PONG, "test2"))
            .verifyComplete();
    }

    @Test
    public void shouldSendTextMessage() {
        TestPublisher<WebSocketMessage> source = TestPublisher.create();
        Mono<Void> result = getWebSocketSession().send(source);

        StepVerifier.create(result)
            .expectSubscription()
            .then(() -> source.assertMinRequested(1))
            .then(() -> source.next(getWebSocketMessage(WebSocketMessage.Type.TEXT, "test1")))
            .then(() -> source.assertMinRequested(1))
            .then(() -> source.next(getWebSocketMessage(WebSocketMessage.Type.TEXT, "test2")))
            .then(() -> source.assertMinRequested(1))
            .then(source::complete)
            .verifyComplete();

        verify(mockServerWebSocket).writeTextMessage("test1");
        verify(mockServerWebSocket).writeTextMessage("test2");
    }

    @Test
    public void shouldSendBinaryMessage() {
        TestPublisher<WebSocketMessage> source = TestPublisher.create();
        Mono<Void> result = getWebSocketSession().send(source);

        StepVerifier.create(result)
            .expectSubscription()
            .then(() -> source.assertMinRequested(1))
            .then(() -> source.next(getWebSocketMessage(WebSocketMessage.Type.BINARY, "test1")))
            .then(() -> source.assertMinRequested(1))
            .then(() -> source.next(getWebSocketMessage(WebSocketMessage.Type.BINARY, "test2")))
            .then(() -> source.assertMinRequested(1))
            .then(source::complete)
            .verifyComplete();

        verify(mockServerWebSocket).writeBinaryMessage(Buffer.buffer("test1"));
        verify(mockServerWebSocket).writeBinaryMessage(Buffer.buffer("test2"));
    }

    @Test
    public void shouldSendPingMessage() {
        TestPublisher<WebSocketMessage> source = TestPublisher.create();
        Mono<Void> result = getWebSocketSession().send(source);

        StepVerifier.create(result)
            .expectSubscription()
            .then(() -> source.assertMinRequested(1))
            .then(() -> source.next(getWebSocketMessage(WebSocketMessage.Type.PING, "test1")))
            .then(() -> source.assertMinRequested(1))
            .then(() -> source.next(getWebSocketMessage(WebSocketMessage.Type.PING, "test2")))
            .then(() -> source.assertMinRequested(1))
            .then(source::complete)
            .verifyComplete();

        verify(mockServerWebSocket).writePing(Buffer.buffer("test1"));
        verify(mockServerWebSocket).writePing(Buffer.buffer("test2"));
    }

    @Test
    public void shouldSendPongMessage() {
        TestPublisher<WebSocketMessage> source = TestPublisher.create();
        Mono<Void> result = getWebSocketSession().send(source);

        StepVerifier.create(result)
            .expectSubscription()
            .then(() -> source.assertMinRequested(1))
            .then(() -> source.next(getWebSocketMessage(WebSocketMessage.Type.PONG, "test1")))
            .then(() -> source.assertMinRequested(1))
            .then(() -> source.next(getWebSocketMessage(WebSocketMessage.Type.PONG, "test2")))
            .then(() -> source.assertMinRequested(1))
            .then(source::complete)
            .verifyComplete();

        verify(mockServerWebSocket).writePong(Buffer.buffer("test1"));
        verify(mockServerWebSocket).writePong(Buffer.buffer("test2"));
    }

    @Test
    public void shouldClose() {
        given(mockServerWebSocket.closeHandler(any())).will(invocation -> {
            Handler<Void> handler = invocation.getArgument(0);
            handler.handle(null);
            return mockServerWebSocket;
        });

        getWebSocketSession().close(new CloseStatus(1000, "test")).block();

        verify(mockServerWebSocket).close((short) 1000, "test");
    }

    private VertxWebSocketSession getWebSocketSession() {
        return new VertxWebSocketSession(mockServerWebSocket, mockHandshakeInfo, bufferConverter, 1, 1);
    }

    private WebSocketMessage getWebSocketMessage(WebSocketMessage.Type type, String data) {
        DataBuffer dataBuffer = bufferConverter.toDataBuffer(Buffer.buffer(data));

        return new WebSocketMessage(type, dataBuffer);
    }

    private void initMockSocketHandlers() {
        given(mockServerWebSocket.pause()).willReturn(mockServerWebSocket);
        given(mockServerWebSocket.textMessageHandler(any())).willReturn(mockServerWebSocket);
        given(mockServerWebSocket.binaryMessageHandler(any())).willReturn(mockServerWebSocket);
        given(mockServerWebSocket.pongHandler(any())).willReturn(mockServerWebSocket);
        given(mockServerWebSocket.exceptionHandler(any())).willReturn(mockServerWebSocket);
        given(mockServerWebSocket.endHandler(any())).will(invocation -> {
            Handler<Void> handler = invocation.getArgument(0);
            handler.handle(null);
            return mockServerWebSocket;
        });
    }
}
