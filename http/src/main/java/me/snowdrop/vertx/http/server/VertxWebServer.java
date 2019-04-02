package me.snowdrop.vertx.http.server;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.CookieHandler;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.server.WebServerException;

public class VertxWebServer implements WebServer {

    private final Vertx vertx;

    private final HttpServerOptions httpServerOptions;

    private final Handler<RoutingContext> requestHandler;

    private HttpServer server;

    public VertxWebServer(Vertx vertx, HttpServerOptions httpServerOptions, Handler<RoutingContext> requestHandler) {
        this.vertx = vertx;
        this.httpServerOptions = httpServerOptions;
        this.requestHandler = requestHandler;
    }

    @Override
    public void start() throws WebServerException {
        if (server != null) {
            return;
        }

        Router router = Router.router(vertx);
        router.route()
            .handler(CookieHandler.create())
            .handler(requestHandler);

        CompletableFuture<Void> future = new CompletableFuture<>();
        server = vertx.createHttpServer(httpServerOptions)
            .requestHandler(router)
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
