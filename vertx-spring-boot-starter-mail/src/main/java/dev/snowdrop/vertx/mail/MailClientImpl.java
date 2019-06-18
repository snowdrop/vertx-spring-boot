package dev.snowdrop.vertx.mail;

import dev.snowdrop.vertx.mail.converter.MailMessageConverter;
import dev.snowdrop.vertx.mail.converter.MailResultConverter;
import io.vertx.axle.core.Vertx;
import reactor.core.publisher.Mono;

class MailClientImpl implements MailClient {

    private final io.vertx.axle.ext.mail.MailClient delegate;

    private final MailMessageConverter mailMessageConverter;

    private final MailResultConverter mailResultConverter;

    MailClientImpl(Vertx vertx, io.vertx.axle.ext.mail.MailClient delegate) {
        this.delegate = delegate;
        this.mailMessageConverter = new MailMessageConverter(vertx);
        this.mailResultConverter = new MailResultConverter();
    }

    @Override
    public Mono<MailResult> send(MailMessage message) {
        return mailMessageConverter
            .toVertxMailMessage(message)
            .map(delegate::sendMail)
            .flatMap(Mono::fromCompletionStage)
            .map(mailResultConverter::fromVertxMailResult);
    }
}
