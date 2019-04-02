package me.snowdrop.vertx.http.client;

import java.util.Collection;

import io.vertx.core.http.HttpClientResponse;
import me.snowdrop.vertx.http.utils.BufferConverter;
import me.snowdrop.vertx.http.utils.CookieConverter;
import me.snowdrop.vertx.http.common.HttpBodyToFluxConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.client.reactive.ClientHttpResponse;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;

public class VertxClientHttpResponse implements ClientHttpResponse {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final HttpClientResponse delegate;

    private final HttpBodyToFluxConnector bodyToFluxConnector;

    public VertxClientHttpResponse(HttpClientResponse delegate, BufferConverter bufferConverter) {
        this.delegate = delegate;
        this.bodyToFluxConnector = new HttpBodyToFluxConnector(bufferConverter);
    }

    @Override
    public int getRawStatusCode() {
        return delegate.statusCode();
    }

    @Override
    public HttpStatus getStatusCode() {
        return HttpStatus.valueOf(delegate.statusCode());
    }

    @Override
    public Flux<DataBuffer> getBody() {
        logger.debug("Creating a response body read stream connector");
        return bodyToFluxConnector.connect(delegate);
    }

    @Override
    public HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        delegate.headers()
            .forEach(e -> headers.add(e.getKey(), e.getValue()));

        return headers;
    }

    @Override
    public MultiValueMap<String, ResponseCookie> getCookies() {
        MultiValueMap<String, ResponseCookie> cookies = new LinkedMultiValueMap<>();

        delegate.cookies()
            .stream()
            .map(CookieConverter::toResponseCookies)
            .flatMap(Collection::stream)
            .forEach(cookie -> cookies.add(cookie.getName(), cookie));

        return cookies;
    }
}
