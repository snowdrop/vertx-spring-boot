package dev.snowdrop.vertx.http.server;

import java.net.URI;
import java.util.function.Supplier;

import dev.snowdrop.vertx.http.common.VertxWebSocketSession;
import dev.snowdrop.vertx.http.utils.BufferConverter;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.ServerWebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.server.reactive.*;
import org.springframework.lang.Nullable;
import org.springframework.web.reactive.socket.HandshakeInfo;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.adapter.ReactorNettyWebSocketSession;
import org.springframework.web.reactive.socket.server.RequestUpgradeStrategy;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

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
        HttpServerRequest servletRequest = ServerHttpRequestDecorator.getNativeRequest(request);
        HttpServerRequest servletResponse = ServerHttpResponseDecorator.getNativeResponse(response);
        HandshakeInfo handshakeInfo = (HandshakeInfo)handshakeInfoFactory.get();
        DataBufferFactory bufferFactory = response.bufferFactory();

//        HttpServerRequest vertxRequest = ((AbstractServerHttpRequest) request).getNativeRequest();
//        ServerWebSocket webSocket = vertxRequest.upgrade();
        Future<ServerWebSocket> webSocket = servletRequest.toWebSocket().onComplete(serverWebSocketAsyncResult -> serverWebSocketAsyncResult.result()).;
//        URI uri = exchange.getRequest().getURI();

        return response.setComplete().then(Mono.deferContextual((contextView) -> {
            VertxWebSocketSession session = new VertxWebSocketSession(webSocket, handshakeInfoFactory.get(),
                bufferConverter, maxWebSocketFrameSize, maxWebSocketMessageSize);
            return response.sendWebsocket((in, out) -> {
                ReactorNettyWebSocketSession session = new ReactorNettyWebSocketSession(in, out, handshakeInfo, bufferFactory, spec.maxFramePayloadLength());
                return handler.handle(session).checkpoint(uri + " [ReactorNettyRequestUpgradeStrategy]");
            }, spec);
        }));
//        ServerWebSocket webSocket = vertxRequest.toWebSocket().onSuccess(serverWebSocket -> {LOGGER.warn("success: {}",serverWebSocket);}).onFailure(throwable -> {LOGGER.error("Failed to upgrade websocket",throwable);}).result();
        VertxWebSocketSession session = new VertxWebSocketSession(webSocket, handshakeInfoFactory.get(),
            bufferConverter, maxWebSocketFrameSize, maxWebSocketMessageSize);

        return handler.handle(session);
    }

}
