package me.snowdrop.vertx.http;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.HttpHandler;

public class VertxHttpHandlerAdapter implements Handler<RoutingContext> {

    private final HttpHandler httpHandler;

    private final NettyDataBufferFactory dataBufferFactory;

    public VertxHttpHandlerAdapter(HttpHandler httpHandler, NettyDataBufferFactory dataBufferFactory) {
        this.httpHandler = httpHandler;
        this.dataBufferFactory = dataBufferFactory;
    }

    @Override
    public void handle(RoutingContext context) {
        VertxServerHttpRequest webFluxRequest = new VertxServerHttpRequest(context, dataBufferFactory);
        VertxServerHttpResponse webFluxResponse = new VertxServerHttpResponse(context, dataBufferFactory);

        httpHandler.handle(webFluxRequest, webFluxResponse)
            .doOnSuccess(v -> context
                .response()
                .end()
            )
            .doOnError(throwable -> context
                .response()
                .setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .end()
            )
            .subscribe();
    }

}
