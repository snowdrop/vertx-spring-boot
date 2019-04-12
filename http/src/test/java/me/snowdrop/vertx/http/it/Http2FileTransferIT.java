package me.snowdrop.vertx.http.it;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpVersion;
import me.snowdrop.vertx.http.client.VertxClientHttpConnector;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;

@Category(SlowTests.class)
@RunWith(SpringRunner.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
    properties = "server.port=" + Ports.HTTP_2_FILE_TRANSFER_IT,
    classes = BaseFileTransferIT.Routers.class
)
public class Http2FileTransferIT extends BaseFileTransferIT {

    private static final String BASE_URL = String.format("http://localhost:%d", Ports.HTTP_2_FILE_TRANSFER_IT);

    @Autowired
    private Vertx vertx;

    @Override
    protected WebClient getClient() {
        HttpClientOptions options = new HttpClientOptions()
            .setHttp2ClearTextUpgrade(false)
            .setProtocolVersion(HttpVersion.HTTP_2);

        return WebClient.builder()
            .clientConnector(new VertxClientHttpConnector(vertx, options))
            .baseUrl(BASE_URL)
            .build();
    }
}
