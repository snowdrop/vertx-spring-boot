package me.snowdrop.vertx.http.it;

import java.net.URI;
import java.time.Duration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClientOptions;
import me.snowdrop.vertx.http.client.VertxWebSocketClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;

@Category(FastTests.class)
@RunWith(SpringRunner.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
    properties = {
        "server.port=" + Ports.WEB_SOCKET_IT,
        "vertx.http.server.maxWebsocketFrameSize=5"
    }
)
public class WebSocketIT {

    private static final String BASE_URL = String.format("ws://localhost:%d", Ports.WEB_SOCKET_IT);

    @Autowired
    private Vertx vertx;

    private WebSocketClient client;

    @Before
    public void setUp() {
        client = new VertxWebSocketClient(vertx);
    }

    @Test
    public void shouldSendAndReceiveTextMessage() {
        AtomicReference<String> expectedMessage = new AtomicReference<>();

        client.execute(URI.create(BASE_URL + "/echo"), session -> {
            WebSocketMessage originalMessage = session.textMessage("ping");

            Mono<Void> outputMono = session.send(Mono.just(originalMessage));
            Mono<Void> inputMono = session.receive()
                .filter(message -> message.getType().equals(WebSocketMessage.Type.TEXT))
                .map(WebSocketMessage::getPayloadAsText)
                .doOnNext(expectedMessage::set)
                .then();

            return outputMono.then(inputMono);
        }).subscribe();

        await()
            .atMost(2, SECONDS)
            .untilAtomic(expectedMessage, equalTo("ping"));
    }

    @Test
    public void shouldSendAndReceiveMultiFrameTextMessage() {
        HttpClientOptions options = new HttpClientOptions()
            .setMaxWebsocketFrameSize(5);
        client = new VertxWebSocketClient(vertx, options);

        AtomicReference<String> expectedMessage = new AtomicReference<>();

        client.execute(URI.create(BASE_URL + "/echo"), session -> {
            WebSocketMessage originalMessage = session.textMessage("ping pong");

            Mono<Void> outputMono = session.send(Mono.just(originalMessage));
            Mono<Void> inputMono = session.receive()
                .filter(message -> message.getType().equals(WebSocketMessage.Type.TEXT))
                .map(WebSocketMessage::getPayloadAsText)
                .doOnNext(expectedMessage::set)
                .then();

            return outputMono.then(inputMono);
        }).subscribe();

        await()
            .atMost(2, SECONDS)
            .untilAtomic(expectedMessage, equalTo("ping pong"));
    }

    @Test
    public void shouldSendAndReceiveBinaryMessage() {
        AtomicReference<String> expectedMessage = new AtomicReference<>();

        client.execute(URI.create(BASE_URL + "/echo"), session -> {
            WebSocketMessage originalMessage = session.binaryMessage(factory -> factory.wrap("ping".getBytes()));

            Mono<Void> outputMono = session.send(Mono.just(originalMessage));
            Mono<Void> inputMono = session.receive()
                .filter(message -> message.getType().equals(WebSocketMessage.Type.BINARY))
                .map(WebSocketMessage::getPayloadAsText)
                .doOnNext(expectedMessage::set)
                .then();

            return outputMono.then(inputMono);
        }).subscribe();

        await()
            .atMost(2, SECONDS)
            .untilAtomic(expectedMessage, equalTo("ping"));
    }

    @Test
    public void shouldSendAndReceiveMultiFrameBinaryMessage() {
        HttpClientOptions options = new HttpClientOptions()
            .setMaxWebsocketFrameSize(5);
        client = new VertxWebSocketClient(vertx, options);

        AtomicReference<String> expectedMessage = new AtomicReference<>();

        client.execute(URI.create(BASE_URL + "/echo"), session -> {
            WebSocketMessage originalMessage = session.binaryMessage(factory -> factory.wrap("ping pong".getBytes()));

            Mono<Void> outputMono = session.send(Mono.just(originalMessage));
            Mono<Void> inputMono = session.receive()
                .filter(message -> message.getType().equals(WebSocketMessage.Type.BINARY))
                .map(WebSocketMessage::getPayloadAsText)
                .doOnNext(expectedMessage::set)
                .then();

            return outputMono.then(inputMono);
        }).subscribe();

        await()
            .atMost(2, SECONDS)
            .untilAtomic(expectedMessage, equalTo("ping pong"));
    }

