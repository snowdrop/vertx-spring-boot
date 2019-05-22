package dev.snowdrop.vertx.http.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

@RunWith(SpringRunner.class)
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
