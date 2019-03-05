package me.snowdrop.vertx.http;

import java.nio.file.Path;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.RoutingContext;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ZeroCopyHttpOutputMessage;
import org.springframework.http.server.reactive.AbstractServerHttpResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.util.function.Function.identity;

public class VertxServerHttpResponse extends AbstractServerHttpResponse implements ZeroCopyHttpOutputMessage {

    private final RoutingContext context;

    private final HttpServerResponse response;

    public VertxServerHttpResponse(RoutingContext context, NettyDataBufferFactory dataBufferFactory) {
        // TODO add headers
        super(dataBufferFactory);
        this.context = context;
        this.response = context.response();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getNativeResponse() {
        return (T) response;
    }

    @Override
    public Mono<Void> writeWith(Path file, long position, long count) {
        response.sendFile(file.toString(), position, count);
        return Mono.empty();
    }

    @Override
    protected Mono<Void> writeWithInternal(Publisher<? extends DataBuffer> chunks) {
        return Flux.from(chunks)
            .map(NettyDataBufferFactory::toByteBuf)
            .map(Buffer::buffer)
            .doOnNext(response::write)
            .then();
    }

    @Override
    protected Mono<Void> writeAndFlushWithInternal(Publisher<? extends Publisher<? extends DataBuffer>> chunks) {
        return writeWithInternal(Flux.from(chunks).flatMap(identity())); // TODO
    }

    @Override
    protected void applyStatusCode() {
        HttpStatus statusCode = getStatusCode();
        if (statusCode != null) {
            response.setStatusCode(statusCode.value());
        }
    }

    @Override
    protected void applyHeaders() {
        HttpHeaders headers = getHeaders();
        if (!headers.containsKey(HttpHeaders.CONTENT_LENGTH)) {
            response.setChunked(true);
        }
        headers.forEach(response::putHeader);
    }

    @Override
    protected void applyCookies() {
        getCookies()
            .toSingleValueMap() // Vert.x doesn't support multi-value cookies
            .values()
            .stream()
            .map(this::cookieMapper)
            .forEach(context::addCookie);
    }

    private Cookie cookieMapper(ResponseCookie responseCookie) {
        return Cookie.cookie(responseCookie.getName(), responseCookie.getValue())
            .setDomain(responseCookie.getDomain())
            .setPath(responseCookie.getPath())
            .setMaxAge(responseCookie.getMaxAge().getSeconds())
            .setHttpOnly(responseCookie.isHttpOnly())
            .setSecure(responseCookie.isSecure());
    }
}
