package dev.snowdrop.vertx.sample.amqp;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AmqpSampleApplicationTest {

    @Autowired
    private WebTestClient client;

    @Test
    public void shouldLogAndReceiveMessages() {
        logMessage("first");
        logMessage("second");

        await()
            .atMost(2, SECONDS)
            .untilAsserted(() -> assertThat(getLoggedMessages()).containsOnly("first", "second"));
    }

    private List<String> getLoggedMessages() {
        return client.get()
            .accept(TEXT_EVENT_STREAM)
            .exchange()
            .expectStatus()
            .isOk()
            .returnResult(String.class)
            .getResponseBody()
            .collectList()
            .block();
    }

    private void logMessage(String message) {
        client.post()
            .bodyValue(message)
            .exchange()
            .expectStatus()
            .isOk();
    }
}
