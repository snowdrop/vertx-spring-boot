package me.snowdrop.vertx.http;

import java.util.Set;

import me.snowdrop.vertx.http.properties.AddressCustomizer;
import me.snowdrop.vertx.http.properties.CompressionCustomizer;
import me.snowdrop.vertx.http.properties.HttpServerOptionsCustomizer;
import me.snowdrop.vertx.http.properties.PortCustomizer;
import me.snowdrop.vertx.http.properties.SslCustomizer;
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
        factory.registerHttpServerOptionsCustomizer(new PortCustomizer(factory.getPort()));
        factory.registerHttpServerOptionsCustomizer(new AddressCustomizer(factory.getAddress()));
        factory.registerHttpServerOptionsCustomizer(new SslCustomizer(factory.getSsl()));
        factory.registerHttpServerOptionsCustomizer(new CompressionCustomizer(factory.getCompression()));

        if (userDefinedCustomizers != null) {
            userDefinedCustomizers.forEach(factory::registerHttpServerOptionsCustomizer);
        }
    }

    @Override
    public int getOrder() {
        return 1; // Run after ReactiveWebServerFactoryCustomizer
    }
}
