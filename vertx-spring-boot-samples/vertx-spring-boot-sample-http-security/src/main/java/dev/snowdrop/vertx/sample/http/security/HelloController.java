package dev.snowdrop.vertx.sample.http.security;

import java.security.Principal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class HelloController {

    @GetMapping("/")
    public Mono<String> hello(Mono<Principal> principal) {
        return principal
            .map(Principal::getName)
            .map(this::helloMessage);
    }

    private String helloMessage(String username) {
        return "Hello, " + username + "!";
    }
}
