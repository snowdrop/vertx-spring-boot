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
        return Flux.create(sink -> {
                ServerWebSocket socket = getDelegate();
                socket.pause()
                    .textMessageHandler(payload -> sink.next(textMessage(payload)))
                    .binaryMessageHandler(payload -> sink.next(binaryMessageAdapter(payload)))
                    .pongHandler(payload -> sink.next(pongMessageAdapter(payload)))
                    .exceptionHandler(sink::error)
                    .endHandler(e -> sink.complete());
                sink.onRequest(socket::fetch);
            }
        );
    }

    @Override
    public Mono<Void> send(Publisher<WebSocketMessage> messages) {
        return Mono.create(sink -> messages.subscribe(new ServerWebSocketSubscriber(getDelegate(), sink)));
    }

    @Override
    public Mono<Void> close(CloseStatus status) {
        return Mono.create(sink -> getDelegate()
            .closeHandler(e -> sink.success())
            .close((short) status.getCode(), status.getReason()));
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
}
