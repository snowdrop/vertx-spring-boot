package me.snowdrop.vertx.http;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerRequest;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.server.WebServerException;

public class VertxWebServer implements WebServer {

    private final Vertx vertx;

    private final HttpServerOptions httpServerOptions;

    private final Handler<HttpServerRequest> requestHandler;

    private HttpServer server;

    public VertxWebServer(Vertx vertx, HttpServerOptions httpServerOptions, Handler<HttpServerRequest> requestHandler) {
        this.vertx = vertx;
        this.httpServerOptions = httpServerOptions;
        this.requestHandler = requestHandler;
    }

    @Override
    public void start() throws WebServerException {
        if (server != null) {
            return;
        }

        CompletableFuture<Void> future = new CompletableFuture<>();
        server = vertx.createHttpServer(httpServerOptions)
            .requestHandler(requestHandler)
            .listen(result -> {
                if (result.succeeded()) {
                    future.complete(null);
                } else {
                    future.completeExceptionally(result.cause());
                }
            });

        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new WebServerException(e.getMessage(), e);
        }
    }

    @Override
    public void stop() throws WebServerException {
        if (server == null) {
            return;
        }

        server.close(); // TODO make blocking
        server = null;
    }

    @Override
    public int getPort() {
        return httpServerOptions.getPort();
    }

}
