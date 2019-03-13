package me.snowdrop.vertx.http;

import java.nio.charset.StandardCharsets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.ServerWebSocket;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.web.reactive.socket.CloseStatus;
import org.springframework.web.reactive.socket.HandshakeInfo;
import org.springframework.web.reactive.socket.WebSocketMessage;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class VertxWebSocketSessionTest {

    @Mock
    private ServerWebSocket mockServerWebSocket;

    @Mock
    private HandshakeInfo mockHandshakeInfo;

    private NettyDataBufferFactory nettyDataBufferFactory = new NettyDataBufferFactory(ByteBufAllocator.DEFAULT);

    private VertxWebSocketSession session;

    @Before
    public void setUp() {
        session = new VertxWebSocketSession(mockServerWebSocket, mockHandshakeInfo, nettyDataBufferFactory);
    }

    @Test
    public void shouldReceiveTextMessages() {
        given(mockServerWebSocket.binaryMessageHandler(any())).willReturn(mockServerWebSocket);
        given(mockServerWebSocket.pongHandler(any())).willReturn(mockServerWebSocket);
        given(mockServerWebSocket.textMessageHandler(any())).will(invocation -> {
            Handler<String> handler = invocation.getArgument(0);
            handler.handle("test1");
            handler.handle("test2");
            return mockServerWebSocket;
        });

        StepVerifier.create(session.receive())
            .expectNext(new WebSocketMessage(WebSocketMessage.Type.TEXT, getDataBuffer("test1")))
            .expectNext(new WebSocketMessage(WebSocketMessage.Type.TEXT, getDataBuffer("test2")))
            .thenCancel()
            .verify();
    }

    @Test
    public void shouldReceiveBinaryMessages() {
        given(mockServerWebSocket.textMessageHandler(any())).willReturn(mockServerWebSocket);
        given(mockServerWebSocket.pongHandler(any())).willReturn(mockServerWebSocket);
        given(mockServerWebSocket.binaryMessageHandler(any())).will(invocation -> {
            Handler<Buffer> handler = invocation.getArgument(0);
            handler.handle(Buffer.buffer("test1"));
            handler.handle(Buffer.buffer("test2"));
            return mockServerWebSocket;
        });

        StepVerifier.create(session.receive())
            .expectNext(new WebSocketMessage(WebSocketMessage.Type.BINARY, getDataBuffer(Buffer.buffer("test1"))))
            .expectNext(new WebSocketMessage(WebSocketMessage.Type.BINARY, getDataBuffer(Buffer.buffer("test2"))))
            .thenCancel()
            .verify();
    }

    @Test
    public void shouldReceivePongMessages() {
        given(mockServerWebSocket.textMessageHandler(any())).willReturn(mockServerWebSocket);
        given(mockServerWebSocket.binaryMessageHandler(any())).willReturn(mockServerWebSocket);
        given(mockServerWebSocket.pongHandler(any())).will(invocation -> {
            Handler<Buffer> handler = invocation.getArgument(0);
            handler.handle(Buffer.buffer("test1"));
            handler.handle(Buffer.buffer("test2"));
            return mockServerWebSocket;
        });

        StepVerifier.create(session.receive())
            .expectNext(new WebSocketMessage(WebSocketMessage.Type.PONG, getDataBuffer(Buffer.buffer("test1"))))
            .expectNext(new WebSocketMessage(WebSocketMessage.Type.PONG, getDataBuffer(Buffer.buffer("test2"))))
            .thenCancel()
            .verify();
    }

    @Test
    public void shouldSendTextMessage() {
        Flux<WebSocketMessage> messages =
            Flux.just(new WebSocketMessage(WebSocketMessage.Type.TEXT, getDataBuffer("test")));
        session.send(messages).subscribe();

        verify(mockServerWebSocket).writeTextMessage("test");
    }

    @Test
    public void shouldSendBinaryMessage() {
        Flux<WebSocketMessage> messages =
            Flux.just(new WebSocketMessage(WebSocketMessage.Type.BINARY, getDataBuffer(Buffer.buffer("test"))));
        session.send(messages).subscribe();

        verify(mockServerWebSocket).writeBinaryMessage(Buffer.buffer("test"));
    }

    @Test
    public void shouldSendPingMessage() {
        Flux<WebSocketMessage> messages =
            Flux.just(new WebSocketMessage(WebSocketMessage.Type.PING, getDataBuffer(Buffer.buffer("test"))));
        session.send(messages).subscribe();

        verify(mockServerWebSocket).writePing(Buffer.buffer("test"));
    }

    @Test
    public void shouldSendPongMessage() {
        Flux<WebSocketMessage> messages =
            Flux.just(new WebSocketMessage(WebSocketMessage.Type.PONG, getDataBuffer(Buffer.buffer("test"))));
        session.send(messages).subscribe();

        verify(mockServerWebSocket).writePong(Buffer.buffer("test"));
    }

    @Test
    public void shouldClose() {
        StepVerifier.create(session.close(new CloseStatus(1000, "test")))
            .verifyComplete();

        verify(mockServerWebSocket).close((short) 1000, "test");
    }

    private DataBuffer getDataBuffer(String payload) {
        byte[] bytes = payload.getBytes(StandardCharsets.UTF_8);
        return nettyDataBufferFactory.wrap(bytes);
    }

    private DataBuffer getDataBuffer(Buffer payload) {
        ByteBuf byteBuf = payload.getByteBuf();
        return nettyDataBufferFactory.wrap(byteBuf);
    }
}
