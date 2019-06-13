package dev.snowdrop.vertx.amqp;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.json.JsonArray;
import javax.json.JsonObject;

import io.vertx.core.buffer.Buffer;
import org.springframework.core.io.buffer.DataBuffer;

class SnowdropAmqpMessageBuilder implements AmqpMessageBuilder {

    private final io.vertx.amqp.AmqpMessageBuilder delegate;

    SnowdropAmqpMessageBuilder(io.vertx.amqp.AmqpMessageBuilder delegate) {
        this.delegate = delegate;
    }

    SnowdropAmqpMessageBuilder() {
        this(io.vertx.amqp.AmqpMessageBuilder.create());
    }

    @Override
    public AmqpMessage build() {
        return new SnowdropAmqpMessage(delegate.build());
    }

    @Override
    public AmqpMessageBuilder priority(short priority) {
        delegate.priority(priority);
        return this;
    }

    @Override
    public AmqpMessageBuilder durable(boolean durable) {
        delegate.durable(durable);
        return this;
    }

    @Override
    public AmqpMessageBuilder ttl(long ttl) {
        delegate.ttl(ttl);
        return this;
    }

    @Override
    public AmqpMessageBuilder firstAcquirer(boolean firstAcquirer) {
        delegate.firstAcquirer(firstAcquirer);
        return this;
    }

    @Override
    public AmqpMessageBuilder deliveryCount(int deliveryCount) {
        delegate.deliveryCount(deliveryCount);
        return this;
    }

    @Override
    public AmqpMessageBuilder id(String id) {
        delegate.id(id);
        return this;
    }

    @Override
    public AmqpMessageBuilder address(String address) {
        delegate.address(address);
        return this;
    }

    @Override
    public AmqpMessageBuilder replyTo(String replyTo) {
        delegate.replyTo(replyTo);
        return this;
    }

    @Override
    public AmqpMessageBuilder correlationId(String correlationId) {
        delegate.correlationId(correlationId);
        return this;
    }

    @Override
    public AmqpMessageBuilder withBody(String value) {
        delegate.withBody(value);
        return this;
    }

    @Override
    public AmqpMessageBuilder withSymbolAsBody(String value) {
        delegate.withSymbolAsBody(value);
        return this;
    }

    @Override
    public AmqpMessageBuilder subject(String subject) {
        delegate.subject(subject);
        return this;
    }

    @Override
    public AmqpMessageBuilder contentType(String contentType) {
        delegate.contentType(contentType);
        return this;
    }

    @Override
    public AmqpMessageBuilder contentEncoding(String contentEncoding) {
        delegate.contentEncoding(contentEncoding);
        return this;
    }

    @Override
    public AmqpMessageBuilder expiryTime(long expiryTime) {
        delegate.expiryTime(expiryTime);
        return this;
    }

    @Override
    public AmqpMessageBuilder creationTime(long creationTime) {
        delegate.creationTime(creationTime);
        return this;
    }

    @Override
    public AmqpMessageBuilder groupId(String groupId) {
        delegate.groupId(groupId);
        return this;
    }

    @Override
    public AmqpMessageBuilder replyToGroupId(String replyToGroupId) {
        delegate.replyToGroupId(replyToGroupId);
        return this;
    }

    @Override
    public AmqpMessageBuilder applicationProperties(Map<String, Object> applicationProperties) {
        delegate.applicationProperties(new io.vertx.core.json.JsonObject(applicationProperties));
        return this;
    }

    @Override
    public AmqpMessageBuilder withBooleanAsBody(boolean value) {
        delegate.withBooleanAsBody(value);
        return this;
    }

    @Override
    public AmqpMessageBuilder withByteAsBody(byte value) {
        delegate.withByteAsBody(value);
        return this;
    }

    @Override
    public AmqpMessageBuilder withShortAsBody(short value) {
        delegate.withShortAsBody(value);
        return this;
    }

    @Override
    public AmqpMessageBuilder withIntegerAsBody(int value) {
        delegate.withIntegerAsBody(value);
        return this;
    }

    @Override
    public AmqpMessageBuilder withLongAsBody(long value) {
        delegate.withLongAsBody(value);
        return this;
    }

    @Override
    public AmqpMessageBuilder withFloatAsBody(float value) {
        delegate.withFloatAsBody(value);
        return this;
    }

    @Override
    public AmqpMessageBuilder withDoubleAsBody(double value) {
        delegate.withDoubleAsBody(value);
        return this;
    }

    @Override
    public AmqpMessageBuilder withCharAsBody(char value) {
        delegate.withCharAsBody(value);
        return this;
    }

    @Override
    public AmqpMessageBuilder withInstantAsBody(Instant value) {
        delegate.withInstantAsBody(value);
        return this;
    }

    @Override
    public AmqpMessageBuilder withUuidAsBody(UUID value) {
        delegate.withUuidAsBody(value);
        return this;
    }

    @Override
    public AmqpMessageBuilder withListAsBody(List list) {
        delegate.withListAsBody(list);
        return this;
    }

    @Override
    public AmqpMessageBuilder withMapAsBody(Map map) {
        delegate.withMapAsBody(map);
        return this;
    }

    @Override
    public AmqpMessageBuilder withBufferAsBody(DataBuffer value) {
        delegate.withBufferAsBody(Buffer.buffer(value.asByteBuffer().array()));
        return this;
    }

    @Override
    public AmqpMessageBuilder withJsonObjectAsBody(JsonObject jsonObject) {
        delegate.withJsonObjectAsBody(new io.vertx.core.json.JsonObject(jsonObject.toString()));
        return this;
    }

    @Override
    public AmqpMessageBuilder withJsonArrayAsBody(JsonArray jsonArray) {
        delegate.withJsonArrayAsBody(new io.vertx.core.json.JsonArray(jsonArray.toString()));
        return this;
    }
}
