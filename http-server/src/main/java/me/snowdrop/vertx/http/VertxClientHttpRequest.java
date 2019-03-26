package me.snowdrop.vertx.http;

import java.net.URI;

import io.vertx.core.http.HttpClientRequest;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.reactive.AbstractClientHttpRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.util.function.Function.identity;

public class VertxClientHttpRequest extends AbstractClientHttpRequest {

    private final HttpClientRequest request;

    private final NettyDataBufferFactory dataBufferFactory;

    public VertxClientHttpRequest(HttpClientRequest request, NettyDataBufferFactory dataBufferFactory) {
        this.request = request;
        this.dataBufferFactory = dataBufferFactory;
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.resolve(request.method().name());
    }

    @Override
    public URI getURI() {
        return URI.create(request.absoluteURI());
    }

    @Override
    public DataBufferFactory bufferFactory() {
        return dataBufferFactory;
    }

    @Override
    public Mono<Void> writeWith(Publisher<? extends DataBuffer> chunks) {
        return doCommit(() -> Mono.create(sink -> chunks.subscribe(new HttpWriteStreamSubscriber(request, sink))));
    }

    @Override
    public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> chunks) {
        return writeWith(Flux.from(chunks).flatMap(identity()));
    }

    @Override
    public Mono<Void> setComplete() {
        return doCommit();
    }

    @Override
    protected void applyHeaders() {
        HttpHeaders headers = getHeaders();
        if (!headers.containsKey(HttpHeaders.CONTENT_LENGTH)) {
            request.setChunked(true);
        }
        headers.forEach(request::putHeader);
    }

    @Override
    protected void applyCookies() {
        // TODO implement once integration tests are available
    }
}
