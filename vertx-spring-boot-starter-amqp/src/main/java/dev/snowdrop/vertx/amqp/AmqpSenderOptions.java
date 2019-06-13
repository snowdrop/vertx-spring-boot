package dev.snowdrop.vertx.amqp;

public class AmqpSenderOptions {

    private final io.vertx.amqp.AmqpSenderOptions delegate = new io.vertx.amqp.AmqpSenderOptions();

    public String getLinkName() {
        return delegate.getLinkName();
    }

    public AmqpSenderOptions setLinkName(String linkName) {
        delegate.setLinkName(linkName);

        return this;
    }

    public boolean isDynamic() {
        return delegate.isDynamic();
    }

    public AmqpSenderOptions setDynamic(boolean dynamic) {
        delegate.setDynamic(dynamic);

        return this;
    }

    public boolean isAutoDrained() {
        return delegate.isAutoDrained();
    }

    public AmqpSenderOptions setAutoDrained(boolean autoDrained) {
        delegate.setAutoDrained(autoDrained);

        return this;
    }

    io.vertx.amqp.AmqpSenderOptions toVertxAmqpSenderOptions() {
        return delegate;
    }
}
