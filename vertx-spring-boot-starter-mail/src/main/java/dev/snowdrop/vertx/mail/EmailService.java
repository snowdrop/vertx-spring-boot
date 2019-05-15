package dev.snowdrop.vertx.mail;

import io.vertx.ext.mail.MailMessage;
import io.vertx.ext.mail.MailResult;
import reactor.core.publisher.Mono;

public interface EmailService {

    Mono<MailResult> send(MailMessage message);

}
