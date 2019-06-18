package dev.snowdrop.vertx.mail;

import reactor.core.publisher.Mono;

public interface MailClient {

    Mono<MailResult> send(MailMessage message);

}
