package me.snowdrop.vertx.http;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
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
            .GET("/cookie-counter", request -> {
                int counter = request.cookies()
                    .get("counter")
                    .stream()
                    .map(HttpCookie::getValue)
                    .map(Integer::valueOf)
                    .findAny()
                    .orElse(0);

                ResponseCookie cookie = ResponseCookie.from("counter", String.valueOf(counter++)).build();
                return noContent().cookie(cookie).build();
            })
            .GET("/header-counter", request -> {
                int counter = request.headers()
                    .header("counter")
                    .stream()
                    .map(Integer::valueOf)
                    .findAny()
                    .orElse(0);

                return noContent().header("counter", String.valueOf(counter++)).build();
            })
            .build();
    }

    @Bean
    public RouterFunction<ServerResponse> staticRouter() {
        return resources("/**", new ClassPathResource("static"));
    }
}
