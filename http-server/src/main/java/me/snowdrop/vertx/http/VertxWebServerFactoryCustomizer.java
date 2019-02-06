package me.snowdrop.vertx.http;

import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.core.Ordered;

public class VertxWebServerFactoryCustomizer
    implements WebServerFactoryCustomizer<VertxReactiveWebServerFactory>, Ordered {

    @Override
    public void customize(VertxReactiveWebServerFactory factory) {
        // TODO
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
