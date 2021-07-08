package dev.snowdrop.vertx.http.client;

import java.net.URI;

import dev.snowdrop.vertx.http.common.VertxWebSocketSession;
import dev.snowdrop.vertx.http.utils.BufferConverter;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.WebSocket;
import io.vertx.core.http.WebSocketConnectOptions;
import org.springframework.http.HttpHeaders;
import org.springframework.util.Assert;
import org.springframework.web.reactive.socket.HandshakeInfo;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

public class VertxWebSocketClient implements WebSocketClient {

    private final Vertx vertx;

    private final HttpClientOptions clientOptions;

    private final BufferConverter bufferConverter;

    public VertxWebSocketClient(Vertx vertx) {
        Assert.notNull(vertx, "Vertx is required");
        this.vertx = vertx;
        this.clientOptions = new HttpClientOptions();
        this.bufferConverter = new BufferConverter();
    }

    public VertxWebSocketClient(Vertx vertx, HttpClientOptions options) {
        Assert.notNull(vertx, "Vertx is required");
        this.vertx = vertx;
        this.clientOptions = options;
        this.bufferConverter = new BufferConverter();
    }

    @Override
    public Mono<Void> execute(URI uri, WebSocketHandler handler) {
        return execute(uri, new HttpHeaders(), handler);
    }

    @Override
    public Mono<Void> execute(URI uri, HttpHeaders headers, WebSocketHandler handler) {
        MultiMap vertxHeaders = convertHeaders(headers);

        return Mono.create(sink -> connect(uri, vertxHeaders, handler, sink));
    }

    private void connect(URI uri, MultiMap headers, WebSocketHandler handler, MonoSink<Void> callback) {
        HttpClient client = vertx.createHttpClient(clientOptions);
        WebSocketConnectOptions options = new WebSocketConnectOptions()
            .setPort(uri.getPort())
            .setHost(uri.getHost())
            .setURI(uri.getPath())
            .setHeaders(headers);
        client.webSocket(options, result -> {
            if (result.failed()) {
                callback.error(result.cause());
            } else {
                handler.handle(initSession(uri, result.result()))
                    .doOnSuccess(callback::success)
                    .doOnError(callback::error)
                    .doFinally(ignore -> client.close())
                    .subscribe();
            }
        });
    }

    private MultiMap convertHeaders(HttpHeaders headers) {
        MultiMap vertxHeaders = MultiMap.caseInsensitiveMultiMap();
        headers.forEach(vertxHeaders::add);
        return vertxHeaders;
    }

    private VertxWebSocketSession initSession(URI uri, WebSocket socket) {
        // Vert.x handshake doesn't return headers so passing an empty collection
        HandshakeInfo handshakeInfo = new HandshakeInfo(uri, new HttpHeaders(), Mono.empty(), socket.subProtocol());

        return new VertxWebSocketSession(socket, handshakeInfo, bufferConverter,
            clientOptions.getMaxWebSocketFrameSize(), clientOptions.getMaxWebSocketMessageSize());
    }
}
