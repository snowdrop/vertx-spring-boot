package me.snowdrop.vertx.http;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.ServerWebSocket;
import org.springframework.web.reactive.socket.WebSocketMessage;
import reactor.core.publisher.MonoSink;

import static me.snowdrop.vertx.http.Utils.dataBufferToBuffer;

public class ServerWebSocketSubscriber extends AbstractWriteStreamSubscriber<ServerWebSocket, WebSocketMessage> {

    public ServerWebSocketSubscriber(ServerWebSocket delegate, MonoSink endHook) {
        super(delegate, endHook);
    }

    @Override
    protected void hookOnNext(WebSocketMessage message) {
        if (message.getType() == WebSocketMessage.Type.TEXT) {
            String payload = message.getPayloadAsText();
            getDelegate().writeTextMessage(payload);
        } else {
            Buffer buffer = dataBufferToBuffer(message.getPayload());

            if (message.getType() == WebSocketMessage.Type.PING) {
                getDelegate().writePing(buffer);
            } else if (message.getType() == WebSocketMessage.Type.PONG) {
                getDelegate().writePong(buffer);
            } else {
                getDelegate().writeBinaryMessage(buffer);
            }
        }
        super.hookOnNext(message);
    }
}
