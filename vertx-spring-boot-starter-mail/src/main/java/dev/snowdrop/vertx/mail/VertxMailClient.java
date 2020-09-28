package dev.snowdrop.vertx.mail;

import io.smallrye.mutiny.converters.uni.UniReactorConverters;
import reactor.core.publisher.Mono;

class VertxMailClient implements MailClient {

    private final io.vertx.mutiny.ext.mail.MailClient delegate;

    private final MailMessageConverter mailMessageConverter;

    private final MailResultConverter mailResultConverter;

    VertxMailClient(io.vertx.mutiny.ext.mail.MailClient delegate, MailMessageConverter mailMessageConverter,
        MailResultConverter mailResultConverter) {
        this.delegate = delegate;
        this.mailMessageConverter = mailMessageConverter;
        this.mailResultConverter = mailResultConverter;
    }

    @Override
    public Mono<MailResult> send(MailMessage message) {
        return mailMessageConverter
            .toVertxMailMessage(message)
            .flatMap(vertxMessage -> delegate.sendMail(vertxMessage)
                .convert()
                .with(UniReactorConverters.toMono()))
            .map(mailResultConverter::fromVertxMailResult);
    }
}
