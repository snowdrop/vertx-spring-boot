package dev.snowdrop.vertx.http.server;

import java.util.function.Supplier;

import dev.snowdrop.vertx.http.utils.BufferConverter;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.ServerWebSocket;
import dev.snowdrop.vertx.http.common.VertxWebSocketSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.reactive.AbstractServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.web.reactive.socket.HandshakeInfo;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.RequestUpgradeStrategy;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class VertxRequestUpgradeStrategy implements RequestUpgradeStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(VertxRequestUpgradeStrategy.class);

    private final BufferConverter bufferConverter;

    public VertxRequestUpgradeStrategy() {
        this.bufferConverter = new BufferConverter();
    }

    @Override
    public Mono<Void> upgrade(ServerWebExchange exchange, WebSocketHandler handler,
        @Nullable String subProtocol, Supplier<HandshakeInfo> handshakeInfoFactory) {

        LOGGER.debug("Upgrading request to web socket");

        ServerHttpRequest request = exchange.getRequest();
        HttpServerRequest vertxRequest = ((AbstractServerHttpRequest) request).getNativeRequest();

        ServerWebSocket webSocket = vertxRequest.upgrade();
        VertxWebSocketSession session =
            new VertxWebSocketSession(webSocket, handshakeInfoFactory.get(), bufferConverter);

        return handler.handle(session);
    }

}
