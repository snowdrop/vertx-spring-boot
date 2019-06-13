package dev.snowdrop.vertx.amqp;

import io.vertx.amqp.AmqpClientOptions;
import io.vertx.core.net.JdkSSLEngineOptions;
import io.vertx.core.net.JksOptions;
import io.vertx.core.net.OpenSSLEngineOptions;
import io.vertx.core.net.PfxOptions;
import io.vertx.core.net.ProxyOptions;
import io.vertx.core.net.ProxyType;

class AmqpPropertiesConverter {

    AmqpClientOptions toAmqpClientOptions(AmqpProperties from) {
        AmqpClientOptions to = new AmqpClientOptions();

        mapAmqpClientOptions(from, to);
        mapProtonClientOptions(from, to);
        mapNetClientOptions(from, to);
        mapClientOptionsBase(from, to);
        mapTcpSslOptions(from, to);
        mapNetworkOptions(from, to);

        return to;
    }

    private void mapAmqpClientOptions(AmqpProperties from, AmqpClientOptions to) {
        to.setHost(from.getHost());
        to.setPort(from.getPort());
        to.setUsername(from.getUsername());
        to.setPassword(from.getPassword());
        to.setContainerId(from.getContainerId());
    }

    private void mapProtonClientOptions(AmqpProperties from, AmqpClientOptions to) {
        from.getEnabledSaslMechanisms().forEach(to::addEnabledSaslMechanism);
        to.setHeartbeat(from.getHeartbeat());
        to.setMaxFrameSize(from.getMaxFrameSize());
        to.setVirtualHost(from.getVirtualHost());
        to.setSniServerName(from.getSniServerName());
    }

    private void mapNetClientOptions(AmqpProperties from, AmqpClientOptions to) {
        to.setReconnectAttempts(from.getReconnectAttempts());
        to.setReconnectInterval(from.getReconnectInterval());
        to.setHostnameVerificationAlgorithm(from.getHostnameVerificationAlgorithm());
    }

    private void mapClientOptionsBase(AmqpProperties from, AmqpClientOptions to) {
        to.setConnectTimeout(from.getConnectTimeout());
        to.setTrustAll(from.isTrustAll());
        to.setMetricsName(from.getMetricsName());
        to.setLocalAddress(from.getLocalAddress());

        if (from.getProxy().isEnabled()) {
            ProxyOptions proxyOptions = new ProxyOptions()
                .setHost(from.getProxy().getHost())
                .setPort(from.getProxy().getPort())
                .setUsername(from.getProxy().getUsername())
                .setPassword(from.getProxy().getPassword())
                .setType(ProxyType.valueOf(from.getProxy().getType().name()));

            to.setProxyOptions(proxyOptions);
        }
    }

    private void mapTcpSslOptions(AmqpProperties from, AmqpClientOptions to) {
        to.setTcpNoDelay(from.isTcpNoDelay());
        to.setTcpKeepAlive(from.isTcpKeepAlive());
        to.setSoLinger(from.getSoLinger());
        to.setUsePooledBuffers(from.isUsePooledBuffers());
        to.setIdleTimeout(from.getIdleTimeout());
        to.setIdleTimeoutUnit(from.getIdleTimeoutUnit());
        to.setSsl(from.isSsl());
        to.setSslHandshakeTimeout(from.getSslHandshakeTimeout());
        to.setSslHandshakeTimeoutUnit(from.getSslHandshakeTimeoutUnit());
        from.getEnabledCipherSuites().forEach(to::addEnabledCipherSuite);
        to.setEnabledSecureTransportProtocols(from.getEnabledSecureTransportProtocols());
        to.setTcpFastOpen(from.isTcpFastOpen());
        to.setTcpCork(from.isTcpCork());
        to.setTcpQuickAck(from.isTcpQuickAck());

        if (from.getJksKeyStore().isEnabled()) {
            JksOptions options = new JksOptions()
                .setPath(from.getJksKeyStore().getPath())
                .setPassword(from.getJksKeyStore().getPassword());

            to.setKeyCertOptions(options);
        } else if (from.getPfxKeyStore().isEnabled()) {
            PfxOptions options = new PfxOptions()
                .setPath(from.getPfxKeyStore().getPath())
                .setPassword(from.getPfxKeyStore().getPassword());

            to.setKeyCertOptions(options);
        }

        if (from.getJksTrustStore().isEnabled()) {
            JksOptions options = new JksOptions()
                .setPath(from.getJksTrustStore().getPath())
                .setPassword(from.getJksTrustStore().getPassword());

            to.setTrustOptions(options);
        } else if (from.getPfxKeyStore().isEnabled()) {
            PfxOptions options = new PfxOptions()
                .setPath(from.getPfxTrustStore().getPath())
                .setPassword(from.getPfxTrustStore().getPassword());

            to.setTrustOptions(options);
        }

        if (from.getJdkSslEngine().isEnabled()) {
            to.setJdkSslEngineOptions(new JdkSSLEngineOptions());
        } else if (from.getOpenSslEngine().isEnabled()) {
            OpenSSLEngineOptions options = new OpenSSLEngineOptions()
                .setSessionCacheEnabled(from.getOpenSslEngine().isSessionCacheEnabled());

            to.setOpenSslEngineOptions(options);
        }
    }

    private void mapNetworkOptions(AmqpProperties from, AmqpClientOptions to) {
        to.setSendBufferSize(from.getSendBufferSize());
        to.setReceiveBufferSize(from.getReceiveBufferSize());
        to.setTrafficClass(from.getTrafficClass());
        to.setReuseAddress(from.isReuseAddress());
        to.setLogActivity(from.isLogActivity());
        to.setReusePort(from.isReusePort());
    }
}
