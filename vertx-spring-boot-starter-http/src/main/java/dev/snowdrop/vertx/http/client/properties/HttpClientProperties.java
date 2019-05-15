package dev.snowdrop.vertx.http.client.properties;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpVersion;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Spring Boot properties integration for HttpClientOptions. All expect the following properties are integrated:
 * crlPaths, crlValues, keyCertOptions, trustOptions, sslEngineOptions, proxyOptions.
 * <p>
 * If any of the ignored properties is required, please use {@link HttpClientOptionsCustomizer}.
 */
@ConfigurationProperties(prefix = HttpClientProperties.PROPERTIES_PREFIX)
public class HttpClientProperties {

    static final String PROPERTIES_PREFIX = "vertx.http.client";

    private final HttpClientOptions delegate = new HttpClientOptions();

    public HttpClientOptions getHttpClientOptions() {
        return new HttpClientOptions(delegate);
    }

    // Vert.x NetworkOptions

    public int getSendBufferSize() {
        return delegate.getSendBufferSize();
    }

    public void setSendBufferSize(int sendBufferSize) {
        delegate.setSendBufferSize(sendBufferSize);
    }

    public int getReceiveBufferSize() {
        return delegate.getReceiveBufferSize();
    }

    public void setReceiveBufferSize(int receiveBufferSize) {
        delegate.setReceiveBufferSize(receiveBufferSize);
    }

    public boolean isReuseAddress() {
        return delegate.isReuseAddress();
    }

    public void setReuseAddress(boolean reuseAddress) {
        delegate.setReuseAddress(reuseAddress);
    }

    public boolean isReusePort() {
        return delegate.isReusePort();
    }

    public void setReusePort(boolean reusePort) {
        delegate.setReusePort(reusePort);
    }

    public int getTrafficClass() {
        return delegate.getTrafficClass();
    }

    public void setTrafficClass(int trafficClass) {
        delegate.setTrafficClass(trafficClass);
    }

    public boolean getLogActivity() {
        return delegate.getLogActivity();
    }

    public void setLogActivity(boolean logEnabled) {
        delegate.setLogActivity(logEnabled);
    }

    // Vert.x TCPSSLOptions

    public boolean isTcpNoDelay() {
        return delegate.isTcpNoDelay();
    }

    public void setTcpNoDelay(boolean tcpNoDelay) {
        delegate.setTcpNoDelay(tcpNoDelay);
    }

    public boolean isTcpKeepAlive() {
        return delegate.isTcpKeepAlive();
    }

    public void setTcpKeepAlive(boolean tcpKeepAlive) {
        delegate.setTcpKeepAlive(tcpKeepAlive);
    }

    public int getSoLinger() {
        return delegate.getSoLinger();
    }

    public void setSoLinger(int soLinger) {
        delegate.setSoLinger(soLinger);
    }

    public int getIdleTimeout() {
        return delegate.getIdleTimeout();
    }

    public void setIdleTimeout(int idleTimeout) {
        delegate.setIdleTimeout(idleTimeout);
    }

    public TimeUnit getIdleTimeoutUnit() {
        return delegate.getIdleTimeoutUnit();
    }

    public void setIdleTimeoutUnit(TimeUnit idleTimeoutUnit) {
        delegate.setIdleTimeoutUnit(idleTimeoutUnit);
    }

    public boolean isSsl() {
        return delegate.isSsl();
    }

    public void setSsl(boolean ssl) {
        delegate.setSsl(ssl);
    }

    public Set<String> getEnabledCipherSuites() {
        return delegate.getEnabledCipherSuites();
    }

    public void setEnabledCipherSuites(Set<String> enabledCipherSuites) {
        if (enabledCipherSuites != null) {
            enabledCipherSuites.forEach(delegate::addEnabledCipherSuite);
        }
    }

    public boolean isUseAlpn() {
        return delegate.isUseAlpn();
    }

    public void setUseAlpn(boolean useAlpn) {
        delegate.setUseAlpn(useAlpn);
    }

