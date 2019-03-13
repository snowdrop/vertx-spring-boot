package me.snowdrop.vertx.http;

import io.netty.buffer.ByteBuf;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.ServerWebSocket;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.util.ObjectUtils;
import org.springframework.web.reactive.socket.CloseStatus;
import org.springframework.web.reactive.socket.HandshakeInfo;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.adapter.AbstractWebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class VertxWebSocketSession extends AbstractWebSocketSession<ServerWebSocket> {

    private final NettyDataBufferFactory dataBufferFactory;

    public VertxWebSocketSession(ServerWebSocket delegate, HandshakeInfo handshakeInfo,
        NettyDataBufferFactory dataBufferFactory) {
        super(delegate, ObjectUtils.getIdentityHexString(delegate), handshakeInfo, dataBufferFactory);
        this.dataBufferFactory = dataBufferFactory;
    }

    @Override
    public Flux<WebSocketMessage> receive() {
        return Flux.create(sink -> getDelegate()
                .textMessageHandler(payload -> sink.next(textMessage(payload)))
                .binaryMessageHandler(payload -> sink.next(binaryMessageAdapter(payload)))
                .pongHandler(payload -> sink.next(pongMessageAdapter(payload)))
        );
    }

    @Override
    public Mono<Void> send(Publisher<WebSocketMessage> messages) {
        return Flux.from(messages)
            .doOnNext(this::sendMessage)
            .then();
    }

    @Override
    public Mono<Void> close(CloseStatus status) {
        return Mono.create(sink -> {
            getDelegate()
                .close((short) status.getCode(), status.getReason());
            sink.success();
        });
    }

    private WebSocketMessage binaryMessageAdapter(Buffer payload) {
        ByteBuf byteBuf = payload.getByteBuf();
        DataBuffer dataBuffer = dataBufferFactory.wrap(byteBuf);

        return new WebSocketMessage(WebSocketMessage.Type.BINARY, dataBuffer);
    }

    private WebSocketMessage pongMessageAdapter(Buffer payload) {
        ByteBuf byteBuf = payload.getByteBuf();
        DataBuffer dataBuffer = dataBufferFactory.wrap(byteBuf);

        return new WebSocketMessage(WebSocketMessage.Type.PONG, dataBuffer);
    }

    private void sendMessage(WebSocketMessage message) {
        if (message.getType() == WebSocketMessage.Type.TEXT) {
            getDelegate().writeTextMessage(message.getPayloadAsText());
            return;
        }

        ByteBuf byteBuf = NettyDataBufferFactory.toByteBuf(message.getPayload());
        Buffer buffer = Buffer.buffer(byteBuf);

        if (message.getType() == WebSocketMessage.Type.PING) {
            getDelegate().writePing(buffer);
        } else if (message.getType() == WebSocketMessage.Type.PONG) {
            getDelegate().writePong(buffer);
        } else {
            getDelegate().writeBinaryMessage(buffer);
        }
    }
}
