package me.snowdrop.vertx.http;

import me.snowdrop.vertx.http.properties.AddressCustomizer;
import me.snowdrop.vertx.http.properties.CompressionCustomizer;
import me.snowdrop.vertx.http.properties.Http2Customizer;
import me.snowdrop.vertx.http.properties.PortCustomizer;
import me.snowdrop.vertx.http.properties.SslCustomizer;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.core.Ordered;

public class VertxReactiveWebServerFactoryCustomizer
    implements WebServerFactoryCustomizer<VertxReactiveWebServerFactory>, Ordered {

    @Override
    public void customize(VertxReactiveWebServerFactory factory) {
        factory.registerHttpServerOptionsCustomizer(new PortCustomizer(factory.getPort()));
        factory.registerHttpServerOptionsCustomizer(new AddressCustomizer(factory.getAddress()));
        factory.registerHttpServerOptionsCustomizer(new SslCustomizer(factory.getSsl()));
        factory.registerHttpServerOptionsCustomizer(new Http2Customizer(factory.getHttp2()));
        factory.registerHttpServerOptionsCustomizer(new CompressionCustomizer(factory.getCompression()));
    }

    @Override
    public int getOrder() {
        return 1; // Run after ReactiveWebServerFactoryCustomizer
    }
}
