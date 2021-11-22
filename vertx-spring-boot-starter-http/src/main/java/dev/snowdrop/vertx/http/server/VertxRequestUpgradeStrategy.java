package dev.snowdrop.vertx.http.server;

import dev.snowdrop.vertx.http.common.VertxWebSocketSession;
import dev.snowdrop.vertx.http.utils.BufferConverter;
import io.vertx.core.http.HttpServerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.reactive.AbstractServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.reactive.socket.HandshakeInfo;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.RequestUpgradeStrategy;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.function.Supplier;

public class VertxRequestUpgradeStrategy implements RequestUpgradeStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(VertxRequestUpgradeStrategy.class);

    private final BufferConverter bufferConverter;

    private final int maxWebSocketFrameSize;

    private final int maxWebSocketMessageSize;

    public VertxRequestUpgradeStrategy(int maxWebSocketFrameSize, int maxWebSocketMessageSize) {
        this.bufferConverter = new BufferConverter();
        this.maxWebSocketFrameSize = maxWebSocketFrameSize;
        this.maxWebSocketMessageSize = maxWebSocketMessageSize;
    }

    @Override
    public Mono<Void> upgrade(ServerWebExchange exchange, WebSocketHandler handler,
                              @Nullable String subProtocol, Supplier<HandshakeInfo> handshakeInfoFactory) {
        LOGGER.debug("Upgrading request to web socket");

        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        HttpServerRequest vertxRequest = ((AbstractServerHttpRequest) request).getNativeRequest();
        HandshakeInfo handshakeInfo = handshakeInfoFactory.get();
        URI uri = exchange.getRequest().getURI();

        return response.setComplete().then(Mono.fromCompletionStage(vertxRequest.toWebSocket().toCompletionStage())
            .flatMap(ws -> {
                VertxWebSocketSession session = new VertxWebSocketSession(ws, handshakeInfo, bufferConverter, maxWebSocketFrameSize, maxWebSocketMessageSize);
                return handler.handle(session).checkpoint(uri + " [VertxRequestUpgradeStrategy]");
            }));
    }

}
