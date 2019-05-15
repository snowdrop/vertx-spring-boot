package dev.snowdrop.vertx.sample.mail;

import dev.snowdrop.vertx.mail.EmailService;
import io.vertx.ext.mail.MailMessage;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.ServerResponse.noContent;

@Component
public class MailHandler {

    private final EmailService emailService;

    public MailHandler(EmailService emailService) {
        this.emailService = emailService;
    }

    public Mono<ServerResponse> send(ServerRequest request) {
        return request.formData()
            .log()
            .map(this::formToMessage)
            .flatMap(emailService::send)
            .flatMap(result -> noContent().build());
    }

    private MailMessage formToMessage(MultiValueMap<String, String> form) {
        return new MailMessage()
            .setFrom(form.getFirst("from"))
            .setTo(form.getFirst("to"))
            .setSubject(form.getFirst("subject"))
            .setText(form.getFirst("text"));
    }

}
