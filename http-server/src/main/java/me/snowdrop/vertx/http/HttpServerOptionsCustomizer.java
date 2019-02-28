package me.snowdrop.vertx.http;

import java.util.function.Function;

import io.vertx.core.http.HttpServerOptions;

@FunctionalInterface
public interface HttpServerOptionsCustomizer extends Function<HttpServerOptions, HttpServerOptions> {

}
