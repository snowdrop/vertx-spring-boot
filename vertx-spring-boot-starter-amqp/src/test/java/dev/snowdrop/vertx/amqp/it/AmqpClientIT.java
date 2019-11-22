package dev.snowdrop.vertx.amqp.it;

import java.time.Duration;
import java.util.Arrays;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;

import dev.snowdrop.vertx.amqp.AmqpClient;
import dev.snowdrop.vertx.amqp.AmqpConnection;
import dev.snowdrop.vertx.amqp.AmqpMessage;
import dev.snowdrop.vertx.amqp.AmqpReceiver;
import dev.snowdrop.vertx.amqp.AmqpSender;
import dev.snowdrop.vertx.streams.Pump;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = "vertx.amqp.port=61616")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class AmqpClientIT {

    private static final Duration TIMEOUT = Duration.ofSeconds(2);

    @Autowired
    private AmqpClient client;

    private AmqpConnection connection;

    @After
    public void tearDown() {
        client.close().block();
    }

    @Test
    public void testBasicSendAndReceiveFlux() {
        Flux<String> receivedMessagesFlux = createReceiver("test-queue")
            .flux()
            .map(AmqpMessage::bodyAsString);

        AmqpSender sender = createSender("test-queue");
        Mono<Void> ackMono = Flux.just("first", "second", "third")
            .map(string -> AmqpMessage.create().withBody(string).build())
            .flatMap(sender::sendWithAck)
            .then();

        StepVerifier.create(ackMono)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
        StepVerifier.create(receivedMessagesFlux)
            .expectNext("first")
            .expectNext("second")
            .expectNext("third")
            .thenCancel()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void testBasicSendAndReceiveMono() {
        Mono<String> receivedMessagesMono = createReceiver("test-queue")
            .mono()
            .map(AmqpMessage::bodyAsString);

        AmqpSender sender = createSender("test-queue");
        Mono<Void> ackMono = Mono.just("first")
            .map(string -> AmqpMessage.create().withBody(string).build())
            .flatMap(sender::sendWithAck)
            .then();

        StepVerifier.create(ackMono)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
        StepVerifier.create(receivedMessagesMono)
            .expectNext("first")
            .thenCancel()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void testAnonymousSenderAndDynamicReceiver() {
        AmqpReceiver receiver = createDynamicReceiver();
        AmqpSender sender = createAnonymousSender();

        Flux<String> receivedMessagesFlux = receiver.flux()
            .map(AmqpMessage::bodyAsString);

        Mono<Void> ackMono = Flux.just("first", "second", "third")
            .map(body -> AmqpMessage.create().address(receiver.address()).withBody(body).build())
            .flatMap(sender::sendWithAck)
            .then();

        StepVerifier.create(ackMono)
            .expectComplete()
            .verify(Duration.ofSeconds(5));

        StepVerifier.create(receivedMessagesFlux)
            .expectNext("first")
            .expectNext("second")
            .expectNext("third")
            .thenCancel()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void testPipe() {
        AmqpSender source = createSender("input-queue");
        AmqpReceiver input = createReceiver("input-queue");
        AmqpSender output = createSender("output-queue");
        AmqpReceiver result = createReceiver("output-queue");

        input.pipeTo(output);
        Flux<String> receivedMessagesFlux = result.flux()
            .map(AmqpMessage::bodyAsString);

        Mono<Void> endMono = Flux.just("first", "second", "third")
            .map(body -> AmqpMessage.create().withBody(body).build())
            .flatMap(source::sendWithAck)
            .then();

        StepVerifier.create(endMono)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
        StepVerifier.create(receivedMessagesFlux)
            .expectNext("first")
            .expectNext("second")
            .expectNext("third")
            .thenCancel()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void testPump() {
        AmqpSender source = createSender("input-queue");
        AmqpReceiver input = createReceiver("input-queue");
        AmqpSender output = createSender("output-queue");
        AmqpReceiver result = createReceiver("output-queue");

        Pump.pump(input, output).start();

        Flux<String> receivedMessagesFlux = result.flux()
            .map(AmqpMessage::bodyAsString);

        Mono<Void> endMono = Flux.just("first", "second", "third")
            .map(body -> AmqpMessage.create().withBody(body).build())
            .flatMap(source::sendWithAck)
            .then();

        StepVerifier.create(endMono)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
        StepVerifier.create(receivedMessagesFlux)
            .expectNext("first")
            .expectNext("second")
            .expectNext("third")
            .thenCancel()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void testJsonObject() {
        JsonObject original = Json.createObjectBuilder()
            .add("integer", 1)
            .add("boolean", true)
            .add("string", "test")
            .add("object", Json.createObjectBuilder().add("key", "value"))
            .add("array", Json.createArrayBuilder(Arrays.asList(1, 2, 3)))
            .build();

        Mono<JsonObject> resultMono = createReceiver("test-queue")
            .mono()
            .map(AmqpMessage::bodyAsJsonObject);

        AmqpMessage message = AmqpMessage.create()
            .withJsonObjectAsBody(original)
            .build();
        createSender("test-queue")
            .send(message);

        StepVerifier.create(resultMono)
            .expectNext(original)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void testJsonArray() {
        JsonArray original = Json.createArrayBuilder()
            .add(1)
            .add(true)
            .add("test")
            .add(Json.createObjectBuilder().add("key", "value"))
            .add(Json.createArrayBuilder(Arrays.asList(1, 2, 3)))
            .build();

        Mono<JsonArray> resultMono = createReceiver("test-queue")
            .mono()
            .map(AmqpMessage::bodyAsJsonArray);

        AmqpMessage message = AmqpMessage.create()
            .withJsonArrayAsBody(original)
            .build();
        createSender("test-queue")
            .send(message);

        StepVerifier.create(resultMono)
            .expectNext(original)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void testDataBuffer() {
        DataBuffer original = new DefaultDataBufferFactory().wrap("test".getBytes());

        Mono<DataBuffer> resultMono = createReceiver("test-queue")
            .mono()
            .map(AmqpMessage::bodyAsBinary);

        AmqpMessage message = AmqpMessage.create()
            .withBufferAsBody(original)
            .build();
        createSender("test-queue")
            .send(message);

        StepVerifier.create(resultMono)
            .expectNext(original)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void testRequestResponse() {
        AmqpSender sender = createAnonymousSender();
        AmqpReceiver requestReceiver = createDynamicReceiver();
        AmqpReceiver responseReceiver = createDynamicReceiver();

        requestReceiver.flux()
            .subscribe(message -> {
                AmqpMessage response = AmqpMessage.create()
                    .address(message.replyTo())
                    .correlationId(message.id())
                    .withBody(message.bodyAsString().toUpperCase())
                    .build();
                sender.send(response);
            });

        AmqpMessage request = AmqpMessage.create()
            .id("test-id")
            .address(requestReceiver.address())
            .replyTo(responseReceiver.address())
            .withBody("test")
            .build();
        sender.send(request);

        StepVerifier.create(responseReceiver.flux())
            .assertNext(message -> {
                assertThat(message.correlationId()).isEqualTo(request.id());
                assertThat(message.bodyAsString()).isEqualTo(request.bodyAsString().toUpperCase());
            })
            .thenCancel()
            .verify(Duration.ofSeconds(5));
    }

    private AmqpConnection createConnection() {
        if (connection == null) {
            connection = client
                .connect()
                .blockOptional(TIMEOUT)
                .orElseThrow(() -> new RuntimeException("Unable to create a connection"));
        }

        return connection;
    }

    private AmqpSender createSender(String address) {
        return client
            .createSender(address)
            .blockOptional(TIMEOUT)
            .orElseThrow(() -> new RuntimeException("Unable to create a sender"));
    }

    private AmqpSender createAnonymousSender() {
        return createConnection()
            .createAnonymousSender()
            .blockOptional(TIMEOUT)
            .orElseThrow(() -> new RuntimeException("Unable to create a sender"));
    }

    private AmqpReceiver createReceiver(String address) {
        return client
            .createReceiver(address)
            .blockOptional(TIMEOUT)
            .orElseThrow(() -> new RuntimeException("Unable to create a receiver"));
    }

    private AmqpReceiver createDynamicReceiver() {
        return createConnection()
            .createDynamicReceiver()
            .blockOptional(TIMEOUT)
            .orElseThrow(() -> new RuntimeException("Unable to create a receiver"));
    }
}
