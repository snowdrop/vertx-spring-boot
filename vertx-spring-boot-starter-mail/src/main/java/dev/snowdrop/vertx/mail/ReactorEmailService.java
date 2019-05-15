package dev.snowdrop.vertx.mail;

import dev.snowdrop.vertx.mail.axel.Email;
import reactor.core.publisher.Mono;

// TODO maybe a better name?
public interface ReactorEmailService {

    Mono<Void> send(Email email);

}
