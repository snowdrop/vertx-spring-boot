package dev.snowdrop.vertx.http.client;

import java.net.URI;
import java.time.Duration;
import java.util.Arrays;

import dev.snowdrop.vertx.http.common.VertxWebSocketSession;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.WebSocket;
import io.vertx.core.http.WebSocketConnectOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.will;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class VertxWebSocketClientTest {

    private static final URI TEST_URI = URI.create("ws://example.com:8080/test");

    @Mock
    private Vertx mockVertx;

    @Mock
    private HttpClient mockHttpClient;

    @Mock
    private WebSocket mockWebSocket;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setUp() {
    }

    @Test
    public void shouldNotAcceptNullVertx() {
        try {
            new VertxWebSocketClient(null);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("Vertx is required");
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldUseCorrectUri() {
        getWebSocketClient().execute(TEST_URI, session -> Mono.empty())
            .subscribe();
        ArgumentCaptor<WebSocketConnectOptions> optionsCaptor = ArgumentCaptor.forClass(WebSocketConnectOptions.class);
        verify(mockHttpClient).webSocket(optionsCaptor.capture(), any(Handler.class));
        assertThat(optionsCaptor.getValue().getPort()).isEqualTo(TEST_URI.getPort());
        assertThat(optionsCaptor.getValue().getHost()).isEqualTo(TEST_URI.getHost());
        assertThat(optionsCaptor.getValue().getURI()).isEqualTo(TEST_URI.getPath());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldInitializeEmptyHeaders() {
        getWebSocketClient().execute(TEST_URI, session -> Mono.empty())
            .subscribe();
        ArgumentCaptor<WebSocketConnectOptions> optionsCaptor = ArgumentCaptor.forClass(WebSocketConnectOptions.class);
        verify(mockHttpClient).webSocket(optionsCaptor.capture(), any(Handler.class));
        assertThat(optionsCaptor.getValue().getPort()).isEqualTo(TEST_URI.getPort());
        assertThat(optionsCaptor.getValue().getHost()).isEqualTo(TEST_URI.getHost());
        assertThat(optionsCaptor.getValue().getURI()).isEqualTo(TEST_URI.getPath());
        assertThat(optionsCaptor.getValue().getHeaders()).isEmpty();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldAdaptHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.put("key1", Arrays.asList("value1", "value2"));
        headers.add("key2", "value3");

        getWebSocketClient().execute(TEST_URI, headers, session -> Mono.empty())
            .subscribe();

        ArgumentCaptor<WebSocketConnectOptions> optionsCaptor = ArgumentCaptor.forClass(WebSocketConnectOptions.class);
        verify(mockHttpClient).webSocket(optionsCaptor.capture(), any(Handler.class));
        assertThat(optionsCaptor.getValue().getPort()).isEqualTo(TEST_URI.getPort());
        assertThat(optionsCaptor.getValue().getHost()).isEqualTo(TEST_URI.getHost());
        assertThat(optionsCaptor.getValue().getURI()).isEqualTo(TEST_URI.getPath());
        assertThat(optionsCaptor.getValue().getHeaders().getAll("key1")).isEqualTo(headers.get("key1"));
        assertThat(optionsCaptor.getValue().getHeaders().getAll("key2")).isEqualTo(headers.get("key2"));
    }

    @Test
    public void shouldInitSession() {
        getWebSocketClient().execute(TEST_URI, session -> session instanceof VertxWebSocketSession
            ? Mono.empty()
            : Mono.error(new AssertionError("Wrong session type: " + session.getClass()))
        ).block(Duration.ofSeconds(2));
    }

    @Test
    public void shouldCompleteSuccessfully() {
        getWebSocketClient().execute(TEST_URI, session -> Mono.empty())
            .block(Duration.ofSeconds(2));
    }

    @Test
    public void shouldCompleteWithError() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(
            () -> getWebSocketClient().execute(TEST_URI, session -> Mono.error(RuntimeException::new))
                .block(Duration.ofSeconds(2)));
    }

    private VertxWebSocketClient getWebSocketClient() {
        // Configure mock http client to invoke passed web socket handler
        will(answer -> {
            Promise<WebSocket> socketPromise = Promise.promise();
            socketPromise.complete(mockWebSocket);

            Handler<AsyncResult<WebSocket>> handler = answer.getArgument(1);
            handler.handle(socketPromise.future());
            return mockHttpClient;
        }).given(mockHttpClient).webSocket(any(WebSocketConnectOptions.class), any(Handler.class));
        given(mockVertx.createHttpClient(any(HttpClientOptions.class))).willReturn(mockHttpClient);

        return new VertxWebSocketClient(mockVertx);
    }
}
