package dev.snowdrop.vertx.amqp;

class MessageConverter {

    io.vertx.axle.amqp.AmqpMessage toAxleMessage(dev.snowdrop.vertx.amqp.AmqpMessage snowdropMessage) {
        return new io.vertx.axle.amqp.AmqpMessage(snowdropMessage.toVertxAmqpMessage());
    }

    dev.snowdrop.vertx.amqp.AmqpMessage toSnowdropMessage(io.vertx.axle.amqp.AmqpMessage axleMessage) {
        return new dev.snowdrop.vertx.amqp.SnowdropAmqpMessage(axleMessage.getDelegate());
    }
}
