package me.snowdrop.vertx.http.server.properties;

import java.net.InetAddress;

import io.vertx.core.http.HttpServerOptions;

public class AddressCustomizer implements HttpServerOptionsCustomizer {

    private final InetAddress address;

    public AddressCustomizer(InetAddress address) {
        this.address = address;
    }

    @Override
    public HttpServerOptions apply(HttpServerOptions options) {
        if (address != null && address.getHostAddress() != null) {
            options.setHost(address.getHostAddress());
        }
        return options;
    }
}
