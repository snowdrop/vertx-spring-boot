package dev.snowdrop.vertx.http.client;

import java.net.URI;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.client.reactive.ClientHttpResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.springframework.http.HttpMethod.GET;

@RunWith(MockitoJUnitRunner.class)
public class VertxClientHttpConnectorTest {

    @Mock
    private Vertx mockVertx;

    @Mock
    private HttpClient mockHttpClient;

    @Test
    public void shouldNotConnectRelativeUri() {
        VertxClientHttpConnector connector = new VertxClientHttpConnector(mockHttpClient);
        Mono<ClientHttpResponse> result = connector.connect(GET, URI.create("/test"), r -> Mono.empty());

        StepVerifier.create(result)
            .verifyErrorMessage("URI is not absolute: /test");
    }

    @Test
    public void shouldDestroyInternalClient() {
        given(mockVertx.createHttpClient()).willReturn(mockHttpClient);

        VertxClientHttpConnector connector = new VertxClientHttpConnector(mockVertx);
        connector.destroy();

        verify(mockHttpClient).close();
    }

    @Test
    public void shouldNotDestroyProvidedClient() {
        VertxClientHttpConnector connector = new VertxClientHttpConnector(mockHttpClient);
        connector.destroy();

        verifyZeroInteractions(mockHttpClient);
    }
}
