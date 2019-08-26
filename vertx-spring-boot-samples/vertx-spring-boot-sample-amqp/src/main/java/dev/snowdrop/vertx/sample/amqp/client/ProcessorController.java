package dev.snowdrop.vertx.sample.amqp.client;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Rest controller exposing GET and POST resources to receive processed messages and submit messages for processing.
 */
@RestController
public class ProcessorController {

    private final ProcessorClient processorClient;

    public ProcessorController(ProcessorClient processorClient) {
        this.processorClient = processorClient;
    }

    /**
     * Get messages which were processed up to this time.
     */
    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getProcessedMessages() {
        return Flux.fromIterable(processorClient.getProcessedMessages());
    }

    /**
     * Submit message for processing.
     */
    @PostMapping
    public Mono<Void> processMessage(@RequestBody String body) {
        return processorClient.processMessage(body.trim());
    }
}
