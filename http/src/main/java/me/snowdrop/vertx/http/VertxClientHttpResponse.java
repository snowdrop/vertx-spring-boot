package me.snowdrop.vertx.http;

import java.util.Collection;

import io.vertx.core.http.HttpClientResponse;
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

    private final HttpClientResponse response;

    private final BufferConverter bufferConverter;

    public VertxClientHttpResponse(HttpClientResponse response, BufferConverter bufferConverter) {
        this.response = response;
        this.bufferConverter = bufferConverter;
    }

    @Override
    public int getRawStatusCode() {
        return response.statusCode();
    }

    @Override
    public HttpStatus getStatusCode() {
        return HttpStatus.valueOf(response.statusCode());
    }

    @Override
    public Flux<DataBuffer> getBody() {
        // TODO look into refactoring with VertxServerHttpRequest.getBody
        return Flux.create(sink -> {
            logger.debug("Connecting to a response body read stream");
            response
                .pause()
                .handler(chunk -> {
                    logger.debug("Received '{}' from a response body read stream", chunk);
                    DataBuffer dataBuffer = bufferConverter.toDataBuffer(chunk);
                    sink.next(dataBuffer);
                })
                .exceptionHandler(sink::error)
                .endHandler(v -> {
                    logger.debug("Response body read stream ended");
                    sink.complete();
                });
            sink.onRequest(i -> {
                logger.debug("Fetching '{}' entries from a response body read stream", i);
                response.fetch(i);
            });
        });
    }

    @Override
    public HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        response.headers()
            .forEach(e -> headers.add(e.getKey(), e.getValue()));

        return headers;
    }

    @Override
    public MultiValueMap<String, ResponseCookie> getCookies() {
        MultiValueMap<String, ResponseCookie> cookies = new LinkedMultiValueMap<>();

        response.cookies()
            .stream()
            .map(CookieConverter::toResponseCookies)
            .flatMap(Collection::stream)
            .forEach(cookie -> cookies.add(cookie.getName(), cookie));

        return cookies;
    }
}
