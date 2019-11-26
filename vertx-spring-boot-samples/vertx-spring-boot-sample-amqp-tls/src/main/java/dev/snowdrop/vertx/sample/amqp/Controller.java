package dev.snowdrop.vertx.sample.amqp;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

/**
 * HTTP controller exposing GET and POST resources to log messages and to receive the previously logged ones.
 */
@RestController
public class Controller {

    private final AmqpLogger logger;

    private final AmqpLog log;

    public Controller(AmqpLogger logger, AmqpLog log) {
        this.logger = logger;
        this.log = log;
    }

    /**
     * Get a flux of previously logged messages.
     */
    @GetMapping(produces = TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getMessages() {
        return Flux.fromIterable(log.getMessages());
    }

    /**
     * Log a message.
     */
    @PostMapping
    public Mono<Void> logMessage(@RequestBody String body) {
        return logger.logMessage(body.trim());
    }
}