    @Test
    public void shouldSendPingAndReceivePong() {
        AtomicReference<String> expectedMessage = new AtomicReference<>();

        // Ping should be handled by a server, not a handler
        client.execute(URI.create(BASE_URL + "/sink"), session -> {
            WebSocketMessage originalMessage = session.pingMessage(factory -> factory.wrap("ping".getBytes()));

            Mono<Void> outputMono = session.send(Mono.just(originalMessage));
            Mono<Void> inputMono = session.receive()
                .filter(message -> message.getType().equals(WebSocketMessage.Type.PONG))
                .map(WebSocketMessage::getPayloadAsText)
                .doOnNext(expectedMessage::set)
                .then();

            return outputMono.then(inputMono);
        }).subscribe();

        await()
            .atMost(2, SECONDS)
            .untilAtomic(expectedMessage, equalTo("ping"));
    }

    @Test
    public void shouldSendAndReceivePong() {
        AtomicReference<String> expectedMessage = new AtomicReference<>();

        client.execute(URI.create(BASE_URL + "/echo"), session -> {
            WebSocketMessage originalMessage = session.pongMessage(factory -> factory.wrap("pong".getBytes()));

            Mono<Void> outputMono = session.send(Mono.just(originalMessage));
            Mono<Void> inputMono = session.receive()
                .filter(message -> message.getType().equals(WebSocketMessage.Type.PONG))
                .map(WebSocketMessage::getPayloadAsText)
                .doOnNext(expectedMessage::set)
                .then();

            return outputMono.then(inputMono);
        }).subscribe();

        await()
            .atMost(2, SECONDS)
            .untilAtomic(expectedMessage, equalTo("pong"));
    }

    @Test
    public void clientShouldCloseSocket() {
        Mono<Void> completionMono = client.execute(URI.create(BASE_URL + "/echo"), WebSocketSession::close);

        StepVerifier.create(completionMono)
            .expectComplete()
            .verify(Duration.ofSeconds(2));
    }

    @Test
    public void serverShouldCloseSocket() {
        Mono<Void> completionMono = client.execute(URI.create(BASE_URL + "/close"), session -> Mono.empty());

        StepVerifier.create(completionMono)
            .expectComplete()
            .verify(Duration.ofSeconds(2));
    }

    @Test
    public void serverShouldSendFromTwoPublishers() {
        List<String> expectedMessages = new LinkedList<>();

        client.execute(URI.create(BASE_URL + "/double-producer"), session -> session.receive()
            .map(WebSocketMessage::getPayloadAsText)
            .doOnNext(expectedMessages::add)
            .then()
        ).subscribe();

        await()
            .atMost(2, SECONDS)
            .until(() -> expectedMessages, contains("ping", "pong"));
    }

    @TestConfiguration
    static class Handlers {

        @Bean
        public HandlerMapping handlerMapping() {
            Map<String, WebSocketHandler> map = new HashMap<>();
            map.put("/echo", this::echoHandler);
            map.put("/sink", this::sinkHandler);
            map.put("/double-producer", this::doubleProducerHandler);
            map.put("/close", this::closeHandler);

            SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
            mapping.setUrlMap(map);
            mapping.setOrder(-1);
            return mapping;
        }

        private Mono<Void> echoHandler(WebSocketSession session) {
            return session.send(session.receive());
        }

        private Mono<Void> sinkHandler(WebSocketSession session) {
            return session.receive()
                .then();
        }

        private Mono<Void> doubleProducerHandler(WebSocketSession session) {
            Mono<Void> firstSend = session.send(Mono.just(session.textMessage("ping")));
            Mono<Void> secondSend = session.send(Mono.just(session.textMessage("pong")));

            return firstSend.then(secondSend);
        }

        private Mono<Void> closeHandler(WebSocketSession session) {
            return session.close();
        }
    }
}
