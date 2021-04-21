package dev.snowdrop.vertx.sample.websocket;

import java.net.URI;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.contains;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebSocketSampleApplicationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private WebSocketClient client;

    private Disposable disposable;

    private URI serviceUri;

    @BeforeEach
    public void setUp() {
        serviceUri = URI.create("ws://localhost:" + port + "/echo-upper");
    }

    @AfterEach
    public void tearDown() {
        if (disposable != null) {
            disposable.dispose(); // Terminate the socket subscription
        }
    }

    @Test
    public void testWebSocket() {
        Flux<String> originalMessages = Flux.just("first", "second");
        List<String> responseMessages = new CopyOnWriteArrayList<>();

        disposable = client.execute(serviceUri, session -> {
            // Convert strings to WebSocket messages and send them
            Mono<Void> outputMono = session.send(originalMessages.map(session::textMessage));

            Mono<Void> inputMono = session.receive() // Receive a messages stream
                .map(WebSocketMessage::getPayloadAsText) // Extract a payload from each message
                .doOnNext(responseMessages::add) // Store the payload to a collection
                .then();

            return outputMono.then(inputMono); // Start receiving messages after sending.
        }).subscribe(); // Subscribe to the socket. Original messages will be sent and then we'll start receiving responses.

        await()
            .atMost(2, SECONDS)
            .until(() -> responseMessages, contains("FIRST", "SECOND"));
    }
}
