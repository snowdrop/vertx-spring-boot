package dev.snowdrop.vertx.http.client;

import java.net.URI;
import java.util.Collection;

import dev.snowdrop.vertx.http.common.WriteStreamSubscriber;
import dev.snowdrop.vertx.http.utils.BufferConverter;
import io.vertx.core.http.HttpClientRequest;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
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

    private final HttpClientRequest delegate;

    private final BufferConverter bufferConverter;

    public VertxClientHttpRequest(HttpClientRequest delegate, BufferConverter bufferConverter) {
        this.delegate = delegate;
        this.bufferConverter = bufferConverter;
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.resolve(delegate.getMethod().name());
    }

    @Override
    public URI getURI() {
        return URI.create(delegate.absoluteURI());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getNativeRequest() {
        return (T) delegate;
    }

    @Override
    public DataBufferFactory bufferFactory() {
        return bufferConverter.getDataBufferFactory();
    }

    @Override
    public Mono<Void> writeWith(Publisher<? extends DataBuffer> chunks) {
        Mono<Void> writeCompletion = Mono.create(sink -> {
            logger.debug("Subscribing to body publisher");
            Subscriber<DataBuffer> subscriber = new WriteStreamSubscriber.Builder<HttpClientRequest, DataBuffer>()
                .writeStream(delegate)
                .endHook(sink)
                .nextHandler((stream, value) -> stream.write(bufferConverter.toBuffer(value)))
                .build();
            chunks.subscribe(subscriber);
        });

        Mono<Void> endCompletion = Mono.create(sink -> {
            logger.debug("Completing request after writing");
            delegate.end();
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
            delegate.end();
            sink.success();
        }));
    }

    @Override
    protected void applyHeaders() {
        HttpHeaders headers = getHeaders();
        if (!headers.containsKey(HttpHeaders.CONTENT_LENGTH)) {
            logger.debug("Setting chunked request");
            delegate.setChunked(true);
        }
        headers.forEach(delegate::putHeader);
    }

    @Override
    protected void applyCookies() {
        getCookies()
            .values()
            .stream()
            .flatMap(Collection::stream)
            .map(HttpCookie::toString)
            .forEach(cookie -> delegate.putHeader("Cookie", cookie));
    }
}
