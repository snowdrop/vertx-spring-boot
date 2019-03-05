package me.snowdrop.vertx.http.properties;

import io.vertx.core.http.HttpServerOptions;
import org.springframework.boot.web.server.Http2;

public class Http2Customizer implements HttpServerOptionsCustomizer {

    private final Http2 http2;

    public Http2Customizer(Http2 http2) {
        this.http2 = http2;
    }

    @Override
    public HttpServerOptions apply(HttpServerOptions options) {
        return options; // TODO
    }
}
