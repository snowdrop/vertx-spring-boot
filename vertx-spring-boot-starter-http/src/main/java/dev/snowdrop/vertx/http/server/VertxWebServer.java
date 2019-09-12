package dev.snowdrop.vertx.http.server;

import java.time.Duration;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.server.WebServerException;
import reactor.core.publisher.Mono;

public class VertxWebServer implements WebServer {

    private final Logger logger = LoggerFactory.getLogger(getClass());

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
            .handler(requestHandler);

        server = vertx
            .createHttpServer(httpServerOptions)
            .requestHandler(router);

        Mono<Void> future = Mono.create(sink -> server.listen(result -> {
            if (result.succeeded()) {
                logger.info("Vert.x HTTP server started on port {}", getPort());
                sink.success();
            } else {
                sink.error(result.cause());
            }
        }));

        future.block(Duration.ofSeconds(5));
    }

    @Override
    public void stop() throws WebServerException {
        if (server == null) {
            return;
        }

        Mono<Void> future = Mono.create(sink -> server.close(result -> {
            if (result.succeeded()) {
                sink.success();
            } else {
                sink.error(result.cause());
            }
        }));

        future
            .doOnTerminate(() -> server = null)
            .block(Duration.ofSeconds(5));
    }

    @Override
    public int getPort() {
        if (server != null) {
            return server.actualPort();
        }
        return 0;
    }

}
