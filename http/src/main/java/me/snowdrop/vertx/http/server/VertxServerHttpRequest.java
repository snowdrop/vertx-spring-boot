package me.snowdrop.vertx.http.server;

import java.net.InetSocketAddress;
import java.net.URI;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.net.SocketAddress;
import io.vertx.ext.web.RoutingContext;
import me.snowdrop.vertx.http.common.ReadStreamFluxBuilder;
import me.snowdrop.vertx.http.utils.BufferConverter;
import me.snowdrop.vertx.http.utils.CookieConverter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.AbstractServerHttpRequest;
import org.springframework.http.server.reactive.SslInfo;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;

public class VertxServerHttpRequest extends AbstractServerHttpRequest {

    private final RoutingContext context;

    private final HttpServerRequest delegate;

    private final Flux<DataBuffer> bodyFlux;

    public VertxServerHttpRequest(RoutingContext context, BufferConverter bufferConverter) {
        super(initUri(context.request()), "", initHeaders(context.request()));
        this.context = context;
        this.delegate = context.request();
        this.bodyFlux = new ReadStreamFluxBuilder<Buffer, DataBuffer>()
            .readStream(delegate)
            .dataConverter(bufferConverter::toDataBuffer)
            .build();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getNativeRequest() {
        return (T) delegate;
    }

    @Override
    public String getMethodValue() {
        return delegate.method().name();
    }

    @Override
    public Flux<DataBuffer> getBody() {
        return bodyFlux;
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        SocketAddress address = delegate.remoteAddress();
        if (address == null) {
            return null;
        }
        return new InetSocketAddress(address.host(), address.port());
    }

    @Override
    protected MultiValueMap<String, HttpCookie> initCookies() {
        MultiValueMap<String, HttpCookie> cookies = new LinkedMultiValueMap<>();

        context.cookies()
            .stream()
            .map(CookieConverter::toHttpCookie)
            .forEach(cookie -> cookies.add(cookie.getName(), cookie));

        return cookies;
    }

    @Override
    protected SslInfo initSslInfo() {
        if (delegate.sslSession() == null) {
            return null;
        }

        return new SslInfoImpl(delegate.sslSession());
    }

    private static URI initUri(HttpServerRequest request) { // TODO refactor
        return URI.create(request.absoluteURI());
    }

    private static HttpHeaders initHeaders(HttpServerRequest request) {
        HttpHeaders headers = new HttpHeaders();
        request.headers()
            .forEach(e -> headers.add(e.getKey(), e.getValue()));

        return headers;
    }
}
