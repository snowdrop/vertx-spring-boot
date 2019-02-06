package me.snowdrop.vertx.http;

import java.nio.file.Path;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerResponse;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ZeroCopyHttpOutputMessage;
import org.springframework.http.server.reactive.AbstractServerHttpResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.util.function.Function.identity;

public class VertxServerHttpResponse extends AbstractServerHttpResponse implements ZeroCopyHttpOutputMessage {

    private final HttpServerResponse response;

    public VertxServerHttpResponse(HttpServerResponse response, NettyDataBufferFactory dataBufferFactory) {
        super(dataBufferFactory);
        this.response = response;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getNativeResponse() {
        return (T) response;
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
        // TODO
    }

    @Override
    public Mono<Void> writeWith(Path file, long position, long count) {
        return Mono.empty(); // TODO
    }

}
