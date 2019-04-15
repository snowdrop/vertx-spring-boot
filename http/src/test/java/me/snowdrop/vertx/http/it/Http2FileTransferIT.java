package me.snowdrop.vertx.http.it;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpVersion;
import me.snowdrop.vertx.http.client.VertxClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

public class Http2FileTransferIT extends HttpFileTransferIT {

    @Override
    protected WebClient getWebClient(HttpClientOptions options) {
        Vertx vertx = getBean(Vertx.class);
        options.setProtocolVersion(HttpVersion.HTTP_2)
            .setHttp2ClearTextUpgrade(false);

        return WebClient.builder()
            .clientConnector(new VertxClientHttpConnector(vertx, options))
            .baseUrl(BASE_URL)
            .build();
    }
}
