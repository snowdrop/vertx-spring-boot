package me.snowdrop.vertx.http.client;

import java.util.Collection;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClientResponse;
import me.snowdrop.vertx.http.common.ReadStreamFluxBuilder;
import me.snowdrop.vertx.http.utils.BufferConverter;
import me.snowdrop.vertx.http.utils.CookieConverter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.client.reactive.ClientHttpResponse;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;

public class VertxClientHttpResponse implements ClientHttpResponse {

    private final HttpClientResponse delegate;

    private final Flux<DataBuffer> bodyFlux;

    public VertxClientHttpResponse(HttpClientResponse delegate, BufferConverter bufferConverter) {
        this.delegate = delegate;
        this.bodyFlux = new ReadStreamFluxBuilder<Buffer, DataBuffer>()
            .readStream(delegate)
            .dataConverter(bufferConverter::toDataBuffer)
            .build();
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
        return bodyFlux;
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
