package me.snowdrop.vertx.http.client;

import java.net.URI;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.WebSocket;
import io.vertx.core.http.impl.headers.VertxHttpHeaders;
import me.snowdrop.vertx.http.utils.BufferConverter;
import me.snowdrop.vertx.http.common.VertxWebSocketSession;
import org.springframework.http.HttpHeaders;
import org.springframework.util.Assert;
import org.springframework.web.reactive.socket.HandshakeInfo;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

public class VertxWebSocketClient implements WebSocketClient {

    private final HttpClient httpClient;

    private final BufferConverter bufferConverter;

    public VertxWebSocketClient(Vertx vertx) {
        this(vertx.createHttpClient());
    }

    public VertxWebSocketClient(Vertx vertx, HttpClientOptions options) {
        this(vertx.createHttpClient(options));
    }

    public VertxWebSocketClient(HttpClient httpClient) {
        Assert.notNull(httpClient, "HttpClient is required");
        this.httpClient = httpClient;
        this.bufferConverter = new BufferConverter();
    }

    @Override
    public Mono<Void> execute(URI uri, WebSocketHandler handler) {
        return execute(uri, new HttpHeaders(), handler);
    }

    @Override
    public Mono<Void> execute(URI uri, HttpHeaders headers, WebSocketHandler handler) {
        VertxHttpHeaders vertxHeaders = convertHeaders(headers);

        return Mono.create(sink -> connect(uri, vertxHeaders, handler, sink));
    }

    private void connect(URI uri, VertxHttpHeaders headers, WebSocketHandler handler, MonoSink<Void> callback) {
        httpClient.websocket(uri.getPort(), uri.getHost(), uri.getPath(), headers,
            socket -> handler.handle(initSession(uri, socket))
                .doOnSuccess(callback::success)
                .doOnError(callback::error)
                .subscribe()
        );
    }

    private VertxHttpHeaders convertHeaders(HttpHeaders headers) {
        VertxHttpHeaders vertxHeaders = new VertxHttpHeaders();
        headers.forEach(vertxHeaders::add);

        return vertxHeaders;
    }

    private VertxWebSocketSession initSession(URI uri, WebSocket socket) {
        // Vert.x handshake doesn't return headers so passing an empty collection
        HandshakeInfo handshakeInfo = new HandshakeInfo(uri, new HttpHeaders(), Mono.empty(), socket.subProtocol());

        return new VertxWebSocketSession(socket, handshakeInfo, bufferConverter);
    }
}
