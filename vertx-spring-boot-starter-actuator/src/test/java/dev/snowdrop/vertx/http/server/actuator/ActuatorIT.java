package dev.snowdrop.vertx.http.server.actuator;

import dev.snowdrop.vertx.http.client.VertxClientHttpConnector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "management.server.port=8081")
public class ActuatorIT {

    @LocalServerPort
    private String port;

    @Autowired
    private VertxClientHttpConnector connector;

    private WebTestClient client;

    @BeforeEach
    public void setUp() {
        client = WebTestClient
            .bindToServer(connector)
            .baseUrl("http://localhost:" + port)
            .build();
    }

    @Test
    public void shouldGetActuatorHealthWithModifiedPort() {
        client.get()
            .uri(builder -> builder
                .port("8081")
                .path("/actuator/health")
                .build())
            .exchange()
            .expectBody(String.class)
            .isEqualTo("{\"status\":\"UP\"}");

        client.get()
            .exchange()
            .expectStatus()
            .isNoContent();
    }
}