    public Set<String> getEnabledSecureTransportProtocols() {
        return delegate.getEnabledSecureTransportProtocols();
    }

    public void setEnabledSecureTransportProtocols(Set<String> enabledSecureTransportProtocols) {
        delegate.setEnabledSecureTransportProtocols(enabledSecureTransportProtocols);
    }

    public boolean isTcpFastOpen() {
        return delegate.isTcpFastOpen();
    }

    public void setTcpFastOpen(boolean tcpFastOpen) {
        delegate.setTcpFastOpen(tcpFastOpen);
    }

    public boolean isTcpCork() {
        return delegate.isTcpCork();
    }

    public void setTcpCork(boolean tcpCork) {
        delegate.setTcpCork(tcpCork);
    }

    public boolean isTcpQuickAck() {
        return delegate.isTcpQuickAck();
    }

    public void setTcpQuickAck(boolean tcpQuickAck) {
        delegate.setTcpQuickAck(tcpQuickAck);
    }

    // Vert.x ClientOptionsBase

    public int getConnectTimeout() {
        return delegate.getConnectTimeout();
    }

    public void setConnectTimeout(int connectTimeout) {
        delegate.setConnectTimeout(connectTimeout);
    }

    public boolean isTrustAll() {
        return delegate.isTrustAll();
    }

    public void setTrustAll(boolean trustAll) {
        delegate.setTrustAll(trustAll);
    }

    public String getMetricsName() {
        return delegate.getMetricsName();
    }

    public void setMetricsName(String metricsName) {
        delegate.setMetricsName(metricsName);
    }

    public String getLocalAddress() {
        return delegate.getLocalAddress();
    }

    public void setLocalAddress(String localAddress) {
        delegate.setLocalAddress(localAddress);
    }

    // Vert.x HttpClientOptions

    public boolean isVerifyHost() {
        return delegate.isVerifyHost();
    }

    public void setVerifyHost(boolean verifyHost) {
        delegate.setVerifyHost(verifyHost);
    }

    public int getMaxPoolSize() {
        return delegate.getMaxPoolSize();
    }

    public void setMaxPoolSize(int maxPoolSize) {
        delegate.setMaxPoolSize(maxPoolSize);
    }

    public boolean isKeepAlive() {
        return delegate.isKeepAlive();
    }

    public void setKeepAlive(boolean keepAlive) {
        delegate.setKeepAlive(keepAlive);
    }

    public int getKeepAliveTimeout() {
        return delegate.getKeepAliveTimeout();
    }

    public void setKeepAliveTimeout(int keepAliveTimeout) {
        delegate.setKeepAliveTimeout(keepAliveTimeout);
    }

    public int getPipeliningLimit() {
        return delegate.getPipeliningLimit();
    }

    public void setPipeliningLimit(int limit) {
        delegate.setPipeliningLimit(limit);
    }

    public boolean isPipelining() {
        return delegate.isPipelining();
    }

    public void setPipelining(boolean pipelining) {
        delegate.setPipelining(pipelining);
    }

    public int getHttp2MaxPoolSize() {
        return delegate.getHttp2MaxPoolSize();
    }

    public void setHttp2MaxPoolSize(int max) {
        delegate.setHttp2MaxPoolSize(max);
    }

    public int getHttp2MultiplexingLimit() {
        return delegate.getHttp2MultiplexingLimit();
    }

    public void setHttp2MultiplexingLimit(int limit) {
        delegate.setHttp2MultiplexingLimit(limit);
    }

    public int getHttp2ConnectionWindowSize() {
        return delegate.getHttp2ConnectionWindowSize();
    }

    public void setHttp2ConnectionWindowSize(int http2ConnectionWindowSize) {
        delegate.setHttp2ConnectionWindowSize(http2ConnectionWindowSize);
    }

    public int getHttp2KeepAliveTimeout() {
        return delegate.getHttp2KeepAliveTimeout();
    }

