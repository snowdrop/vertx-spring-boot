package me.snowdrop.vertx.http;

import java.net.URI;
import java.util.Collection;

import io.vertx.core.http.HttpClientRequest;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.reactive.AbstractClientHttpRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.util.function.Function.identity;

public class VertxClientHttpRequest extends AbstractClientHttpRequest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final HttpClientRequest request;

    private final BufferConverter bufferConverter;

    public VertxClientHttpRequest(HttpClientRequest request, BufferConverter bufferConverter) {
        this.request = request;
        this.bufferConverter = bufferConverter;
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.resolve(request.method().name()); // TODO refactor
    }

    @Override
    public URI getURI() {
        return URI.create(request.absoluteURI());
    }

    @Override
    public DataBufferFactory bufferFactory() {
        return bufferConverter.getDataBufferFactory();
    }

    @Override
    public Mono<Void> writeWith(Publisher<? extends DataBuffer> chunks) {
        Mono<Void> writeCompletion = Mono.create(sink -> {
            logger.debug("Subscribing to body publisher");
            chunks.subscribe(new PublisherToHttpBodyConnector(request, sink, bufferConverter));
        });

        Mono<Void> endCompletion = Mono.create(sink -> {
            logger.debug("Completing request after writing");
            request.end();
            sink.success();
        });

        return doCommit(() -> writeCompletion.then(endCompletion));
    }

    @Override
    public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> chunks) {
        return writeWith(Flux.from(chunks).flatMap(identity()));
    }

    @Override
    public Mono<Void> setComplete() {
        return doCommit(() -> Mono.create(sink -> {
            logger.debug("Completing empty request");
            request.end();
            sink.success();
        }));
    }

    @Override
    protected void applyHeaders() {
        HttpHeaders headers = getHeaders();
        if (!headers.containsKey(HttpHeaders.CONTENT_LENGTH)) {
            logger.debug("Setting chunked request");
            request.setChunked(true);
        }
        headers.forEach(request::putHeader);
    }

    @Override
    protected void applyCookies() {
        getCookies()
            .values()
            .stream()
            .flatMap(Collection::stream)
            .map(HttpCookie::toString)
            .forEach(cookie -> request.putHeader("Cookie", cookie));
    }
}
