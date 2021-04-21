package dev.snowdrop.vertx.http.client;

import java.net.URI;

import io.vertx.core.Vertx;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.client.reactive.ClientHttpResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.springframework.http.HttpMethod.GET;

@ExtendWith(MockitoExtension.class)
public class VertxClientHttpConnectorTest {

    @Mock
    private Vertx mockVertx;

    @Test
    public void shouldNotConnectRelativeUri() {
        VertxClientHttpConnector connector = new VertxClientHttpConnector(mockVertx);
        Mono<ClientHttpResponse> result = connector.connect(GET, URI.create("/test"), r -> Mono.empty());

        StepVerifier.create(result)
            .verifyErrorMessage("URI is not absolute: /test");
    }
}
