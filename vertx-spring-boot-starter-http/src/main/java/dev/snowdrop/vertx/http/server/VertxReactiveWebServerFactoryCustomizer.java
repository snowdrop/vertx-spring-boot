package dev.snowdrop.vertx.http.server;

import java.util.Set;

import dev.snowdrop.vertx.http.server.properties.AddressCustomizer;
import dev.snowdrop.vertx.http.server.properties.HttpServerOptionsCustomizer;
import dev.snowdrop.vertx.http.server.properties.PortCustomizer;
import dev.snowdrop.vertx.http.server.properties.SslCustomizer;
import dev.snowdrop.vertx.http.server.properties.CompressionCustomizer;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.core.Ordered;

public class VertxReactiveWebServerFactoryCustomizer
    implements WebServerFactoryCustomizer<VertxReactiveWebServerFactory>, Ordered {

    private final Set<HttpServerOptionsCustomizer> userDefinedCustomizers;

    public VertxReactiveWebServerFactoryCustomizer(Set<HttpServerOptionsCustomizer> userDefinedCustomizers) {
        this.userDefinedCustomizers = userDefinedCustomizers;
    }

    @Override
    public void customize(VertxReactiveWebServerFactory factory) {
        factory.registerHttpServerOptionsCustomizer(new PortCustomizer(factory));
        factory.registerHttpServerOptionsCustomizer(new AddressCustomizer(factory));
        factory.registerHttpServerOptionsCustomizer(new SslCustomizer(factory));
        factory.registerHttpServerOptionsCustomizer(new CompressionCustomizer(factory));

        if (userDefinedCustomizers != null) {
            userDefinedCustomizers.forEach(factory::registerHttpServerOptionsCustomizer);
        }
    }

    @Override
    public int getOrder() {
        return 1; // Run after ReactiveWebServerFactoryCustomizer
    }
}
