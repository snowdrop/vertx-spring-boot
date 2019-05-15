package dev.snowdrop.vertx.mail.axel;

import java.util.concurrent.CompletionStage;

// TODO should come from an external dependency
public interface ReactiveEmailService {

    CompletionStage<Void> send(Email email);

}
