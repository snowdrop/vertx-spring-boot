package dev.snowdrop.vertx.sample.amqp;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

/**
 * Rest controller exposing GET and POST resources to receive processed messages and submit messages for processing.
 */
@RestController
public class Controller {

    private final MessagesManager messagesManager;

    public Controller(MessagesManager messagesManager) {
        this.messagesManager = messagesManager;
    }

    /**
     * Get a flux of messages processed up to this point.
     */
    @GetMapping(produces = TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getProcessedMessages() {
        return Flux.fromIterable(messagesManager.getProcessedMessages());
    }

    /**
     * Submit a message for processing by publishing it to a processing requests queue.
     */
    @PostMapping
    public Mono<Void> submitMessageForProcessing(@RequestBody String body) {
        return messagesManager.processMessage(body.trim());
    }
}
