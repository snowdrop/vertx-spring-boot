package me.snowdrop.vertx.http;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.HttpHandler;

public class VertxHttpHandlerAdapter implements Handler<HttpServerRequest> {

    private final HttpHandler httpHandler;

    private final NettyDataBufferFactory dataBufferFactory;

    public VertxHttpHandlerAdapter(HttpHandler httpHandler, NettyDataBufferFactory dataBufferFactory) {
        this.httpHandler = httpHandler;
        this.dataBufferFactory = dataBufferFactory;
    }

    @Override
    public void handle(HttpServerRequest request) {
        VertxServerHttpRequest webFluxRequest = new VertxServerHttpRequest(request, dataBufferFactory);
        VertxServerHttpResponse webFluxResponse = new VertxServerHttpResponse(request.response(), dataBufferFactory);

        httpHandler.handle(webFluxRequest, webFluxResponse)
            .doOnSuccess(v -> request
                .response()
                .end()
            )
            .doOnError(throwable -> request
                .response()
                .setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .end()
            )
            .subscribe();
    }

}
