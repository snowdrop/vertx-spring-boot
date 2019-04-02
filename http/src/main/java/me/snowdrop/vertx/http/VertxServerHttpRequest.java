package me.snowdrop.vertx.http;

import java.net.InetSocketAddress;
import java.net.URI;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.net.SocketAddress;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.AbstractServerHttpRequest;
import org.springframework.http.server.reactive.SslInfo;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;

public class VertxServerHttpRequest extends AbstractServerHttpRequest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final RoutingContext context;

    private final HttpServerRequest request;

    private final BufferConverter bufferConverter;

    public VertxServerHttpRequest(RoutingContext context, BufferConverter bufferConverter) {
        super(initUri(context.request()), "", initHeaders(context.request()));
        this.context = context;
        this.request = context.request();
        this.bufferConverter = bufferConverter;
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
            logger.debug("Connecting to a request body read stream");
            request
                .pause()
                .handler(chunk -> {
                    logger.debug("Received '{}' from a request body read stream", chunk);
                    sink.next(bufferConverter.toDataBuffer(chunk));
                })
                .exceptionHandler(throwable -> {
                    logger.debug("Received exception '{}' from a request body read stream", throwable);
                    sink.error(throwable);
                })
                .endHandler(v -> {
                    logger.debug("Request body read stream ended");
                    sink.complete();
                });
            sink.onRequest(i -> {
                logger.debug("Fetching '{}' entries from a request body read stream", i);
                request.fetch(i);
            });
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
            .map(CookieConverter::toHttpCookie)
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
