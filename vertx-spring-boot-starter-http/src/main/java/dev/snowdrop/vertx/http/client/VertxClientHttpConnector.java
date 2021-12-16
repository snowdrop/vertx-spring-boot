package dev.snowdrop.vertx.http.client;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.http.client.reactive.ClientHttpResponse;
import org.springframework.util.Assert;

import dev.snowdrop.vertx.http.common.ReadStreamFluxBuilder;
import dev.snowdrop.vertx.http.utils.BufferConverter;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.RequestOptions;
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

        // New way to create absolute requests is via RequestOptions.
        // More info in https://github.com/vert-x3/vertx-4-migration-guide/issues/61.
        RequestOptions requestOptions = new RequestOptions();
        try {
            requestOptions.setAbsoluteURI(uri.toURL());
            requestOptions.setMethod(HttpMethod.valueOf(method.name()));
        } catch (MalformedURLException e) {
            return Mono.error(new IllegalArgumentException("URI is malformed: " + uri));
        }

        // request handler
        CompletableFuture<HttpClientRequest> requestFuture = new CompletableFuture<>();
        client.request(requestOptions)
            .onFailure(requestFuture::completeExceptionally)
            .onSuccess(request -> {
                request.response()
                    .onSuccess(response -> {
                        Flux<DataBuffer> responseBody = responseToFlux(response).doFinally(ignore -> client.close());
                        responseFuture.complete(new VertxClientHttpResponse(response, responseBody));
                    })
                    .onFailure(responseFuture::completeExceptionally);

                requestFuture.complete(request);
            });

        return Mono.fromFuture(requestFuture)
            .flatMap(request -> requestCallback.apply(new VertxClientHttpRequest(request, bufferConverter)))
            .then(Mono.fromCompletionStage(responseFuture));
    }

    private Flux<DataBuffer> responseToFlux(HttpClientResponse response) {
        return new ReadStreamFluxBuilder<Buffer, DataBuffer>()
            .readStream(response)
            .dataConverter(bufferConverter::toDataBuffer)
            .build();
    }
}
