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

    private final Handler<HttpServerRequest> requestHandler;

    private HttpServer server;

    public VertxWebServer(Vertx vertx, Handler<HttpServerRequest> requestHandler) {
        this.vertx = vertx;
        this.requestHandler = requestHandler;
    }

    @Override
    public void start() throws WebServerException {
        if (server != null) {
            return;
        }

        HttpServerOptions options = new HttpServerOptions()
            .setPort(8080)
            .setLogActivity(true);

        CompletableFuture<Void> future = new CompletableFuture<>();
        server = vertx.createHttpServer(options)
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
        return 8080;
    }

}