    public void setHttp2KeepAliveTimeout(int keepAliveTimeout) {
        delegate.setHttp2KeepAliveTimeout(keepAliveTimeout);
    }

    public int getPoolCleanerPeriod() {
        return delegate.getPoolCleanerPeriod();
    }

    public void setPoolCleanerPeriod(int poolCleanerPeriod) {
        delegate.setPoolCleanerPeriod(poolCleanerPeriod);
    }

    public boolean isTryUseCompression() {
        return delegate.isTryUseCompression();
    }

    public void setTryUseCompression(boolean tryUseCompression) {
        delegate.setTryUseCompression(tryUseCompression);
    }

    public int getMaxWebsocketFrameSize() {
        return delegate.getMaxWebsocketFrameSize();
    }

    public void setMaxWebsocketFrameSize(int maxWebsocketFrameSize) {
        delegate.setMaxWebsocketFrameSize(maxWebsocketFrameSize);
    }

    public int getMaxWebsocketMessageSize() {
        return delegate.getMaxWebsocketMessageSize();
    }

    public void setMaxWebsocketMessageSize(int maxWebsocketMessageSize) {
        delegate.setMaxWebsocketMessageSize(maxWebsocketMessageSize);
    }

    public String getDefaultHost() {
        return delegate.getDefaultHost();
    }

    public void setDefaultHost(String defaultHost) {
        delegate.setDefaultHost(defaultHost);
    }

    public int getDefaultPort() {
        return delegate.getDefaultPort();
    }

    public void setDefaultPort(int defaultPort) {
        delegate.setDefaultPort(defaultPort);
    }

    public HttpVersion getProtocolVersion() {
        return delegate.getProtocolVersion();
    }

    public void setProtocolVersion(HttpVersion protocolVersion) {
        delegate.setProtocolVersion(protocolVersion);
    }

    public int getMaxChunkSize() {
        return delegate.getMaxChunkSize();
    }

    public void setMaxChunkSize(int maxChunkSize) {
        delegate.setMaxChunkSize(maxChunkSize);
    }

    public int getMaxInitialLineLength() {
        return delegate.getMaxInitialLineLength();
    }

    public void setMaxInitialLineLength(int maxInitialLineLength) {
        delegate.setMaxInitialLineLength(maxInitialLineLength);
    }

    public int getMaxHeaderSize() {
        return delegate.getMaxHeaderSize();
    }

    public void setMaxHeaderSize(int maxHeaderSize) {
        delegate.setMaxHeaderSize(maxHeaderSize);
    }

    public int getMaxWaitQueueSize() {
        return delegate.getMaxWaitQueueSize();
    }

    public void setMaxWaitQueueSize(int maxWaitQueueSize) {
        delegate.setMaxWaitQueueSize(maxWaitQueueSize);
    }

    public long getHeaderTableSize() {
        return delegate.getInitialSettings().getHeaderTableSize();
    }

    public void setHeaderTableSize(long headerTableSize) {
        delegate.getInitialSettings().setHeaderTableSize(headerTableSize);
    }

    public boolean isPushEnabled() {
        return delegate.getInitialSettings().isPushEnabled();
    }

    public void setPushEnabled(boolean pushEnabled) {
        delegate.getInitialSettings().setPushEnabled(pushEnabled);
    }

    public long getMaxConcurrentStreams() {
        return delegate.getInitialSettings().getMaxConcurrentStreams();
    }

    public void setMaxConcurrentStreams(long maxConcurrentStreams) {
        delegate.getInitialSettings().setMaxConcurrentStreams(maxConcurrentStreams);
    }

    public int getInitialWindowSize() {
        return delegate.getInitialSettings().getInitialWindowSize();
    }

    public void setInitialWindowSize(int initialWindowSize) {
        delegate.getInitialSettings().setInitialWindowSize(initialWindowSize);
    }

    public int getMaxFrameSize() {
        return delegate.getInitialSettings().getMaxFrameSize();
    }

