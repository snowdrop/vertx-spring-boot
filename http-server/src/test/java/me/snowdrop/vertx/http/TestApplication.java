package me.snowdrop.vertx.http;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.resources;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.noContent;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@SpringBootApplication
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

    @Bean
    public RouterFunction<ServerResponse> testRouter() {
        return route()
            .GET("/echo", request -> ok().body(request.bodyToMono(String.class), String.class))
            .GET("/noop", request -> noContent().build())
            .build();
    }

    @Bean
    public RouterFunction<ServerResponse> staticRouter() {
        return resources("/**", new ClassPathResource("static"));
    }
}
