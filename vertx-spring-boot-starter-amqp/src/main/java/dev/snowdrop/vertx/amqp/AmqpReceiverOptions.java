package dev.snowdrop.vertx.amqp;

import java.util.List;

public class AmqpReceiverOptions {

    private final io.vertx.amqp.AmqpReceiverOptions delegate = new io.vertx.amqp.AmqpReceiverOptions();

    public String getLinkName() {
        return delegate.getLinkName();
    }

    public AmqpReceiverOptions setLinkName(String linkName) {
        delegate.setLinkName(linkName);

        return this;
    }

    public boolean isDynamic() {
        return delegate.isDynamic();
    }

    public AmqpReceiverOptions setDynamic(boolean dynamic) {
        delegate.setDynamic(dynamic);

        return this;
    }

    public String getQos() {
        return delegate.getQos();
    }

    public AmqpReceiverOptions setQos(String qos) {
        delegate.setQos(qos);

        return this;
    }

    public List<String> getCapabilities() {
        return delegate.getCapabilities();
    }

    public AmqpReceiverOptions setCapabilities(List<String> capabilities) {
        delegate.setCapabilities(capabilities);

        return this;
    }

    public AmqpReceiverOptions addCapability(String capability) {
        delegate.addCapability(capability);

        return this;
    }

    public boolean isDurable() {
        return delegate.isDurable();
    }

    public AmqpReceiverOptions setDurable(boolean durable) {
        delegate.setDurable(durable);

        return this;
    }

    public int getMaxBufferedMessages() {
        return delegate.getMaxBufferedMessages();
    }

    public AmqpReceiverOptions setMaxBufferedMessages(int maxBufferedMessages) {
        delegate.setMaxBufferedMessages(maxBufferedMessages);

        return this;
    }

    io.vertx.amqp.AmqpReceiverOptions toVertxAmqpReceiverOptions() {
        return delegate;
    }
}
