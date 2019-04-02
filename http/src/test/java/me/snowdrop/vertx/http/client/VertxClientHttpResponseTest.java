package me.snowdrop.vertx.http.client;

import java.util.Arrays;

import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.impl.headers.VertxHttpHeaders;
import me.snowdrop.vertx.http.utils.BufferConverter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class VertxClientHttpResponseTest {

    @Mock
    private HttpClientResponse mockHttpClientResponse;

    private BufferConverter bufferConverter;

    private VertxClientHttpResponse vertxClientHttpResponse;

    @Before
    public void setUp() {
        bufferConverter = new BufferConverter();
        vertxClientHttpResponse = new VertxClientHttpResponse(mockHttpClientResponse, bufferConverter);
    }

    @Test
    public void shouldGetRawStatus() {
        given(mockHttpClientResponse.statusCode()).willReturn(200);

        int code = vertxClientHttpResponse.getRawStatusCode();

        assertThat(code).isEqualTo(200);
    }

    @Test
    public void shouldGetStatusCode() {
        given(mockHttpClientResponse.statusCode()).willReturn(200);

        HttpStatus status = vertxClientHttpResponse.getStatusCode();

        assertThat(status).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void shouldGetBodyChunks() {
        Buffer firstBuffer = Buffer.buffer("chunk 1");
        Buffer secondBuffer = Buffer.buffer("chunk 2");
        given(mockHttpClientResponse.pause()).willReturn(mockHttpClientResponse);
        given(mockHttpClientResponse.exceptionHandler(any())).willReturn(mockHttpClientResponse);
        given(mockHttpClientResponse.handler(any())).will(invocation -> {
            Handler<Buffer> handler = invocation.getArgument(0);
            handler.handle(firstBuffer);
            handler.handle(secondBuffer);
            return mockHttpClientResponse;
        });
        given(mockHttpClientResponse.endHandler(any())).will(invocation -> {
            Handler<Void> handler = invocation.getArgument(0);
            handler.handle(null);
            return mockHttpClientResponse;
        });

        Flux<DataBuffer> bodyPublisher = vertxClientHttpResponse.getBody();

        StepVerifier.create(bodyPublisher)
            .expectNext(bufferConverter.toDataBuffer(firstBuffer))
            .expectNext(bufferConverter.toDataBuffer(secondBuffer))
            .verifyComplete();
    }

    @Test
    public void shouldGetHeaders() {
        MultiMap originalHeaders = new VertxHttpHeaders()
            .add("key1", "value1")
            .add("key1", "value2")
            .add("key2", "value3");
        given(mockHttpClientResponse.headers()).willReturn(originalHeaders);

        HttpHeaders expectedHeaders = new HttpHeaders();
        expectedHeaders.add("key1", "value1");
        expectedHeaders.add("key1", "value2");
        expectedHeaders.add("key2", "value3");

        HttpHeaders actualHeaders = vertxClientHttpResponse.getHeaders();

        assertThat(actualHeaders).isEqualTo(expectedHeaders);
    }

    @Test
    public void shouldGetCookies() {
        ResponseCookie simpleCookie = ResponseCookie.from("key2", "value2")
            .build();
        ResponseCookie complexCookie = ResponseCookie.from("key1", "value1")
            .domain("domain")
            .httpOnly(true)
            .maxAge(1)
            .path("path")
            .secure(true)
            .build();

        given(mockHttpClientResponse.cookies()).willReturn(
            Arrays.asList(simpleCookie.toString(), complexCookie.toString()));

        MultiValueMap<String, ResponseCookie> expectedCookies = new LinkedMultiValueMap<>();
        expectedCookies.add(simpleCookie.getName(), simpleCookie);
        expectedCookies.add(complexCookie.getName(), complexCookie);

        MultiValueMap<String, ResponseCookie> actualCookies = vertxClientHttpResponse.getCookies();

        assertThat(actualCookies).isEqualTo(expectedCookies);
    }
}
