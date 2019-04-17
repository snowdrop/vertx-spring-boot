package me.snowdrop.vertx.http.common;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.WebSocketBase;
import me.snowdrop.vertx.http.utils.BufferConverter;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.util.ObjectUtils;
import org.springframework.web.reactive.socket.CloseStatus;
import org.springframework.web.reactive.socket.HandshakeInfo;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.adapter.AbstractWebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class VertxWebSocketSession extends AbstractWebSocketSession<WebSocketBase> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final BufferConverter bufferConverter;

    public VertxWebSocketSession(WebSocketBase delegate, HandshakeInfo handshakeInfo, BufferConverter bufferConverter) {
        super(delegate, ObjectUtils.getIdentityHexString(delegate), handshakeInfo,
            bufferConverter.getDataBufferFactory());
        this.bufferConverter = bufferConverter;
    }

    @Override
    public Flux<WebSocketMessage> receive() {
        return Flux.create(sink -> {
                logger.debug("{}Connecting to a web socket read stream", getLogPrefix());
                WebSocketBase socket = getDelegate();
                socket.pause()
                    .textMessageHandler(payload -> {
                        logger.debug("{}Received text '{}' from a web socket read stream", getLogPrefix(), payload);
                        sink.next(textMessage(payload));
                    })
                    .binaryMessageHandler(payload -> {
                        logger.debug("{}Received binary '{}' from a web socket read stream", getLogPrefix(), payload);
                        sink.next(binaryMessage(payload));
                    })
                    .pongHandler(payload -> {
                        logger.debug("{}Received pong '{}' from a web socket read stream", getLogPrefix(), payload);
                        sink.next(pongMessage(payload));
                    })
                    .exceptionHandler(throwable -> {
                        logger.debug("{}Received exception '{}' from a web socket read stream", getLogPrefix(), throwable);
                        sink.error(throwable);
                    })
                    .endHandler(e -> {
                        logger.debug("{}Web socket read stream ended", getLogPrefix());
                        sink.complete();
                    });
                sink.onRequest(i -> {
                    logger.debug("{}Fetching '{}' entries from a web socket read stream", getLogPrefix(), i);
                    socket.fetch(i);
                });
            }
        );
    }

    @Override
    public Mono<Void> send(Publisher<WebSocketMessage> messages) {
        return Mono.create(sink -> {
            logger.debug("{}Subscribing to messages publisher", getLogPrefix());
            Subscriber<WebSocketMessage> subscriber =
                new WriteStreamSubscriber.Builder<WebSocketBase, WebSocketMessage>()
                    .writeStream(getDelegate())
                    .nextHandler(this::messageHandler)
                    .endHook(sink)
                    .build();
            messages.subscribe(subscriber);
        });
    }

    @Override
    public Mono<Void> close(CloseStatus status) {
        logger.debug("{}Closing web socket with status '{}'", getLogPrefix(), status);
        return Mono.create(sink -> getDelegate()
            .closeHandler(e -> {
                logger.debug("{}Web socket closed", getLogPrefix());
                sink.success();
            })
            .close((short) status.getCode(), status.getReason()));
    }

    private void messageHandler(WebSocketBase socket, WebSocketMessage message) {
        if (message.getType() == WebSocketMessage.Type.TEXT) {
            String payload = message.getPayloadAsText();
            socket.writeTextMessage(payload);
        } else {
            Buffer buffer = bufferConverter.toBuffer(message.getPayload());

            if (message.getType() == WebSocketMessage.Type.PING) {
                socket.writePing(buffer);
            } else if (message.getType() == WebSocketMessage.Type.PONG) {
                socket.writePong(buffer);
            } else {
                socket.writeBinaryMessage(buffer);
            }
        }
    }

    private WebSocketMessage binaryMessage(Buffer payloadBuffer) {
        DataBuffer payload = bufferConverter.toDataBuffer(payloadBuffer);
        return new WebSocketMessage(WebSocketMessage.Type.BINARY, payload);
    }

    private WebSocketMessage pongMessage(Buffer payloadBuffer) {
        DataBuffer payload = bufferConverter.toDataBuffer(payloadBuffer);
        return new WebSocketMessage(WebSocketMessage.Type.PONG, payload);
    }
}
