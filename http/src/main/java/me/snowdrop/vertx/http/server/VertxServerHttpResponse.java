package me.snowdrop.vertx.http.server;

import java.nio.file.Path;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import me.snowdrop.vertx.http.common.WriteStreamSubscriber;
import me.snowdrop.vertx.http.utils.BufferConverter;
import me.snowdrop.vertx.http.utils.CookieConverter;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ZeroCopyHttpOutputMessage;
import org.springframework.http.server.reactive.AbstractServerHttpResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.util.function.Function.identity;

public class VertxServerHttpResponse extends AbstractServerHttpResponse implements ZeroCopyHttpOutputMessage {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final RoutingContext context;

    private final HttpServerResponse delegate;

    private final BufferConverter bufferConverter;

    public VertxServerHttpResponse(RoutingContext context, BufferConverter bufferConverter) {
        super(bufferConverter.getDataBufferFactory(), initHeaders(context.response()));
        this.context = context;
        this.delegate = context.response();
        this.bufferConverter = bufferConverter;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getNativeResponse() {
        return (T) delegate;
    }

    @Override
    public Mono<Void> writeWith(Path file, long position, long count) {
        Mono<Void> writeCompletion = Mono.create(sink -> {
            logger.debug("Sending file '{}' pos='{}' count='{}'", file, position, count);
            delegate.sendFile(file.toString(), position, count, result -> {
                if (result.succeeded()) {
                    sink.success();
                } else {
                    sink.error(result.cause());
                }
            });
        });

        return doCommit(() -> writeCompletion);
    }

    @Override
    protected Mono<Void> writeWithInternal(Publisher<? extends DataBuffer> chunks) {
        return Mono.create(sink -> {
            logger.debug("Subscribing to body publisher");
            Subscriber<DataBuffer> subscriber = new WriteStreamSubscriber.Builder<HttpServerResponse, DataBuffer>()
                .writeStream(delegate)
                .endHook(sink)
                .nextHandler((stream, value) -> stream.write(bufferConverter.toBuffer(value)))
                .build();
            chunks.subscribe(subscriber);
        });
    }

    @Override
    protected Mono<Void> writeAndFlushWithInternal(Publisher<? extends Publisher<? extends DataBuffer>> chunks) {
        return writeWithInternal(Flux.from(chunks).flatMap(identity()));
    }

    @Override
    protected void applyStatusCode() {
        HttpStatus statusCode = getStatusCode();
        if (statusCode != null) {
            delegate.setStatusCode(statusCode.value());
        }
    }

    @Override
    protected void applyHeaders() {
        HttpHeaders headers = getHeaders();
        if (!headers.containsKey(HttpHeaders.CONTENT_LENGTH)) {
            logger.debug("Setting chunked response");
            delegate.setChunked(true);
        }
        headers.forEach(delegate::putHeader);
    }

    @Override
    protected void applyCookies() {
        getCookies()
            .toSingleValueMap() // Vert.x doesn't support multi-value cookies
            .values()
            .stream()
            .map(CookieConverter::toCookie)
            .forEach(context::addCookie);
    }

    private static HttpHeaders initHeaders(HttpServerResponse response) {
        HttpHeaders headers = new HttpHeaders();
        response.headers()
            .forEach(e -> headers.add(e.getKey(), e.getValue()));

        return headers;
    }
}
