package me.snowdrop.vertx.http;

import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLSession;

import org.springframework.http.server.reactive.SslInfo;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * Session ID and certificates extraction logic copied from org.springframework.http.server.reactive.DefaultSslInfo.
 */
final class SslInfoImpl implements SslInfo {

    private final SSLSession session;

    SslInfoImpl(SSLSession session) {
        Assert.notNull(session, "SSLSession is required");
        this.session = session;
    }

    @Nullable
    @Override
    public String getSessionId() {
        byte[] bytes = session.getId();
        if (bytes == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            String digit = Integer.toHexString(b);
            if (digit.length() < 2) {
                sb.append('0');
            }
            if (digit.length() > 2) {
                digit = digit.substring(digit.length() - 2);
            }
            sb.append(digit);
        }
        return sb.toString();
    }

    @Nullable
    @Override
    public X509Certificate[] getPeerCertificates() {
        Certificate[] certificates;
        try {
            certificates = session.getPeerCertificates();
        } catch (Throwable ex) {
            return null;
        }

        List<X509Certificate> result = new ArrayList<>(certificates.length);
        for (Certificate certificate : certificates) {
            if (certificate instanceof X509Certificate) {
                result.add((X509Certificate) certificate);
            }
        }
        return (!result.isEmpty() ? result.toArray(new X509Certificate[0]) : null);
    }
}
