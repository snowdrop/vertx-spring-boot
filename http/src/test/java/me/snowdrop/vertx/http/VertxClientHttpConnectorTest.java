package me.snowdrop.vertx.http;

import java.net.URI;

import io.vertx.core.http.HttpClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.client.reactive.ClientHttpResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.springframework.http.HttpMethod.GET;

@RunWith(MockitoJUnitRunner.class)
public class VertxClientHttpConnectorTest {

    @Mock
    private HttpClient mockHttpClient;

    private VertxClientHttpConnector connector;

    @Before
    public void setUp() {
        connector = new VertxClientHttpConnector(mockHttpClient);
    }

    @Test
    public void shouldNotConnectRelativeUri() {
        Mono<ClientHttpResponse> result = connector.connect(GET, URI.create("/test"), r -> Mono.empty());

        StepVerifier.create(result)
            .verifyErrorMessage("URI is not absolute: /test");
    }
}
