package me.snowdrop.vertx.http.server.properties;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.vertx.core.http.ClientAuth;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpVersion;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Spring Boot properties integration for HttpServerOptions. All expect the following properties are integrated:
 * usePooledBuffers (deprecated), crlPaths, crlValues, keyCertOptions, trustOptions, sslEngineOptions.
 * <p>
 * If sslEngineOptions, crlPath or crlValues are required, please use HttpServerOptionsCustomizer.
 * If keyCertOptions or trustOptions either use {@link HttpServerOptionsCustomizer} or {@link ServerProperties}.
 */
@ConfigurationProperties(prefix = HttpServerProperties.PROPERTIES_PREFIX)
public class HttpServerProperties {

    static final String PROPERTIES_PREFIX = "vertx.http.server";

    private final HttpServerOptions delegate = new HttpServerOptions();

    public HttpServerOptions getHttpServerOptions() {
        return new HttpServerOptions(delegate);
    }

    // Vert.x NetServerOptions

    public int getPort() {
        return delegate.getPort();
    }

    public void setPort(int port) {
        delegate.setPort(port);
    }

    public String getHost() {
        return delegate.getHost();
    }

    public void setHost(String host) {
        delegate.setHost(host);
    }

    public int getAcceptBacklog() {
        return delegate.getAcceptBacklog();
    }

    public void setAcceptBacklog(int acceptBacklog) {
        delegate.setAcceptBacklog(acceptBacklog);
    }

    public ClientAuth getClientAuth() {
        return delegate.getClientAuth();
    }

    public void setClientAuth(ClientAuth clientAuth) {
        delegate.setClientAuth(clientAuth);
    }

    public boolean isSni() {
        return delegate.isSni();
    }

    public void setSni(boolean sni) {
        delegate.setSni(sni);
    }

    // Vert.x HttpServerOptions

    public boolean isCompressionSupported() {
        return delegate.isCompressionSupported();
    }

    public void setCompressionSupported(boolean compressionSupported) {
        delegate.setCompressionSupported(compressionSupported);
    }

    public int getCompressionLevel() {
        return delegate.getCompressionLevel();
    }

    public void setCompressionLevel(int compressionLevel) {
        delegate.setCompressionLevel(compressionLevel);
    }

    public boolean isAcceptUnmaskedFrames() {
        return delegate.isAcceptUnmaskedFrames();
    }

    public void setAcceptUnmaskedFrames(boolean acceptUnmaskedFrames) {
        delegate.setAcceptUnmaskedFrames(acceptUnmaskedFrames);
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

    public String getWebsocketSubProtocols() {
        return delegate.getWebsocketSubProtocols();
    }

    public void setWebsocketSubProtocols(String subProtocols) {
        delegate.setWebsocketSubProtocols(subProtocols);
    }

    public boolean isHandle100ContinueAutomatically() {
        return delegate.isHandle100ContinueAutomatically();
    }

    public void setHandle100ContinueAutomatically(boolean handle100ContinueAutomatically) {
        delegate.setHandle100ContinueAutomatically(handle100ContinueAutomatically);
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

    public List<HttpVersion> getAlpnVersions() {
        return delegate.getAlpnVersions();
    }

    public void setAlpnVersions(List<HttpVersion> alpnVersions) {
        delegate.setAlpnVersions(alpnVersions);
    }

    public int getHttp2ConnectionWindowSize() {
        return delegate.getHttp2ConnectionWindowSize();
    }

    public void setHttp2ConnectionWindowSize(int http2ConnectionWindowSize) {
        delegate.setHttp2ConnectionWindowSize(http2ConnectionWindowSize);
    }

    public boolean isDecompressionSupported() {
        return delegate.isDecompressionSupported();
    }

    public void setDecompressionSupported(boolean decompressionSupported) {
        delegate.setDecompressionSupported(decompressionSupported);
    }

    public int getDecoderInitialBufferSize() {
        return delegate.getDecoderInitialBufferSize();
    }

    public void setDecoderInitialBufferSize(int decoderInitialBufferSize) {
        delegate.setDecoderInitialBufferSize(decoderInitialBufferSize);
    }

    public boolean isPerFrameWebsocketCompressionSupported() {
        return delegate.getPerFrameWebsocketCompressionSupported();
    }

    public void setPerFrameWebsocketCompressionSupported(boolean perFrameWebsocketCompressionSupported) {
        delegate.setPerFrameWebsocketCompressionSupported(perFrameWebsocketCompressionSupported);
    }

    public boolean isPerMessageWebsocketCompressionSupported() {
        return delegate.getPerMessageWebsocketCompressionSupported();
    }

    public void setPerMessageWebsocketCompressionSupported(boolean perMessageWebsocketCompressionSupported) {
        delegate.setPerMessageWebsocketCompressionSupported(perMessageWebsocketCompressionSupported);
    }

    public int getWebsocketCompressionLevel() {
        return delegate.getWebsocketCompressionLevel();
    }

    public void setWebsocketCompressionLevel(int websocketCompressionLevel) {
        delegate.setWebsocketCompressionLevel(websocketCompressionLevel);
    }

    public boolean isWebsocketAllowServerNoContext() {
        return delegate.getWebsocketAllowServerNoContext();
    }

    public void setWebsocketAllowServerNoContext(boolean allowServerNoContext) {
        delegate.setWebsocketAllowServerNoContext(allowServerNoContext);
    }

    public boolean isWebsocketPreferredClientNoContext() {
        return delegate.getWebsocketPreferredClientNoContext();
    }

    public void setWebsocketPreferredClientNoContext(boolean preferredClientNoContext) {
        delegate.setWebsocketPreferredClientNoContext(preferredClientNoContext);
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

    // NetworkOptions

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

    public int getTrafficClass() {
        return delegate.getTrafficClass();
    }

    public void setTrafficClass(int trafficClass) {
        delegate.setTrafficClass(trafficClass);
    }

    public boolean isReuseAddress() {
        return delegate.isReuseAddress();
    }

    public void setReuseAddress(boolean reuseAddress) {
        delegate.setReuseAddress(reuseAddress);
    }

    public boolean getLogActivity() {
        return delegate.getLogActivity();
    }

    public void setLogActivity(boolean logActivity) {
        delegate.setLogActivity(logActivity);
    }

    public boolean isReusePort() {
        return delegate.isReusePort();
    }

    public void setReusePort(boolean reusePort) {
        delegate.setReusePort(reusePort);
    }
}
