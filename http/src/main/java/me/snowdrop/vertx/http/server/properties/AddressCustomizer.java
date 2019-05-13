package me.snowdrop.vertx.http.server.properties;

import java.net.InetAddress;

import io.vertx.core.http.HttpServerOptions;
import org.springframework.boot.web.server.AbstractConfigurableWebServerFactory;

public class AddressCustomizer implements HttpServerOptionsCustomizer {

    private final AbstractConfigurableWebServerFactory factory;

    public AddressCustomizer(AbstractConfigurableWebServerFactory factory) {
        this.factory = factory;
    }

    @Override
    public HttpServerOptions apply(HttpServerOptions options) {
        InetAddress address = factory.getAddress();

        if (address != null && address.getHostAddress() != null) {
            options.setHost(address.getHostAddress());
        }

        return options;
    }
}
