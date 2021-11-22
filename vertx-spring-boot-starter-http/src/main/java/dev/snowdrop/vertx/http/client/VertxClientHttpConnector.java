package dev.snowdrop.vertx.http.client;

import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import dev.snowdrop.vertx.http.common.ReadStreamFluxBuilder;
import dev.snowdrop.vertx.http.utils.BufferConverter;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.http.client.reactive.ClientHttpResponse;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class VertxClientHttpConnector implements ClientHttpConnector {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final BufferConverter bufferConverter;

    private final Vertx vertx;

    private final HttpClientOptions clientOptions;

    public VertxClientHttpConnector(Vertx vertx) {
        Assert.notNull(vertx, "Vertx is required");
        this.bufferConverter = new BufferConverter();
        this.vertx = vertx;
        this.clientOptions = new HttpClientOptions();
    }

    public VertxClientHttpConnector(Vertx vertx, HttpClientOptions options) {
        Assert.notNull(vertx, "Vertx is required");
        this.bufferConverter = new BufferConverter();
        this.vertx = vertx;
        this.clientOptions = options;
    }

    @Override
    public Mono<ClientHttpResponse> connect(org.springframework.http.HttpMethod method, URI uri,
        Function<? super ClientHttpRequest, Mono<Void>> requestCallback) {

        logger.debug("Connecting to '{}' with '{}", uri, method);

        if (!uri.isAbsolute()) {
            return Mono.error(new IllegalArgumentException("URI is not absolute: " + uri));
        }

        CompletableFuture<ClientHttpResponse> responseFuture = new CompletableFuture<>();
        HttpClient client = vertx.createHttpClient(clientOptions);
        Future<HttpClientRequest> request = client.request(HttpMethod.valueOf(method.name()), uri.toString());
        request.onComplete(response -> {
            if (response.succeeded()) {
                Flux<DataBuffer> responseBody = responseToFlux(response.result().response().result())
                    .doFinally(ignore -> client.close());

                responseFuture.complete(new VertxClientHttpResponse(response.result().response().result(), responseBody));
            } else {
                Throwable failure = response.cause();
            }
        });
//            .exceptionHandler(responseFuture::completeExceptionally)
//            .handler(response -> {
//                Flux<DataBuffer> responseBody = responseToFlux(response)
//                    .doFinally(ignore -> client.close());
//
//                responseFuture.complete(new VertxClientHttpResponse(response, responseBody));
//            });

        return requestCallback.apply(new VertxClientHttpRequest(request.result(), bufferConverter))
            .then(Mono.fromCompletionStage(responseFuture));
    }

    private Flux<DataBuffer> responseToFlux(HttpClientResponse response) {
        return new ReadStreamFluxBuilder<Buffer, DataBuffer>()
            .readStream(response)
            .dataConverter(bufferConverter::toDataBuffer)
            .build();
    }
}
