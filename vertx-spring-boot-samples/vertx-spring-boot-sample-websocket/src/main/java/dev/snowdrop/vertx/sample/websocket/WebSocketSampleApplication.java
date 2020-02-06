package dev.snowdrop.vertx.sample.websocket;

import java.util.Collections;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class WebSocketSampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebSocketSampleApplication.class, args);
    }

    @Bean
    public HandlerMapping handlerMapping() {
        // Define URL mapping for the socket handlers
        Map<String, WebSocketHandler> handlers = Collections.singletonMap("/echo-upper", this::toUppercaseHandler);

        SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
        handlerMapping.setUrlMap(handlers);
        // Set a higher precedence than annotated controllers (smaller value means higher precedence)
        handlerMapping.setOrder(-1);

        return handlerMapping;
    }

    private Mono<Void> toUppercaseHandler(WebSocketSession session) {
        Flux<WebSocketMessage> messages = session.receive() // Get incoming messages stream
            .filter(message -> message.getType() == WebSocketMessage.Type.TEXT) // Filter out non-text messages
            .map(message -> message.getPayloadAsText().toUpperCase()) // Execute service logic
            .map(session::textMessage); // Create a response message

        return session.send(messages); // Send response messages
    }
}
