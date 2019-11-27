package dev.snowdrop.vertx.sample.kafka;

import java.time.Duration;
import java.util.List;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class KafkaSampleApplicationTest {

    @ClassRule
    public static EmbeddedKafkaRule EMBEDDED_KAFKA_RULE = new EmbeddedKafkaRule(1);

    static {
        EMBEDDED_KAFKA_RULE.kafkaPorts(9092);
    }

    @Autowired
    private WebTestClient client;

    @Test
    public void shouldLogAndReceiveMessages() {
        logMessage("first");
        logMessage("second");

        await()
            .atMost(Duration.ofSeconds(2))
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
            .block(Duration.ofSeconds(2));
    }

    private void logMessage(String message) {
        client.post()
            .syncBody(message)
            .exchange()
            .expectStatus()
            .isOk();
    }
}
