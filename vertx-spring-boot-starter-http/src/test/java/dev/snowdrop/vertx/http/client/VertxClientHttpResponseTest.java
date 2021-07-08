package dev.snowdrop.vertx.http.client;

import java.util.Arrays;

import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpClientResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class VertxClientHttpResponseTest {

    @Mock
    private HttpClientResponse mockDelegate;

    @Test
    public void shouldGetRawStatus() {
        given(mockDelegate.statusCode()).willReturn(200);

        VertxClientHttpResponse response = new VertxClientHttpResponse(mockDelegate, Flux.empty());
        int code = response.getRawStatusCode();

        assertThat(code).isEqualTo(200);
    }

    @Test
    public void shouldGetStatusCode() {
        given(mockDelegate.statusCode()).willReturn(200);

        VertxClientHttpResponse response = new VertxClientHttpResponse(mockDelegate, Flux.empty());
        HttpStatus status = response.getStatusCode();

        assertThat(status).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void shouldGetBody() {
        DataBufferFactory factory = new DefaultDataBufferFactory();
        Flux<DataBuffer> body = Flux.just(factory.wrap("first".getBytes()), factory.wrap("second".getBytes()));

        VertxClientHttpResponse response = new VertxClientHttpResponse(mockDelegate, body);
        assertThat(response.getBody()).isEqualTo(body);
    }

    @Test
    public void shouldGetHeaders() {
        MultiMap originalHeaders = MultiMap.caseInsensitiveMultiMap()
            .add("key1", "value1")
            .add("key1", "value2")
            .add("key2", "value3");
        given(mockDelegate.headers()).willReturn(originalHeaders);

        HttpHeaders expectedHeaders = new HttpHeaders();
        expectedHeaders.add("key1", "value1");
        expectedHeaders.add("key1", "value2");
        expectedHeaders.add("key2", "value3");

        VertxClientHttpResponse response = new VertxClientHttpResponse(mockDelegate, Flux.empty());
        HttpHeaders actualHeaders = response.getHeaders();

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

        given(mockDelegate.cookies()).willReturn(
            Arrays.asList(simpleCookie.toString(), complexCookie.toString()));

        MultiValueMap<String, ResponseCookie> expectedCookies = new LinkedMultiValueMap<>();
        expectedCookies.add(simpleCookie.getName(), simpleCookie);
        expectedCookies.add(complexCookie.getName(), complexCookie);

        VertxClientHttpResponse response = new VertxClientHttpResponse(mockDelegate, Flux.empty());
        MultiValueMap<String, ResponseCookie> actualCookies = response.getCookies();

        assertThat(actualCookies).isEqualTo(expectedCookies);
    }
}
