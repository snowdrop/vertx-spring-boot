package dev.snowdrop.vertx.http.client;

import java.net.URI;
import java.time.Duration;
import java.util.Arrays;

import dev.snowdrop.vertx.http.common.VertxWebSocketSession;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.WebSocket;
import io.vertx.core.http.impl.headers.VertxHttpHeaders;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class VertxWebSocketClientTest {

    private static final URI TEST_URI = URI.create("ws://example.com:8080/test");

    @Mock
    private Vertx mockVertx;

    @Mock
    private HttpClient mockHttpClient;

    @Mock
    private WebSocket mockWebSocket;

    private VertxWebSocketClient webSocketClient;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        // Configure mock http client to invoke passed web socket handler
        given(mockHttpClient.websocket(anyInt(), any(), anyString(), any(), any(Handler.class), any(Handler.class)))
            .will(answer -> {
                Handler<WebSocket> handler = answer.getArgument(4);
                handler.handle(mockWebSocket);
                return mockHttpClient;
            });
        given(mockVertx.createHttpClient(any(HttpClientOptions.class))).willReturn(mockHttpClient);

        webSocketClient = new VertxWebSocketClient(mockVertx);
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
        webSocketClient.execute(TEST_URI, session -> Mono.empty())
            .subscribe();

        verify(mockHttpClient).websocket(eq(TEST_URI.getPort()), eq(TEST_URI.getHost()), eq(TEST_URI.getPath()),
            any(MultiMap.class), any(Handler.class), any(Handler.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldInitializeEmptyHeaders() {
        webSocketClient.execute(TEST_URI, session -> Mono.empty())
            .subscribe();

        ArgumentCaptor<VertxHttpHeaders> headersCaptor = ArgumentCaptor.forClass(VertxHttpHeaders.class);
        verify(mockHttpClient).websocket(anyInt(), anyString(), anyString(), headersCaptor.capture(),
            any(Handler.class), any(Handler.class));

        assertThat(headersCaptor.getValue()).isEmpty();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldAdaptHeaders() {
        HttpHeaders originalHeaders = new HttpHeaders();
        originalHeaders.put("key1", Arrays.asList("value1", "value2"));
        originalHeaders.add("key2", "value3");

        webSocketClient.execute(TEST_URI, originalHeaders, session -> Mono.empty())
            .subscribe();

        ArgumentCaptor<VertxHttpHeaders> headersCaptor = ArgumentCaptor.forClass(VertxHttpHeaders.class);
        verify(mockHttpClient).websocket(anyInt(), anyString(), anyString(), headersCaptor.capture(),
            any(Handler.class), any(Handler.class));

        VertxHttpHeaders actualHeaders = headersCaptor.getValue();
        assertThat(actualHeaders.getAll("key1")).isEqualTo(originalHeaders.get("key1"));
        assertThat(actualHeaders.getAll("key2")).isEqualTo(originalHeaders.get("key2"));
    }

    @Test
    public void shouldInitSession() {
        webSocketClient.execute(TEST_URI, session -> session instanceof VertxWebSocketSession
            ? Mono.empty()
            : Mono.error(new AssertionError("Wrong session type: " + session.getClass()))
        ).block(Duration.ofSeconds(2));
    }

    @Test
    public void shouldCompleteSuccessfully() {
        webSocketClient.execute(TEST_URI, session -> Mono.empty())
            .block(Duration.ofSeconds(2));
    }

    @Test(expected = RuntimeException.class)
    public void shouldCompleteWithError() {
        webSocketClient.execute(TEST_URI, session -> Mono.error(RuntimeException::new))
            .block(Duration.ofSeconds(2));
    }
}
