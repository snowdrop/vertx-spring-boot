package dev.snowdrop.vertx.http.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

@RunWith(SpringRunner.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "vertx.http.client.ssl=true",
        "vertx.http.client.trust-all=true",
        "vertx.http.server.ssl=true",
        "vertx.http.server.client-auth=NONE",
        "server.ssl.key-store-type=JKS",
        "server.ssl.key-store=target/test-classes/tls/server-keystore.jks",
        "server.ssl.key-store-password=wibble"
    }
)
public class DefaultWebTestClientHttpsIT {

    @Autowired
    private WebTestClient client;

    @Test
    public void testAccessToHttpsResource() {
        client
            .get()
            .exchange()
            .expectBody(String.class)
            .isEqualTo("test");
    }
}
