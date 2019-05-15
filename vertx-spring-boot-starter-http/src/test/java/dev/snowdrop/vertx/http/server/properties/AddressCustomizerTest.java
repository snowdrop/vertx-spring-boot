package dev.snowdrop.vertx.http.server.properties;

import java.net.InetAddress;
import java.net.UnknownHostException;

import io.vertx.core.http.HttpServerOptions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.web.server.AbstractConfigurableWebServerFactory;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AddressCustomizerTest {

    @Mock
    private AbstractConfigurableWebServerFactory mockFactory;

    @Mock
    private HttpServerOptions mockHttpServerOptions;

    @Test
    public void shouldSetValidAddress() throws UnknownHostException {
        given(mockFactory.getAddress()).willReturn(InetAddress.getByName("localhost"));

        AddressCustomizer customizer = new AddressCustomizer(mockFactory);
        customizer.apply(mockHttpServerOptions);

        verify(mockHttpServerOptions).setHost("127.0.0.1");
    }

    @Test
    public void shouldIgnoreInvalidAddress() {
        AddressCustomizer customizer = new AddressCustomizer(mockFactory);
        customizer.apply(mockHttpServerOptions);

        verify(mockHttpServerOptions, times(0)).setHost(anyString());
    }
}
