package dev.snowdrop.vertx.http.server.properties;

import java.util.Set;

import io.vertx.core.http.ClientAuth;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.JksOptions;
import io.vertx.core.net.PfxOptions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.web.server.AbstractConfigurableWebServerFactory;
import org.springframework.boot.web.server.Ssl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@RunWith(MockitoJUnitRunner.class)
public class SslCustomizerTest {

    @Mock
    private AbstractConfigurableWebServerFactory mockFactory;

    @Mock
    private HttpServerOptions mockHttpServerOptions;

    @Mock
    private Ssl mockSsl;

    private SslCustomizer customizer;

    @Before
    public void setUp() {
        given(mockFactory.getSsl()).willReturn(mockSsl);

        customizer = new SslCustomizer(mockFactory);
    }

    @Test
    public void shouldIgnoreNullSsl() {
        given(mockFactory.getSsl()).willReturn(null);

        customizer.apply(mockHttpServerOptions);

        verifyNoInteractions(mockHttpServerOptions);
    }

    @Test
    public void shouldEnableSsl() {
        given(mockSsl.isEnabled()).willReturn(true);

        customizer.apply(mockHttpServerOptions);

        verify(mockHttpServerOptions).setSsl(true);
    }

    @Test
    public void shouldHandleNullClientAuth() {
        customizer.apply(mockHttpServerOptions);

        verify(mockHttpServerOptions, times(0)).setClientAuth(any());
    }

    @Test
    public void shouldSetNoneClientAuth() {
        given(mockSsl.getClientAuth()).willReturn(Ssl.ClientAuth.NONE);

        customizer.apply(mockHttpServerOptions);

        verify(mockHttpServerOptions).setClientAuth(ClientAuth.NONE);
    }

    @Test
    public void shouldSetWantClientAuth() {
        given(mockSsl.getClientAuth()).willReturn(Ssl.ClientAuth.WANT);

        customizer.apply(mockHttpServerOptions);

        verify(mockHttpServerOptions).setClientAuth(ClientAuth.REQUEST);
    }

    @Test
    public void shouldSetNeedClientAuth() {
        given(mockSsl.getClientAuth()).willReturn(Ssl.ClientAuth.NEED);

        customizer.apply(mockHttpServerOptions);

        verify(mockHttpServerOptions).setClientAuth(ClientAuth.REQUIRED);
    }

    @Test
    public void shouldHandleNullEnabledProtocols() {
        customizer.apply(mockHttpServerOptions);

        verify(mockHttpServerOptions, times(0)).setEnabledSecureTransportProtocols(any());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldSetEnabledProtocols() {
        given(mockSsl.getEnabledProtocols()).willReturn(new String[]{ "protocol1", "protocol2", "protocol2" });

        customizer.apply(mockHttpServerOptions);

        ArgumentCaptor<Set> captor = ArgumentCaptor.forClass(Set.class);
        verify(mockHttpServerOptions).setEnabledSecureTransportProtocols(captor.capture());
        assertThat(captor.getValue()).containsOnly("protocol1", "protocol2");
    }

    @Test
    public void shouldHandleNullCiphers() {
        customizer.apply(mockHttpServerOptions);

        verify(mockHttpServerOptions, times(0)).addEnabledCipherSuite(anyString());
    }

    @Test
    public void shouldAddCiphers() {
        given(mockSsl.getCiphers()).willReturn(new String[]{ "cipher1", "cipher2" });

        customizer.apply(mockHttpServerOptions);

        verify(mockHttpServerOptions).addEnabledCipherSuite("cipher1");
        verify(mockHttpServerOptions).addEnabledCipherSuite("cipher2");
    }

    @Test
    public void shouldHandleNullKeyStoreType() {
        customizer.apply(mockHttpServerOptions);

        verify(mockHttpServerOptions).setKeyCertOptions(null);
    }

    @Test
    public void shouldSetJksKeyCert() {
        given(mockSsl.getKeyStoreType()).willReturn("JKS");
        given(mockSsl.getKeyStore()).willReturn("/key/store/path");
        given(mockSsl.getKeyStorePassword()).willReturn("pass");

        customizer.apply(mockHttpServerOptions);

        ArgumentCaptor<JksOptions> captor = ArgumentCaptor.forClass(JksOptions.class);
        verify(mockHttpServerOptions).setKeyCertOptions(captor.capture());

        JksOptions jksOptions = captor.getValue();
        assertThat(jksOptions.getPath()).isEqualTo("/key/store/path");
        assertThat(jksOptions.getPassword()).isEqualTo("pass");
    }

    @Test
    public void shouldSetPfxKeyCert() {
        given(mockSsl.getKeyStoreType()).willReturn("PKCS12");
        given(mockSsl.getKeyStore()).willReturn("/key/store/path");
        given(mockSsl.getKeyStorePassword()).willReturn("pass");

        customizer.apply(mockHttpServerOptions);

        ArgumentCaptor<PfxOptions> captor = ArgumentCaptor.forClass(PfxOptions.class);
        verify(mockHttpServerOptions).setKeyCertOptions(captor.capture());

        PfxOptions pfxOptions = captor.getValue();
        assertThat(pfxOptions.getPath()).isEqualTo("/key/store/path");
        assertThat(pfxOptions.getPassword()).isEqualTo("pass");
    }

    @Test
    public void shouldHandleNullTrustStoreType() {
        customizer.apply(mockHttpServerOptions);

        verify(mockHttpServerOptions).setTrustOptions(null);
    }

    @Test
    public void shouldSetJksTrustOptions() {
        given(mockSsl.getTrustStoreType()).willReturn("JKS");
        given(mockSsl.getTrustStore()).willReturn("/trust/store/path");
        given(mockSsl.getTrustStorePassword()).willReturn("pass");

        customizer.apply(mockHttpServerOptions);

        ArgumentCaptor<JksOptions> captor = ArgumentCaptor.forClass(JksOptions.class);
        verify(mockHttpServerOptions).setTrustOptions(captor.capture());

        JksOptions jksOptions = captor.getValue();
        assertThat(jksOptions.getPath()).isEqualTo("/trust/store/path");
        assertThat(jksOptions.getPassword()).isEqualTo("pass");
    }

    @Test
    public void shouldSetPfxTrustOptions() {
        given(mockSsl.getTrustStoreType()).willReturn("PKCS12");
        given(mockSsl.getTrustStore()).willReturn("/trust/store/path");
        given(mockSsl.getTrustStorePassword()).willReturn("pass");

        customizer.apply(mockHttpServerOptions);

        ArgumentCaptor<PfxOptions> captor = ArgumentCaptor.forClass(PfxOptions.class);
        verify(mockHttpServerOptions).setTrustOptions(captor.capture());

        PfxOptions pfxOptions = captor.getValue();
        assertThat(pfxOptions.getPath()).isEqualTo("/trust/store/path");
        assertThat(pfxOptions.getPassword()).isEqualTo("pass");
    }
}
