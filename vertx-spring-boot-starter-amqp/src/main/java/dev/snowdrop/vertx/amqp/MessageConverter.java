package dev.snowdrop.vertx.amqp;

class MessageConverter {

    io.vertx.mutiny.amqp.AmqpMessage toMutinyMessage(dev.snowdrop.vertx.amqp.AmqpMessage snowdropMessage) {
        return new io.vertx.mutiny.amqp.AmqpMessage(snowdropMessage.toVertxAmqpMessage());
    }

    dev.snowdrop.vertx.amqp.AmqpMessage toSnowdropMessage(io.vertx.mutiny.amqp.AmqpMessage mutinyMessage) {
        return new dev.snowdrop.vertx.amqp.SnowdropAmqpMessage(mutinyMessage.getDelegate());
    }
}
