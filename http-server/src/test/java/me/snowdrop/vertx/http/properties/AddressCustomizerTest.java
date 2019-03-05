package me.snowdrop.vertx.http.properties;

import java.net.InetAddress;
import java.net.UnknownHostException;

import io.vertx.core.http.HttpServerOptions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AddressCustomizerTest {

    @Mock
    private HttpServerOptions mockHttpServerOptions;

    @Test
    public void shouldSetValidAddress() throws UnknownHostException {
        AddressCustomizer customizer = new AddressCustomizer(InetAddress.getByName("localhost"));
        customizer.apply(mockHttpServerOptions);

        verify(mockHttpServerOptions).setHost("127.0.0.1");
    }

    @Test
    public void shouldIgnoreInvalidAddress() {
        AddressCustomizer customizer = new AddressCustomizer(null);
        customizer.apply(mockHttpServerOptions);

        verify(mockHttpServerOptions, times(0)).setHost(anyString());
    }
}
