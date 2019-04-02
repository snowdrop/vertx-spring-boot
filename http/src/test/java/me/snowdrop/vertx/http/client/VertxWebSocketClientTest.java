package me.snowdrop.vertx.http.client;

import java.net.URI;
import java.time.Duration;
import java.util.Arrays;

import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.WebSocket;
import io.vertx.core.http.impl.headers.VertxHttpHeaders;
import me.snowdrop.vertx.http.common.VertxWebSocketSession;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

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
    private HttpClient mockHttpClient;

    @Mock
    private WebSocket mockWebSocket;

    private VertxWebSocketClient webSocketClient;

    @Before
    public void setUp() {
        // Configure mock http client to invoke passed web socket handler
        given(mockHttpClient.websocket(anyInt(), anyString(), anyString(), any(MultiMap.class), any())).will(answer -> {
            Handler<WebSocket> handler = answer.getArgument(4);
            handler.handle(mockWebSocket);
            return mockHttpClient;
        });

        webSocketClient = new VertxWebSocketClient(mockHttpClient);
    }

    @Test
    public void shouldNotAcceptNullHttpClient() {
        try {
            new VertxWebSocketClient((HttpClient) null);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("HttpClient is required");
        }
    }

    @Test
    public void shouldUseCorrectUri() {
        webSocketClient.execute(TEST_URI, session -> Mono.empty())
            .subscribe();

        verify(mockHttpClient).websocket(
            eq(TEST_URI.getPort()), eq(TEST_URI.getHost()), eq(TEST_URI.getPath()), any(MultiMap.class), any());
    }

    @Test
    public void shouldInitializeEmptyHeaders() {
        webSocketClient.execute(TEST_URI, session -> Mono.empty())
            .subscribe();

        ArgumentCaptor<VertxHttpHeaders> headersCaptor = ArgumentCaptor.forClass(VertxHttpHeaders.class);
        verify(mockHttpClient).websocket(anyInt(), anyString(), anyString(), headersCaptor.capture(), any());

        assertThat(headersCaptor.getValue()).isEmpty();
    }

    @Test
    public void shouldAdaptHeaders() {
        HttpHeaders originalHeaders = new HttpHeaders();
        originalHeaders.put("key1", Arrays.asList("value1", "value2"));
        originalHeaders.add("key2", "value3");

        webSocketClient.execute(TEST_URI, originalHeaders, session -> Mono.empty())
            .subscribe();

        ArgumentCaptor<VertxHttpHeaders> headersCaptor = ArgumentCaptor.forClass(VertxHttpHeaders.class);
        verify(mockHttpClient).websocket(anyInt(), anyString(), anyString(), headersCaptor.capture(), any());

        VertxHttpHeaders actualHeaders = headersCaptor.getValue();
        assertThat(actualHeaders.getAll("key1")).isEqualTo(originalHeaders.get("key1"));
        assertThat(actualHeaders.getAll("key2")).isEqualTo(originalHeaders.get("key2"));
    }

    @Test
    public void shouldInitSession() {
        Mono<Void> completionMono = webSocketClient.execute(TEST_URI,
            session -> session instanceof VertxWebSocketSession ? Mono.empty()
                : Mono.error(new AssertionError("Wrong session type: " + session.getClass())));

        StepVerifier.create(completionMono)
            .expectComplete()
            .verify(Duration.ofSeconds(2));
    }

    @Test
    public void shouldCompleteSuccessfully() {
        Mono<Void> completionMono = webSocketClient.execute(TEST_URI, session -> Mono.empty());

        StepVerifier.create(completionMono)
            .expectComplete()
            .verify(Duration.ofSeconds(2));
    }

    @Test
    public void shouldCompleteWithError() {
        Mono<Void> completionMono = webSocketClient.execute(TEST_URI, session -> Mono.error(RuntimeException::new));

        StepVerifier.create(completionMono)
            .expectError(RuntimeException.class)
            .verify(Duration.ofSeconds(2));
    }
}
