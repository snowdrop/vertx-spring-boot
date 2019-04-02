package me.snowdrop.vertx.http;

import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.http.client.reactive.ClientHttpResponse;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

public class VertxClientHttpConnector implements ClientHttpConnector {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final HttpClient httpClient;

    private final BufferConverter bufferConverter;

    public VertxClientHttpConnector(Vertx vertx) {
        this(vertx.createHttpClient());
    }

    public VertxClientHttpConnector(Vertx vertx, HttpClientOptions options) {
        this(vertx.createHttpClient(options));
    }

    public VertxClientHttpConnector(HttpClient httpClient) {
        Assert.notNull(httpClient, "HttpClient is required");
        this.httpClient = httpClient;
        this.bufferConverter = new BufferConverter();
    }

    @Override
    public Mono<ClientHttpResponse> connect(org.springframework.http.HttpMethod method, URI uri,
        Function<? super ClientHttpRequest, Mono<Void>> requestCallback) {

        logger.debug("Connecting to '{}' with '{}", uri, method);

        if (!uri.isAbsolute()) {
            return Mono.error(new IllegalArgumentException("URI is not absolute: " + uri));
        }

        CompletableFuture<ClientHttpResponse> futureResponse = new CompletableFuture<>();

        HttpClientRequest request = httpClient.requestAbs(httpMethodAdapter(method), uri.toString())
            .handler(response -> futureResponse.complete(responseAdapter(response)))
            .exceptionHandler(futureResponse::completeExceptionally);

        return requestCallback.apply(requestAdapter(request))
            .then(Mono.fromCompletionStage(futureResponse));
    }

    private ClientHttpRequest requestAdapter(HttpClientRequest request) {
        return new VertxClientHttpRequest(request, bufferConverter);
    }

    private ClientHttpResponse responseAdapter(HttpClientResponse response) {
        return new VertxClientHttpResponse(response, bufferConverter);
    }

    private HttpMethod httpMethodAdapter(org.springframework.http.HttpMethod method) {
        return HttpMethod.valueOf(method.name());  // TODO refactor
    }
}
