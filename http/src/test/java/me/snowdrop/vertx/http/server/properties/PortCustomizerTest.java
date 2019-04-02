package me.snowdrop.vertx.http.server.properties;

import io.vertx.core.http.HttpServerOptions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PortCustomizerTest {

    @Mock
    private HttpServerOptions mockHttpServerOptions;

    @Test
    public void shouldSetValidPort() {
        PortCustomizer customizer = new PortCustomizer(1);
        customizer.apply(mockHttpServerOptions);

        verify(mockHttpServerOptions).setPort(1);
    }

    @Test
    public void shouldIgnoreInvalidPort() {
        PortCustomizer customizer = new PortCustomizer(0);
        customizer.apply(mockHttpServerOptions);

        verify(mockHttpServerOptions, times(0)).setPort(anyInt());
    }

}