    public void setMaxFrameSize(int maxFrameSize) {
        delegate.getInitialSettings().setMaxFrameSize(maxFrameSize);
    }

    public long getMaxHeaderListSize() {
        return delegate.getInitialSettings().getMaxHeaderListSize();
    }

    public void setMaxHeaderListSize(long maxHeaderListSize) {
        delegate.getInitialSettings().setMaxHeaderListSize(maxHeaderListSize);
    }

    public Map<Integer, Long> getHttp2ExtraSettings() {
        return delegate.getInitialSettings().getExtraSettings();
    }

    public void setHttp2ExtraSettings(Map<Integer, Long> http2ExtraSettings) {
        delegate.getInitialSettings().setExtraSettings(http2ExtraSettings);
    }

    public List<HttpVersion> getAlpnVersions() {
        return delegate.getAlpnVersions();
    }

    public void setAlpnVersions(List<HttpVersion> alpnVersions) {
        delegate.setAlpnVersions(alpnVersions);
    }

    public boolean isHttp2ClearTextUpgrade() {
        return delegate.isHttp2ClearTextUpgrade();
    }

    public void setHttp2ClearTextUpgrade(boolean value) {
        delegate.setHttp2ClearTextUpgrade(value);
    }

    public boolean isSendUnmaskedFrames() {
        return delegate.isSendUnmaskedFrames();
    }

    public void setSendUnmaskedFrames(boolean sendUnmaskedFrames) {
        delegate.setSendUnmaskedFrames(sendUnmaskedFrames);
    }

    public int getMaxRedirects() {
        return delegate.getMaxRedirects();
    }

    public void setMaxRedirects(int maxRedirects) {
        delegate.setMaxRedirects(maxRedirects);
    }

    public boolean isForceSni() {
        return delegate.isForceSni();
    }

    public void setForceSni(boolean forceSni) {
        delegate.setForceSni(forceSni);
    }

    public int getDecoderInitialBufferSize() {
        return delegate.getDecoderInitialBufferSize();
    }

    public void setDecoderInitialBufferSize(int decoderInitialBufferSize) {
        delegate.setDecoderInitialBufferSize(decoderInitialBufferSize);
    }

    public boolean isTryWebsocketDeflateFrameCompression() {
        return delegate.getTryWebsocketDeflateFrameCompression();
    }

    public void setTryUsePerFrameWebsocketCompression(boolean tryWebsocketDeflateFrameCompression) {
        delegate.setTryUsePerFrameWebsocketCompression(tryWebsocketDeflateFrameCompression);
    }

    public boolean isTryUsePerMessageWebsocketCompression() {
        return delegate.getTryUsePerMessageWebsocketCompression();
    }

    public void setTryUsePerMessageWebsocketCompression(boolean tryUsePerMessageWebsocketCompression) {
        delegate.setTryUsePerMessageWebsocketCompression(tryUsePerMessageWebsocketCompression);
    }

    public int getWebsocketCompressionLevel() {
        return delegate.getWebsocketCompressionLevel();
    }

    public void setWebsocketCompressionLevel(int websocketCompressionLevel) {
        delegate.setWebsocketCompressionLevel(websocketCompressionLevel);
    }

    public boolean isWebsocketCompressionAllowClientNoContext() {
        return delegate.getWebsocketCompressionAllowClientNoContext();
    }

    public void setWebsocketCompressionAllowClientNoContext(boolean websocketCompressionAllowClientNoContext) {
        delegate.setWebsocketCompressionAllowClientNoContext(websocketCompressionAllowClientNoContext);
    }

    public boolean isWebsocketCompressionRequestServerNoContext() {
        return delegate.getWebsocketCompressionRequestServerNoContext();
    }

    public void setWebsocketCompressionRequestServerNoContext(boolean websocketCompressionRequestServerNoContext) {
        delegate.setWebsocketCompressionRequestServerNoContext(websocketCompressionRequestServerNoContext);
    }
}
