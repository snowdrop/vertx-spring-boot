package me.snowdrop.vertx.http;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.WebSocketBase;
import org.reactivestreams.Publisher;
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
            messages.subscribe(new PublisherToWebSocketConnector(getDelegate(), sink, bufferConverter));
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

    private WebSocketMessage binaryMessage(Buffer payloadBuffer) {
        DataBuffer payload = bufferConverter.toDataBuffer(payloadBuffer);
        return new WebSocketMessage(WebSocketMessage.Type.BINARY, payload);
    }

    private WebSocketMessage pongMessage(Buffer payloadBuffer) {
        DataBuffer payload = bufferConverter.toDataBuffer(payloadBuffer);
        return new WebSocketMessage(WebSocketMessage.Type.PONG, payload);
    }
}
