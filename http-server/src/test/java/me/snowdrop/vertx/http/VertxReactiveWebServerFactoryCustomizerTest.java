package me.snowdrop.vertx.http;

import me.snowdrop.vertx.http.properties.AddressCustomizer;
import me.snowdrop.vertx.http.properties.CompressionCustomizer;
import me.snowdrop.vertx.http.properties.Http2Customizer;
import me.snowdrop.vertx.http.properties.PortCustomizer;
import me.snowdrop.vertx.http.properties.SslCustomizer;
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

    private VertxReactiveWebServerFactoryCustomizer customizer;

    @Before
    public void setUp() {
        customizer = new VertxReactiveWebServerFactoryCustomizer();
    }

    @Test
    public void shouldCustomizeWebServerFactory() {
        customizer.customize(mockVertxReactiveWebServerFactory);

        verify(mockVertxReactiveWebServerFactory).getPort();
        verify(mockVertxReactiveWebServerFactory).getAddress();
        verify(mockVertxReactiveWebServerFactory).getSsl();
        verify(mockVertxReactiveWebServerFactory).getHttp2();
        verify(mockVertxReactiveWebServerFactory).getCompression();

        verify(mockVertxReactiveWebServerFactory).registerHttpServerOptionsCustomizer(any(PortCustomizer.class));
        verify(mockVertxReactiveWebServerFactory).registerHttpServerOptionsCustomizer(any(AddressCustomizer.class));
        verify(mockVertxReactiveWebServerFactory).registerHttpServerOptionsCustomizer(any(SslCustomizer.class));
        verify(mockVertxReactiveWebServerFactory).registerHttpServerOptionsCustomizer(any(Http2Customizer.class));
        verify(mockVertxReactiveWebServerFactory).registerHttpServerOptionsCustomizer(any(CompressionCustomizer.class));
    }

    @Test
    public void shouldHaveCorrectPriority() {
        assertThat(customizer.getOrder()).isEqualTo(1);
    }

}
