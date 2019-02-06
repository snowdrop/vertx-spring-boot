package me.snowdrop.vertx.http;

import java.net.InetSocketAddress;
import java.net.URI;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.net.SocketAddress;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.AbstractServerHttpRequest;
import org.springframework.http.server.reactive.SslInfo;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;

public class VertxServerHttpRequest extends AbstractServerHttpRequest {

    private final HttpServerRequest request;

    private final NettyDataBufferFactory dataBufferFactory;

    public VertxServerHttpRequest(HttpServerRequest request, NettyDataBufferFactory dataBufferFactory) {
        super(initUri(request), request.path(), initHeaders(request));
        this.request = request;
        this.dataBufferFactory = dataBufferFactory;
    }

    @Override
    protected MultiValueMap<String, HttpCookie> initCookies() {
        return null; // TODO
    }

    @Override
    protected SslInfo initSslInfo() {
        return null; // TODO
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getNativeRequest() {
        return (T) request;
    }

    @Override
    public String getMethodValue() {
        return request.method().name();
    }

    @Override
    public Flux<DataBuffer> getBody() {
        return Flux.create(sink -> {
            request.handler(chunk -> {
                DataBuffer dataBuffer = dataBufferFactory.wrap(chunk.getByteBuf());
                sink.next(dataBuffer);
            });

            request.endHandler(v -> sink.complete());
        });
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        SocketAddress address = request.remoteAddress();
        return InetSocketAddress.createUnresolved(address.host(), address.port());
    }

    private static URI initUri(HttpServerRequest request) {
        return URI.create(request.uri());
    }

    private static HttpHeaders initHeaders(HttpServerRequest request) {
        HttpHeaders headers = new HttpHeaders();
        request.headers()
            .forEach(e -> headers.add(e.getKey(), e.getValue()));

        return headers;
    }
}
