package dev.snowdrop.vertx.mail.impl;

import java.util.concurrent.CompletionStage;

import dev.snowdrop.vertx.mail.EmailService;
import io.smallrye.reactive.converters.ReactiveTypeConverter;
import io.vertx.axle.ext.mail.MailClient;
import io.vertx.ext.mail.MailMessage;
import io.vertx.ext.mail.MailResult;
import reactor.core.publisher.Mono;

public class EmailServiceImpl implements EmailService {

    private final MailClient mailClient;

    private final ReactiveTypeConverter<Mono> monoConverter;

    public EmailServiceImpl(MailClient mailClient, ReactiveTypeConverter<Mono> monoConverter) {
        this.mailClient = mailClient;
        this.monoConverter = monoConverter;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Mono<MailResult> send(MailMessage message) {
        CompletionStage<MailResult> future = mailClient.sendMail(message);

        return monoConverter.fromCompletionStage(future);
    }

}
