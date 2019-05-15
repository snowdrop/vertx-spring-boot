package dev.snowdrop.vertx.http.server;

import java.util.Collections;

import dev.snowdrop.vertx.http.server.properties.AddressCustomizer;
import dev.snowdrop.vertx.http.server.properties.CompressionCustomizer;
import dev.snowdrop.vertx.http.server.properties.HttpServerOptionsCustomizer;
import dev.snowdrop.vertx.http.server.properties.PortCustomizer;
import dev.snowdrop.vertx.http.server.properties.SslCustomizer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class VertxReactiveWebServerFactoryCustomizerTest {

    @Mock
    private VertxReactiveWebServerFactory mockVertxReactiveWebServerFactory;

    @Mock
    private HttpServerOptionsCustomizer mockHttpServerOptionsCustomizer;

    private VertxReactiveWebServerFactoryCustomizer customizer;

    @Before
    public void setUp() {
        customizer =
            new VertxReactiveWebServerFactoryCustomizer(Collections.singleton(mockHttpServerOptionsCustomizer));
    }

    @Test
    public void shouldCustomizeWebServerFactory() {
        customizer.customize(mockVertxReactiveWebServerFactory);

        verify(mockVertxReactiveWebServerFactory).registerHttpServerOptionsCustomizer(any(PortCustomizer.class));
        verify(mockVertxReactiveWebServerFactory).registerHttpServerOptionsCustomizer(any(AddressCustomizer.class));
        verify(mockVertxReactiveWebServerFactory).registerHttpServerOptionsCustomizer(any(SslCustomizer.class));
        verify(mockVertxReactiveWebServerFactory).registerHttpServerOptionsCustomizer(any(CompressionCustomizer.class));
        verify(mockVertxReactiveWebServerFactory).registerHttpServerOptionsCustomizer(mockHttpServerOptionsCustomizer);
    }

    @Test
    public void shouldHaveCorrectPriority() {
        assertThat(customizer.getOrder()).isEqualTo(1);
    }

}
