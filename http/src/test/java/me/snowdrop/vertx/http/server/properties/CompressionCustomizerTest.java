package me.snowdrop.vertx.http.server.properties;

import io.vertx.core.http.HttpServerOptions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.web.server.Compression;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CompressionCustomizerTest {

    @Mock
    private HttpServerOptions mockHttpServerOptions;

    @Mock
    private Compression mockCompression;

    @Test
    public void shouldEnableCompression() {
        given(mockCompression.getEnabled()).willReturn(true);
        CompressionCustomizer customizer = new CompressionCustomizer(mockCompression);

        customizer.apply(mockHttpServerOptions);

        verify(mockHttpServerOptions).setCompressionSupported(true);
    }

    @Test
    public void shouldIgnoreNullCompression() {
        CompressionCustomizer customizer = new CompressionCustomizer(null);

        customizer.apply(mockHttpServerOptions);

        verify(mockHttpServerOptions, times(0)).setCompressionSupported(anyBoolean());
    }
}
