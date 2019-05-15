package dev.snowdrop.vertx.sample.chunked;

import java.time.Duration;
import java.util.List;

import dev.snowdrop.vertx.mail.EmailService;
import io.vertx.ext.mail.MailMessage;
import io.vertx.ext.mail.MailResult;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class DataHandler {

    private static final String FROM_ADDRESS = "examples@snowdrop.dev";

    private final EmailService emailService;

    private final WebClient client;

    public DataHandler(EmailService emailService, WebClient.Builder clientBuilder) {
        this.emailService = emailService;
        this.client = clientBuilder
            .baseUrl("https://httpbin.org")
            .build();
    }

    public Mono<ServerResponse> get(ServerRequest request) {
        String count = request.queryParam("count")
            .orElseThrow(() -> new ServerWebInputException("Count is required"));
        String email = request.queryParam("email")
            .orElseThrow(() -> new ServerWebInputException("Email is required"));

        System.out.println(String.format("Request for %s entries", count));

        // Get data from httpbin
        Flux<String> chunks = client.get()
            .uri("/stream/{count}", count)
            .retrieve()
            .bodyToFlux(String.class)
            .log()
            // Delay to make a stream of data easily visible in the UI
            .delayElements(Duration.ofMillis(200))
            .share();

        // Send batches of 10 entries by email
        chunks.buffer(10)
            .subscribe(entries -> this.sendEmail(email, entries));

        // Return a stream of entries to the requester
        return ok()
            .contentType(APPLICATION_JSON)
            .body(chunks, String.class);
    }

    private Mono<MailResult> sendEmail(String address, List<String> entries) {
        System.out.println("Sending an email with " + entries.size() + " entries to " + address);

        MailMessage message = new MailMessage()
            .setFrom(FROM_ADDRESS)
            .setTo(address)
            .setSubject(String.format("%d entries from httpbin", entries.size()))
            .setText(String.join(", ", entries));

        return emailService.send(message);
    }

}
