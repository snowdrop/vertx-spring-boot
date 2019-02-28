package me.snowdrop.vertx.http;

import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.core.Ordered;

public class VertxWebServerFactoryCustomizer
    implements WebServerFactoryCustomizer<VertxReactiveWebServerFactory>, Ordered {

    @Override
    public void customize(VertxReactiveWebServerFactory factory) {
        factory.registerHttpServerOptionsCustomizer(portCustomizer(factory.getPort()));
    }

    @Override
    public int getOrder() {
        return 1; // Run after ReactiveWebServerFactoryCustomizer
    }

    private HttpServerOptionsCustomizer portCustomizer(int port) {
        return options -> port > 0 ? options.setPort(port) : options;
    }
}
