package dev.snowdrop.vertx.http.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DefaultWebTestClientIT {

    @Autowired
    private WebTestClient client;

    @Test
    public void testAccessToHttpResource() {
        client.get()
            .exchange()
            .expectBody(String.class)
            .isEqualTo("test");
    }
}
