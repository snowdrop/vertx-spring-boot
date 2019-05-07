package me.snowdrop.vertx.http.it;

import java.net.URI;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

import io.vertx.core.http.WebsocketRejectedException;
import org.junit.After;
import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;

public class WebSocketIT extends TestBase {

    @After
    public void tearDown() {
        stopServer();
    }

    @Test
    public void shouldSendAndReceiveTextMessage() {
        startServer(Handlers.class);

        AtomicReference<String> expectedMessage = new AtomicReference<>();

        getWebSocketClient()
            .execute(URI.create(WS_BASE_URL + "/echo"), session -> {
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
        Properties properties = new Properties();
        properties.setProperty("vertx.http.client.maxWebsocketFrameSize", "5");
        properties.setProperty("vertx.http.server.maxWebsocketFrameSize", "5");
        startServer(properties, Handlers.class);

        AtomicReference<String> expectedMessage = new AtomicReference<>();

        getWebSocketClient()
            .execute(URI.create(WS_BASE_URL + "/echo"), session -> {
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
        startServer(Handlers.class);

        AtomicReference<String> expectedMessage = new AtomicReference<>();

        getWebSocketClient()
            .execute(URI.create(BASE_URL + "/echo"), session -> {
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
        Properties properties = new Properties();
        properties.setProperty("vertx.http.client.maxWebsocketFrameSize", "5");
        properties.setProperty("vertx.http.server.maxWebsocketFrameSize", "5");
        startServer(properties, Handlers.class);

        AtomicReference<String> expectedMessage = new AtomicReference<>();

        getWebSocketClient()
            .execute(URI.create(BASE_URL + "/echo"), session -> {
                WebSocketMessage originalMessage =
                    session.binaryMessage(factory -> factory.wrap("ping pong".getBytes()));

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
        startServer(Handlers.class);

        AtomicReference<String> expectedMessage = new AtomicReference<>();

        // Ping should be handled by a server, not a handler
        getWebSocketClient()
            .execute(URI.create(BASE_URL + "/sink"), session -> {
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
        startServer(Handlers.class);

        AtomicReference<String> expectedMessage = new AtomicReference<>();

        getWebSocketClient()
            .execute(URI.create(BASE_URL + "/echo"), session -> {
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
        startServer(Handlers.class);

        getWebSocketClient()
            .execute(URI.create(BASE_URL + "/echo"), WebSocketSession::close)
            .block(Duration.ofSeconds(2));
    }

    @Test
    public void serverShouldCloseSocket() {
        startServer(Handlers.class);

        getWebSocketClient()
            .execute(URI.create(BASE_URL + "/close"), session -> Mono.empty())
            .block(Duration.ofSeconds(2));
    }

    @Test
    public void serverShouldSendFromTwoPublishers() {
        startServer(Handlers.class);

        List<String> expectedMessages = new LinkedList<>();

        getWebSocketClient()
            .execute(URI.create(BASE_URL + "/double-producer"), session -> session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .doOnNext(expectedMessages::add)
                .then()
            ).subscribe();

        await()
            .atMost(2, SECONDS)
            .until(() -> expectedMessages, contains("ping", "pong"));
    }

    @Test
    public void testAllowedCorsOrigin() {
        startServer(Handlers.class);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ORIGIN, "http://snowdrop.dev");

        getWebSocketClient()
            .execute(URI.create(WS_BASE_URL + "/sink"), headers, session -> Mono.empty())
            .block(Duration.ofSeconds(2));
    }

    @Test(expected = WebsocketRejectedException.class)
    public void testNotAllowedCorsOrigin() {
        startServer(Handlers.class);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ORIGIN, "http://example.com");

        getWebSocketClient()
            .execute(URI.create(WS_BASE_URL + "/sink"), headers, session -> Mono.empty())
            .block(Duration.ofSeconds(2));
    }

    @Configuration
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

            CorsConfiguration cors = new CorsConfiguration();
            cors.addAllowedOrigin("http://snowdrop.dev");
            mapping.setCorsConfigurations(Collections.singletonMap("/sink", cors));

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
