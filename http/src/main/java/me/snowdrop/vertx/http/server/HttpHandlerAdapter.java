package me.snowdrop.vertx.http.server;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import me.snowdrop.vertx.http.utils.BufferConverter;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.HttpHandler;

public class HttpHandlerAdapter implements Handler<RoutingContext> {

    private final HttpHandler httpHandler;

    private final BufferConverter bufferConverter;

    public HttpHandlerAdapter(HttpHandler httpHandler, BufferConverter bufferConverter) {
        this.httpHandler = httpHandler;
        this.bufferConverter = bufferConverter;
    }

    @Override
    public void handle(RoutingContext context) {
        VertxServerHttpRequest webFluxRequest = new VertxServerHttpRequest(context, bufferConverter);
        VertxServerHttpResponse webFluxResponse = new VertxServerHttpResponse(context, bufferConverter);

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
