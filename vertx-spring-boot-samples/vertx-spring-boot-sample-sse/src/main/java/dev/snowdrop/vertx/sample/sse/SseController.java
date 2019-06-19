package dev.snowdrop.vertx.sample.sse;

import java.time.Duration;
import java.util.Random;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class SseController {

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Integer> getRandomNumberStream() {
        Random random = new Random();

        return Flux.interval(Duration.ofSeconds(1))
            .map(i -> random.nextInt())
            .log();
    }
}
