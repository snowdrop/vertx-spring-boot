package me.snowdrop.vertx.http.properties;

import java.util.Arrays;
import java.util.LinkedHashSet;

import io.vertx.core.http.ClientAuth;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.JksOptions;
import io.vertx.core.net.KeyCertOptions;
import io.vertx.core.net.PfxOptions;
import io.vertx.core.net.TrustOptions;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.web.server.Ssl;

public class SslCustomizer implements HttpServerOptionsCustomizer {

    private final Ssl ssl;

    private final PropertyMapper propertyMapper;

    public SslCustomizer(Ssl ssl) {
        this.ssl = ssl;
        this.propertyMapper = PropertyMapper.get();
    }

    @Override
    public HttpServerOptions apply(HttpServerOptions options) {
        if (ssl == null) {
            return options;
        }

        options.setSsl(ssl.isEnabled());
        options.setKeyCertOptions(keyCertOptionsAdapter(ssl));
        options.setTrustOptions(trustOptionsAdapter(ssl));

        propertyMapper.from(ssl.getClientAuth())
            .whenNonNull()
            .as(this::clientAuthAdapter)
            .to(options::setClientAuth);

        propertyMapper.from(ssl.getEnabledProtocols())
            .whenNonNull()
            .as(Arrays::asList)
            .as(LinkedHashSet::new)
            .to(options::setEnabledSecureTransportProtocols);

        propertyMapper.from(ssl.getCiphers())
            .whenNonNull()
            .as(Arrays::stream)
            .to(stream -> stream.forEach(options::addEnabledCipherSuite));

        return options;
    }

    private ClientAuth clientAuthAdapter(Ssl.ClientAuth clientAuth) {
        switch (clientAuth) {
            case WANT:
                return ClientAuth.REQUEST;
            case NEED:
                return ClientAuth.REQUIRED;
            default:
                return ClientAuth.NONE;
        }
    }

    private KeyCertOptions keyCertOptionsAdapter(Ssl ssl) {
        if ("JKS".equalsIgnoreCase(ssl.getKeyStoreType())) {
            return getJksOptions(ssl.getKeyStore(), ssl.getKeyStorePassword());
        } else if ("PKCS12".equalsIgnoreCase(ssl.getKeyStoreType())) {
            return getPfxOptions(ssl.getKeyStore(), ssl.getKeyStorePassword());
        }

        return null;
    }

    private TrustOptions trustOptionsAdapter(Ssl ssl) {
        if ("JKS".equalsIgnoreCase(ssl.getTrustStoreType())) {
            return getJksOptions(ssl.getTrustStore(), ssl.getTrustStorePassword());
        } else if ("PKCS12".equalsIgnoreCase(ssl.getTrustStoreType())) {
            return getPfxOptions(ssl.getTrustStore(), ssl.getTrustStorePassword());
        }

        return null;
    }

    private JksOptions getJksOptions(String path, String password) {
        JksOptions options = new JksOptions();
        options.setPath(path);
        options.setPassword(password);
        return options;
    }

    private PfxOptions getPfxOptions(String path, String password) {
        PfxOptions options = new PfxOptions();
        options.setPath(path);
        options.setPassword(password);
        return options;
    }
}
