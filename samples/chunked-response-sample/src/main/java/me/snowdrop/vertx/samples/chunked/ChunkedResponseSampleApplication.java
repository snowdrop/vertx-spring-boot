package me.snowdrop.vertx.samples.chunked;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.resources;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@SpringBootApplication
public class ChunkedResponseSampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChunkedResponseSampleApplication.class, args);
    }

    @Bean
    public RouterFunction<ServerResponse> dataRouter(DataHandler dataHandler) {
        return route()
            .GET("/data", dataHandler::get)
            .build();
    }

    @Bean
    public RouterFunction<ServerResponse> staticResourceRouter() {
        return resources("/**", new ClassPathResource("static/"));
    }
}
