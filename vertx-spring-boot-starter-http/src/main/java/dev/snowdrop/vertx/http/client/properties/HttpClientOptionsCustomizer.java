package dev.snowdrop.vertx.http.client.properties;

import java.util.function.Function;

import io.vertx.core.http.HttpClientOptions;

@FunctionalInterface
public interface HttpClientOptionsCustomizer extends Function<HttpClientOptions, HttpClientOptions> {

}
