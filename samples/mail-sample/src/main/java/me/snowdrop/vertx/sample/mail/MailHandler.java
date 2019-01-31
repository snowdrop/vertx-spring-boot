package me.snowdrop.vertx.sample.mail;

import me.snowdrop.vertx.mail.ReactorEmailService;
import me.snowdrop.vertx.mail.axel.Email;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.ServerResponse.noContent;

@Component
public class MailHandler {

    private final ReactorEmailService emailService;

    public MailHandler(ReactorEmailService emailService) {
        this.emailService = emailService;
    }

    public Mono<ServerResponse> send(ServerRequest request) {
        return request.formData()
                .log()
                .map(this::formToEmail)
                .flatMap(emailService::send)
                .flatMap(v -> noContent().build());
    }

    private Email formToEmail(MultiValueMap<String, String> form) {
        return Email.create()
                .from(form.getFirst("from"))
                .to(form.getFirst("to"))
                .subject(form.getFirst("subject"))
                .text(form.getFirst("text"))
                .build();
    }

}
