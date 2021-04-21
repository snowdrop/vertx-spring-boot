package dev.snowdrop.vertx.http.server.properties;

import io.vertx.core.http.HttpServerOptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.server.AbstractConfigurableWebServerFactory;
import org.springframework.boot.web.server.Compression;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CompressionCustomizerTest {

    @Mock
    private AbstractConfigurableWebServerFactory mockFactory;

    @Mock
    private HttpServerOptions mockHttpServerOptions;

    @Mock
    private Compression mockCompression;

    @Test
    public void shouldEnableCompression() {
        given(mockFactory.getCompression()).willReturn(mockCompression);
        given(mockCompression.getEnabled()).willReturn(true);

        CompressionCustomizer customizer = new CompressionCustomizer(mockFactory);

        customizer.apply(mockHttpServerOptions);

        verify(mockHttpServerOptions).setCompressionSupported(true);
    }

    @Test
    public void shouldIgnoreNullCompression() {
        CompressionCustomizer customizer = new CompressionCustomizer(mockFactory);

        customizer.apply(mockHttpServerOptions);

        verify(mockHttpServerOptions, times(0)).setCompressionSupported(anyBoolean());
    }
}
