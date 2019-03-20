package me.snowdrop.vertx.http;

import java.net.InetSocketAddress;
import java.net.URI;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.net.SocketAddress;
import io.vertx.ext.web.RoutingContext;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.AbstractServerHttpRequest;
import org.springframework.http.server.reactive.SslInfo;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;

public class VertxServerHttpRequest extends AbstractServerHttpRequest {

    private final RoutingContext context;

    private final HttpServerRequest request;

    private final NettyDataBufferFactory dataBufferFactory;

    public VertxServerHttpRequest(RoutingContext context, NettyDataBufferFactory dataBufferFactory) {
        super(initUri(context.request()), "", initHeaders(context.request()));
        this.context = context;
        this.request = context.request();
        this.dataBufferFactory = dataBufferFactory;
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
            request
                .pause()
                .handler(chunk -> {
                    DataBuffer dataBuffer = dataBufferFactory.wrap(chunk.getByteBuf());
                    sink.next(dataBuffer);
                })
                .exceptionHandler(sink::error)
                .endHandler(v -> sink.complete());
            sink.onRequest(request::fetch);
        });
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        SocketAddress address = request.remoteAddress();
        return InetSocketAddress.createUnresolved(address.host(), address.port());
    }

    @Override
    protected MultiValueMap<String, HttpCookie> initCookies() {
        MultiValueMap<String, HttpCookie> cookies = new LinkedMultiValueMap<>();

        context.cookies()
            .stream()
            .map(cookie -> new HttpCookie(cookie.getName(), cookie.getValue()))
            .forEach(cookie -> cookies.add(cookie.getName(), cookie));

        return cookies;
    }

    @Override
    protected SslInfo initSslInfo() {
        if (request.sslSession() == null) {
            return null;
        }

        return new SslInfoImpl(request.sslSession());
    }

    private static URI initUri(HttpServerRequest request) {
        return URI.create(request.absoluteURI());
    }

    private static HttpHeaders initHeaders(HttpServerRequest request) {
        HttpHeaders headers = new HttpHeaders();
        request.headers()
            .forEach(e -> headers.add(e.getKey(), e.getValue()));

        return headers;
    }
}
