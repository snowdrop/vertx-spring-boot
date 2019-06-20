package dev.snowdrop.vertx.mail;

import reactor.core.publisher.Mono;

class VertxMailClient implements MailClient {

    private final io.vertx.axle.ext.mail.MailClient delegate;

    private final MailMessageConverter mailMessageConverter;

    private final MailResultConverter mailResultConverter;

    VertxMailClient(io.vertx.axle.ext.mail.MailClient delegate, MailMessageConverter mailMessageConverter,
        MailResultConverter mailResultConverter) {
        this.delegate = delegate;
        this.mailMessageConverter = mailMessageConverter;
        this.mailResultConverter = mailResultConverter;
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
