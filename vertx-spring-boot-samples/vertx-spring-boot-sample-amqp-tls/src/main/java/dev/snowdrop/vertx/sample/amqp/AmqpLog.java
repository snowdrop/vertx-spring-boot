package dev.snowdrop.vertx.sample.amqp;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import dev.snowdrop.vertx.amqp.AmqpClient;
import dev.snowdrop.vertx.amqp.AmqpMessage;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import reactor.core.Disposable;

import static dev.snowdrop.vertx.sample.amqp.AmqpSampleApplication.QUEUE;

final class AmqpLog implements InitializingBean, DisposableBean {

    private final List<String> messages = new CopyOnWriteArrayList<>();

    private final AmqpClient client;

    private Disposable receiverDisposer;

    AmqpLog(AmqpClient client) {
        this.client = client;
    }

    @Override
    public void afterPropertiesSet() {
        receiverDisposer = client.createReceiver(QUEUE)
            .flatMapMany(receiver -> receiver.flux()
                .doOnCancel(() -> receiver.close().block())) // Close the receiver once subscription is disposed
            .subscribe(this::handleMessage);
    }

    @Override
    public void destroy() {
        if (receiverDisposer != null) {
            receiverDisposer.dispose();
        }
    }

    public List<String> getMessages() {
        return messages;
    }

    private void handleMessage(AmqpMessage message) {
        System.out.println("Received log message '" + message.bodyAsString() + "'");

        messages.add(message.bodyAsString());
    }
}
