package dev.snowdrop.vertx.amqp;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.json.JsonArray;
import javax.json.JsonObject;

import org.springframework.core.io.buffer.DataBuffer;

public interface AmqpMessage {

    static AmqpMessageBuilder create() {
        return new SnowdropAmqpMessageBuilder();
    }

    boolean isDurable();

    boolean isFirstAcquirer();

    int priority();

    int deliveryCount();

    long ttl();

    String id();

    String address();

    String replyTo();

    String correlationId();

    boolean isBodyNull();

    boolean bodyAsBoolean();

    byte bodyAsByte();

    short bodyAsShort();

    int bodyAsInteger();

    long bodyAsLong();

    float bodyAsFloat();

    double bodyAsDouble();

    char bodyAsChar();

    Instant bodyAsTimestamp();

    UUID bodyAsUUID();

    DataBuffer bodyAsBinary();

    String bodyAsString();

    String bodyAsSymbol();

    <T> List<T> bodyAsList();

    <K, V> Map<K, V> bodyAsMap();

    JsonObject bodyAsJsonObject();

    JsonArray bodyAsJsonArray();

    String subject();

    String contentType();

    String contentEncoding();

    long expiryTime();

    long creationTime();

    String groupId();

    String replyToGroupId();

    long groupSequence();

    io.vertx.amqp.AmqpMessage toVertxAmqpMessage();
}
