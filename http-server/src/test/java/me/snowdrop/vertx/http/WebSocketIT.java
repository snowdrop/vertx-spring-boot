package me.snowdrop.vertx.http;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;

@RunWith(SpringRunner.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
    properties = {
        "server.port=" + WebSocketIT.PORT,
        "vertx.http.server.maxWebsocketFrameSize=5"
    }
)
public class WebSocketIT {

    static final int PORT = 8082;

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketIT.class);

    @Autowired
    private Vertx vertx;

    private HttpClient client;

    @After
    public void tearDown() {
        if (client != null) {
            client.close();
        }
    }

    @Test
    public void shouldSendAndReceiveTextMessage() {
        client = vertx.createHttpClient();

        AtomicReference<String> message = new AtomicReference<>();
        client.websocket(PORT, "localhost", "/echo", socket -> {
            socket.textMessageHandler(message::set);
            socket.writeTextMessage("ping");
        });

        await()
            .atMost(2, SECONDS)
            .untilAtomic(message, equalTo("ping"));
    }

    @Test
    public void shouldSendAndReceiveMultiFrameTextMessage() {
        HttpClientOptions options = new HttpClientOptions()
            .setMaxWebsocketFrameSize(5);
        client = vertx.createHttpClient(options);

        AtomicReference<String> message = new AtomicReference<>();
        client.websocket(PORT, "localhost", "/echo", socket -> {
            socket.textMessageHandler(message::set);
            socket.writeTextMessage("ping pong");
        });

        await()
            .atMost(2, SECONDS)
            .untilAtomic(message, equalTo("ping pong"));
    }

    @Test
    public void shouldSendAndReceiveBinaryMessage() {
        client = vertx.createHttpClient();

        AtomicReference<Buffer> message = new AtomicReference<>();
        client.websocket(PORT, "localhost", "/echo", socket -> {
            socket.binaryMessageHandler(message::set);
            socket.writeBinaryMessage(Buffer.buffer("ping"));
        });

        await()
            .atMost(2, SECONDS)
            .untilAtomic(message, equalTo(Buffer.buffer("ping")));
    }

    @Test
    public void shouldSendAndReceiveMultiFrameBinaryMessage() {
        HttpClientOptions options = new HttpClientOptions()
            .setMaxWebsocketFrameSize(5);
        client = vertx.createHttpClient(options);

        AtomicReference<Buffer> message = new AtomicReference<>();
        client.websocket(PORT, "localhost", "/echo", socket -> {
            socket.binaryMessageHandler(message::set);
            socket.writeBinaryMessage(Buffer.buffer("ping pong"));
        });

        await()
            .atMost(2, SECONDS)
            .untilAtomic(message, equalTo(Buffer.buffer("ping pong")));
    }

    @Test
    public void shouldSendAndReceivePing() {
        client = vertx.createHttpClient();

        AtomicReference<Buffer> message = new AtomicReference<>();
        // Ping should be handled by a server, not a handler
        client.websocket(PORT, "localhost", "/sink", socket -> {
            socket.pongHandler(message::set);
            socket.writePing(Buffer.buffer("ping"));
        });

        await()
            .atMost(2, SECONDS)
            .untilAtomic(message, equalTo(Buffer.buffer("ping")));
    }

    @Test
    public void shouldSendAndReceivePong() {
        client = vertx.createHttpClient();

        AtomicReference<Buffer> message = new AtomicReference<>();
        client.websocket(PORT, "localhost", "/echo", socket -> {
            socket.pongHandler(message::set);
            socket.writePong(Buffer.buffer("pong"));
        });

        await()
            .atMost(2, SECONDS)
            .untilAtomic(message, equalTo(Buffer.buffer("pong")));
    }

    @Test
    public void shouldCloseSocket() {
        client = vertx.createHttpClient();

        AtomicBoolean closed = new AtomicBoolean(false);
        client.websocket(PORT, "localhost", "/close", socket -> {
            socket.closeHandler(event -> closed.set(true));
        });

        await()
            .atMost(2, SECONDS)
            .untilAtomic(closed, equalTo(true));
    }

    @TestConfiguration
    static class Handlers {

        @Bean
        public HandlerMapping handlerMapping() {
            Map<String, WebSocketHandler> map = new HashMap<>();
            map.put("/echo", this::echoHandler);
            map.put("/sink", this::sinkHandler);
            map.put("/close", this::closeHandler);

            SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
            mapping.setUrlMap(map);
            mapping.setOrder(-1);
            return mapping;
        }

        private Mono<Void> echoHandler(WebSocketSession session) {
            Flux<WebSocketMessage> output = session.receive()
                .log()
                .doOnNext(message -> LOGGER.info("Echo handler received {}", message));

            return session.send(output);
        }

        private Mono<Void> sinkHandler(WebSocketSession session) {
            return session.receive()
                .log()
                .doOnNext(message -> LOGGER.info("Sink handler received {}", message))
                .then();
        }

        private Mono<Void> closeHandler(WebSocketSession session) {
            return session.close();
        }
    }
}
