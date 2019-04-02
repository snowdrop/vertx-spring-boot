package me.snowdrop.vertx.http;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.WebSocketBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.socket.WebSocketMessage;
import reactor.core.publisher.MonoSink;

import static me.snowdrop.vertx.http.Utils.dataBufferToBuffer;

public class WebSocketSubscriber extends AbstractWriteStreamSubscriber<WebSocketBase, WebSocketMessage> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public WebSocketSubscriber(WebSocketBase delegate, MonoSink endHook) {
        super(delegate, endHook);
    }

    @Override
    protected void hookOnNext(WebSocketMessage message) {
        logger.debug("{}Next message: {}", getLogPrefix(), message);

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
