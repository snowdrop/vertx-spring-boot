package dev.snowdrop.vertx.http.server;

import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class SslInfoImplTest {

    @Mock
    private SSLSession mockSession;

    @Mock
    private Certificate mockCertificate;

    @Mock
    private X509Certificate mockX509Certificate;

    private SslInfoImpl sslInfo;

    @Before
    public void setUp() {
        sslInfo = new SslInfoImpl(mockSession);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowNullSession() {
        new SslInfoImpl(null);
    }

    @Test
    public void shouldIgnoreNullSessionId() {
        assertThat(sslInfo.getSessionId()).isNull();
    }

    @Test
    public void shouldGetSessionId() {
        given(mockSession.getId()).willReturn(new byte[]{ -1, 0, 1 });

        // -1 (#ffffffff) -> ff, 0 -> 00, 1 -> 01
        assertThat(sslInfo.getSessionId()).isEqualTo("ff0001");
    }

    @Test
    public void shouldHandleSSLPeerUnverifiedException() throws SSLPeerUnverifiedException {
        given(mockSession.getPeerCertificates()).willThrow(SSLPeerUnverifiedException.class);

        assertThat(sslInfo.getPeerCertificates()).isNull();
    }

    @Test
    public void shouldGetX509Certificates() throws SSLPeerUnverifiedException {
        given(mockSession.getPeerCertificates()).willReturn(new Certificate[]{ mockCertificate, mockX509Certificate });

        assertThat(sslInfo.getPeerCertificates()).containsOnly(mockX509Certificate);
    }

    @Test
    public void shouldNotGetX509Certificates() throws SSLPeerUnverifiedException {
        given(mockSession.getPeerCertificates()).willReturn(new Certificate[]{ mockCertificate });

        assertThat(sslInfo.getPeerCertificates()).isNull();
    }

}
