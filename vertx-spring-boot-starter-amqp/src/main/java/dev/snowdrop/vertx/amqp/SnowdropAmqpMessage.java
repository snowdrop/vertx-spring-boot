package dev.snowdrop.vertx.amqp;

import java.io.StringReader;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;

class SnowdropAmqpMessage implements AmqpMessage {

    private final io.vertx.amqp.AmqpMessage delegate;

    private final DataBufferFactory dataBufferFactory;

    SnowdropAmqpMessage(io.vertx.amqp.AmqpMessage delegate) {
        this.delegate = delegate;
        this.dataBufferFactory = new DefaultDataBufferFactory();
    }

    @Override
    public boolean isDurable() {
        return delegate.isDurable();
    }

    @Override
    public boolean isFirstAcquirer() {
        return delegate.isFirstAcquirer();
    }

    @Override
    public int priority() {
        return delegate.priority();
    }

    @Override
    public int deliveryCount() {
        return delegate.deliveryCount();
    }

    @Override
    public long ttl() {
        return delegate.ttl();
    }

    @Override
    public String id() {
        return delegate.id();
    }

    @Override
    public String address() {
        return delegate.address();
    }

    @Override
    public String replyTo() {
        return delegate.replyTo();
    }

    @Override
    public String correlationId() {
        return delegate.correlationId();
    }

    @Override
    public boolean isBodyNull() {
        return delegate.isBodyNull();
    }

    @Override
    public boolean bodyAsBoolean() {
        return delegate.bodyAsBoolean();
    }

    @Override
    public byte bodyAsByte() {
        return delegate.bodyAsByte();
    }

    @Override
    public short bodyAsShort() {
        return delegate.bodyAsShort();
    }

    @Override
    public int bodyAsInteger() {
        return delegate.bodyAsInteger();
    }

    @Override
    public long bodyAsLong() {
        return delegate.bodyAsLong();
    }

    @Override
    public float bodyAsFloat() {
        return delegate.bodyAsFloat();
    }

    @Override
    public double bodyAsDouble() {
        return delegate.bodyAsFloat();
    }

    @Override
    public char bodyAsChar() {
        return delegate.bodyAsChar();
    }

    @Override
    public Instant bodyAsTimestamp() {
        return delegate.bodyAsTimestamp();
    }

    @Override
    public UUID bodyAsUUID() {
        return delegate.bodyAsUUID();
    }

    @Override
    public DataBuffer bodyAsBinary() {
        return dataBufferFactory.wrap(delegate.bodyAsBinary().getBytes());
    }

    @Override
    public String bodyAsString() {
        return delegate.bodyAsString();
    }

    @Override
    public String bodyAsSymbol() {
        return delegate.bodyAsSymbol();
    }

    @Override
    public <T> List<T> bodyAsList() {
        return delegate.bodyAsList();
    }

    @Override
    public <K, V> Map<K, V> bodyAsMap() {
        return delegate.bodyAsMap();
    }

    @Override
    public JsonObject bodyAsJsonObject() {
        return Json.createParser(new StringReader(delegate.bodyAsJsonObject().toString())).getObject();
    }

    @Override
    public JsonArray bodyAsJsonArray() {
        return Json.createParser(new StringReader(delegate.bodyAsJsonArray().toString())).getArray();
    }

    @Override
    public String subject() {
        return delegate.subject();
    }

    @Override
    public String contentType() {
        return delegate.contentType();
    }

    @Override
    public String contentEncoding() {
        return delegate.contentEncoding();
    }

    @Override
    public long expiryTime() {
        return delegate.expiryTime();
    }

    @Override
    public long creationTime() {
        return delegate.creationTime();
    }

    @Override
    public String groupId() {
        return delegate.groupId();
    }

    @Override
    public String replyToGroupId() {
        return delegate.replyToGroupId();
    }

    @Override
    public long groupSequence() {
        return delegate.groupSequence();
    }

    @Override
    public io.vertx.amqp.AmqpMessage toVertxAmqpMessage() {
        return delegate;
    }
}
