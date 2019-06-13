package dev.snowdrop.vertx.amqp;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.json.JsonArray;
import javax.json.JsonObject;

import org.springframework.core.io.buffer.DataBuffer;

public interface AmqpMessageBuilder {

    static AmqpMessageBuilder create() {
        return new SnowdropAmqpMessageBuilder();
    }

    AmqpMessage build();

    AmqpMessageBuilder priority(short priority);

    AmqpMessageBuilder durable(boolean durable);

    AmqpMessageBuilder ttl(long ttl);

    AmqpMessageBuilder firstAcquirer(boolean firstAcquirer);

    AmqpMessageBuilder deliveryCount(int deliveryCount);

    AmqpMessageBuilder id(String id);

    AmqpMessageBuilder address(String address);

    AmqpMessageBuilder replyTo(String replyTo);

    AmqpMessageBuilder correlationId(String correlationId);

    AmqpMessageBuilder withBody(String value);

    AmqpMessageBuilder withSymbolAsBody(String value);

    AmqpMessageBuilder subject(String subject);

    AmqpMessageBuilder contentType(String contentType);

    AmqpMessageBuilder contentEncoding(String contentEncoding);

    AmqpMessageBuilder expiryTime(long expiry);

    AmqpMessageBuilder creationTime(long creationTime);

    AmqpMessageBuilder groupId(String groupId);

    AmqpMessageBuilder replyToGroupId(String replyToGroupId);

    AmqpMessageBuilder applicationProperties(Map<String, Object> applicationProperties);

    AmqpMessageBuilder withBooleanAsBody(boolean value);

    AmqpMessageBuilder withByteAsBody(byte value);

    AmqpMessageBuilder withShortAsBody(short value);

    AmqpMessageBuilder withIntegerAsBody(int value);

    AmqpMessageBuilder withLongAsBody(long value);

    AmqpMessageBuilder withFloatAsBody(float value);

    AmqpMessageBuilder withDoubleAsBody(double value);

    AmqpMessageBuilder withCharAsBody(char value);

    AmqpMessageBuilder withInstantAsBody(Instant value);

    AmqpMessageBuilder withUuidAsBody(UUID value);

    AmqpMessageBuilder withListAsBody(List list);

    AmqpMessageBuilder withMapAsBody(Map map);

    AmqpMessageBuilder withBufferAsBody(DataBuffer value);

    AmqpMessageBuilder withJsonObjectAsBody(JsonObject jsonObject);

    AmqpMessageBuilder withJsonArrayAsBody(JsonArray jsonArray);
}
