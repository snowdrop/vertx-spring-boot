package me.snowdrop.vertx.http.it;

import io.vertx.core.Vertx;
import me.snowdrop.vertx.http.client.VertxClientHttpConnector;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;

@Category(FastTests.class)
@RunWith(SpringRunner.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
    properties = "server.port=" + Ports.HTTP_IT,
    classes = BaseHttpIT.Routers.class
)
public class HttpIT extends BaseHttpIT {

    private static final String BASE_URL = String.format("http://localhost:%d", Ports.HTTP_IT);

    @Autowired
    private Vertx vertx;

    @Override
    public WebClient getClient() {
        return WebClient.builder()
            .clientConnector(new VertxClientHttpConnector(vertx))
            .baseUrl(BASE_URL)
            .build();
    }
}
