package me.snowdrop.vertx.http.properties;

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
 *
 * sslEngineOptions, crlPath or crlValues are required, please use HttpServerOptionsCustomizer.
 * If keyCertOptions or trustOptions either use {@link HttpServerOptionsCustomizer} or {@link ServerProperties}.
 */
@ConfigurationProperties(prefix = VertxHttpServerProperties.PROPERTIES_PREFIX)
public class VertxHttpServerProperties {

    static final String PROPERTIES_PREFIX = "vertx.http.server";

    private final HttpServerOptions httpServerOptions = new HttpServerOptions();

    public HttpServerOptions getHttpServerOptions() {
        return new HttpServerOptions(httpServerOptions);
    }

    // Vert.x NetServerOptions

    public int getPort() {
        return httpServerOptions.getPort();
    }

    public void setPort(int port) {
        httpServerOptions.setPort(port);
    }

    public String getHost() {
        return httpServerOptions.getHost();
    }

    public void setHost(String host) {
        httpServerOptions.setHost(host);
    }

    public int getAcceptBacklog() {
        return httpServerOptions.getAcceptBacklog();
    }

    public void setAcceptBacklog(int acceptBacklog) {
        httpServerOptions.setAcceptBacklog(acceptBacklog);
    }

    public ClientAuth getClientAuth() {
        return httpServerOptions.getClientAuth();
    }

    public void setClientAuth(ClientAuth clientAuth) {
        httpServerOptions.setClientAuth(clientAuth);
    }

    public boolean isSni() {
        return httpServerOptions.isSni();
    }

    public void setSni(boolean sni) {
        httpServerOptions.setSni(sni);
    }

    // Vert.x HttpServerOptions

    public boolean isCompressionSupported() {
        return httpServerOptions.isCompressionSupported();
    }

    public void setCompressionSupported(boolean compressionSupported) {
        httpServerOptions.setCompressionSupported(compressionSupported);
    }

    public int getCompressionLevel() {
        return httpServerOptions.getCompressionLevel();
    }

    public void setCompressionLevel(int compressionLevel) {
        httpServerOptions.setCompressionLevel(compressionLevel);
    }

    public boolean isAcceptUnmaskedFrames() {
        return httpServerOptions.isAcceptUnmaskedFrames();
    }

    public void setAcceptUnmaskedFrames(boolean acceptUnmaskedFrames) {
        httpServerOptions.setAcceptUnmaskedFrames(acceptUnmaskedFrames);
    }

    public int getMaxWebsocketFrameSize() {
        return httpServerOptions.getMaxWebsocketFrameSize();
    }

    public void setMaxWebsocketFrameSize(int maxWebsocketFrameSize) {
        httpServerOptions.setMaxWebsocketFrameSize(maxWebsocketFrameSize);
    }

    public int getMaxWebsocketMessageSize() {
        return httpServerOptions.getMaxWebsocketMessageSize();
    }

    public void setMaxWebsocketMessageSize(int maxWebsocketMessageSize) {
        httpServerOptions.setMaxWebsocketMessageSize(maxWebsocketMessageSize);
    }

    public String getWebsocketSubProtocols() {
        return httpServerOptions.getWebsocketSubProtocols();
    }

    public void setWebsocketSubProtocols(String subProtocols) {
        httpServerOptions.setWebsocketSubProtocols(subProtocols);
    }

    public boolean isHandle100ContinueAutomatically() {
        return httpServerOptions.isHandle100ContinueAutomatically();
    }

    public void setHandle100ContinueAutomatically(boolean handle100ContinueAutomatically) {
        httpServerOptions.setHandle100ContinueAutomatically(handle100ContinueAutomatically);
    }

    public int getMaxChunkSize() {
        return httpServerOptions.getMaxChunkSize();
    }

    public void setMaxChunkSize(int maxChunkSize) {
        httpServerOptions.setMaxChunkSize(maxChunkSize);
    }

    public int getMaxInitialLineLength() {
        return httpServerOptions.getMaxInitialLineLength();
    }

    public void setMaxInitialLineLength(int maxInitialLineLength) {
        httpServerOptions.setMaxInitialLineLength(maxInitialLineLength);
    }

    public int getMaxHeaderSize() {
        return httpServerOptions.getMaxHeaderSize();
    }

    public void setMaxHeaderSize(int maxHeaderSize) {
        httpServerOptions.setMaxHeaderSize(maxHeaderSize);
    }

    public List<HttpVersion> getAlpnVersions() {
        return httpServerOptions.getAlpnVersions();
    }

    public void setAlpnVersions(List<HttpVersion> alpnVersions) {
        httpServerOptions.setAlpnVersions(alpnVersions);
    }

    public int getHttp2ConnectionWindowSize() {
        return httpServerOptions.getHttp2ConnectionWindowSize();
    }

    public void setHttp2ConnectionWindowSize(int http2ConnectionWindowSize) {
        httpServerOptions.setHttp2ConnectionWindowSize(http2ConnectionWindowSize);
    }

    public boolean isDecompressionSupported() {
        return httpServerOptions.isDecompressionSupported();
    }

    public void setDecompressionSupported(boolean decompressionSupported) {
        httpServerOptions.setDecompressionSupported(decompressionSupported);
    }

    public int getDecoderInitialBufferSize() {
        return httpServerOptions.getDecoderInitialBufferSize();
    }

    public void setDecoderInitialBufferSize(int decoderInitialBufferSize) {
        httpServerOptions.setDecoderInitialBufferSize(decoderInitialBufferSize);
    }

    public boolean isPerFrameWebsocketCompressionSupported() {
        return httpServerOptions.perFrameWebsocketCompressionSupported();
    }

    public void setPerFrameWebsocketCompressionSupported(boolean perFrameWebsocketCompressionSupported) {
        httpServerOptions.setPerFrameWebsocketCompressionSupported(perFrameWebsocketCompressionSupported);
    }

    public boolean isPerMessageWebsocketCompressionSupported() {
        return httpServerOptions.perMessageWebsocketCompressionSupported();
    }

    public void setPerMessageWebsocketCompressionSupported(boolean perMessageWebsocketCompressionSupported) {
        httpServerOptions.setPerMessageWebsocketCompressionSupported(perMessageWebsocketCompressionSupported);
    }

    public int getWebsocketCompressionLevel() {
        return httpServerOptions.websocketCompressionLevel();
    }

    public void setWebsocketCompressionLevel(int websocketCompressionLevel) {
        httpServerOptions.setWebsocketCompressionLevel(websocketCompressionLevel);
    }

    public boolean isWebsocketAllowServerNoContext() {
        return httpServerOptions.getWebsocketAllowServerNoContext();
    }

    public void setWebsocketAllowServerNoContext(boolean allowServerNoContext) {
        httpServerOptions.setWebsocketAllowServerNoContext(allowServerNoContext);
    }

    public boolean isWebsocketPreferredClientNoContext() {
        return httpServerOptions.getWebsocketPreferredClientNoContext();
    }

    public void setWebsocketPreferredClientNoContext(boolean preferredClientNoContext) {
        httpServerOptions.setWebsocketPreferredClientNoContext(preferredClientNoContext);
    }

    public long getHeaderTableSize() {
        return httpServerOptions.getInitialSettings().getHeaderTableSize();
    }

    public void setHeaderTableSize(long headerTableSize) {
        httpServerOptions.getInitialSettings().setHeaderTableSize(headerTableSize);
    }

    public boolean isPushEnabled() {
        return httpServerOptions.getInitialSettings().isPushEnabled();
    }

    public void setPushEnabled(boolean pushEnabled) {
        httpServerOptions.getInitialSettings().setPushEnabled(pushEnabled);
    }

    public long getMaxConcurrentStreams() {
        return httpServerOptions.getInitialSettings().getMaxConcurrentStreams();
    }

    public void getMaxConcurrentStreams(long maxConcurrentStreams) {
        httpServerOptions.getInitialSettings().setMaxConcurrentStreams(maxConcurrentStreams);
    }

    public int getInitialWindowSize() {
        return httpServerOptions.getInitialSettings().getInitialWindowSize();
    }

    public void getMaxConcurrentStreams(int initialWindowSize) {
        httpServerOptions.getInitialSettings().setInitialWindowSize(initialWindowSize);
    }

    public int getMaxFrameSize() {
        return httpServerOptions.getInitialSettings().getMaxFrameSize();
    }

    public void setMaxFrameSize(int maxFrameSize) {
        httpServerOptions.getInitialSettings().setMaxFrameSize(maxFrameSize);
    }

    public long getMaxHeaderListSize() {
        return httpServerOptions.getInitialSettings().getMaxHeaderListSize();
    }

    public void setMaxHeaderListSize(long maxHeaderListSize) {
        httpServerOptions.getInitialSettings().setMaxHeaderListSize(maxHeaderListSize);
    }

    public Map<Integer, Long> getHttp2ExtraSettings() {
        return httpServerOptions.getInitialSettings().getExtraSettings();
    }

    public void setHttp2ExtraSettings(Map<Integer, Long> http2ExtraSettings) {
        httpServerOptions.getInitialSettings().setExtraSettings(http2ExtraSettings);
    }

    // Vert.x TCPSSLOptions

    public boolean isTcpNoDelay() {
        return httpServerOptions.isTcpNoDelay();
    }

    public void setTcpNoDelay(boolean tcpNoDelay) {
        httpServerOptions.setTcpNoDelay(tcpNoDelay);
    }

    public boolean isTcpKeepAlive() {
        return httpServerOptions.isTcpKeepAlive();
    }

    public void setTcpKeepAlive(boolean tcpKeepAlive) {
        httpServerOptions.setTcpKeepAlive(tcpKeepAlive);
    }

    public int getSoLinger() {
        return httpServerOptions.getSoLinger();
    }

    public void setSoLinger(int soLinger) {
        httpServerOptions.setSoLinger(soLinger);
    }

    public int getIdleTimeout() {
        return httpServerOptions.getIdleTimeout();
    }

    public void setIdleTimeout(int idleTimeout) {
        httpServerOptions.setIdleTimeout(idleTimeout);
    }

    public TimeUnit getIdleTimeoutUnit() {
        return httpServerOptions.getIdleTimeoutUnit();
    }

    public void setIdleTimeoutUnit(TimeUnit idleTimeoutUnit) {
        httpServerOptions.setIdleTimeoutUnit(idleTimeoutUnit);
    }

    public boolean isSsl() {
        return httpServerOptions.isSsl();
    }

    public void setSsl(boolean ssl) {
        httpServerOptions.setSsl(ssl);
    }

    public Set<String> getEnabledCipherSuites() {
        return httpServerOptions.getEnabledCipherSuites();
    }

    public void setEnabledCipherSuites(Set<String> enabledCipherSuites) {
        if (enabledCipherSuites != null) {
            enabledCipherSuites.forEach(httpServerOptions::addEnabledCipherSuite);
        }
    }

    public boolean isUseAlpn() {
        return httpServerOptions.isUseAlpn();
    }

    public void setUseAlpn(boolean useAlpn) {
        httpServerOptions.setUseAlpn(useAlpn);
    }

    public Set<String> getEnabledSecureTransportProtocols() {
        return httpServerOptions.getEnabledSecureTransportProtocols();
    }

    public void setEnabledSecureTransportProtocols(Set<String> enabledSecureTransportProtocols) {
        httpServerOptions.setEnabledSecureTransportProtocols(enabledSecureTransportProtocols);
    }

    public boolean isTcpFastOpen() {
        return httpServerOptions.isTcpFastOpen();
    }

    public void setTcpFastOpen(boolean tcpFastOpen) {
        httpServerOptions.setTcpFastOpen(tcpFastOpen);
    }

    public boolean isTcpCork() {
        return httpServerOptions.isTcpCork();
    }

    public void setTcpCork(boolean tcpCork) {
        httpServerOptions.setTcpCork(tcpCork);
    }

    public boolean isTcpQuickAck() {
        return httpServerOptions.isTcpQuickAck();
    }

    public void setTcpQuickAck(boolean tcpQuickAck) {
        httpServerOptions.setTcpQuickAck(tcpQuickAck);
    }

    // NetworkOptions

    public int getSendBufferSize() {
        return httpServerOptions.getSendBufferSize();
    }

    public void setSendBufferSize(int sendBufferSize) {
        httpServerOptions.setSendBufferSize(sendBufferSize);
    }

    public int getReceiveBufferSize() {
        return httpServerOptions.getReceiveBufferSize();
    }

    public void setReceiveBufferSize(int receiveBufferSize) {
        httpServerOptions.setReceiveBufferSize(receiveBufferSize);
    }

    public int getTrafficClass() {
        return httpServerOptions.getTrafficClass();
    }

    public void setTrafficClass(int trafficClass) {
        httpServerOptions.setTrafficClass(trafficClass);
    }

    public boolean isReuseAddress() {
        return httpServerOptions.isReuseAddress();
    }

    public void setReuseAddress(boolean reuseAddress) {
        httpServerOptions.setReuseAddress(reuseAddress);
    }

    public boolean getLogActivity() {
        return httpServerOptions.getLogActivity();
    }

    public void setLogActivity(boolean logActivity) {
        httpServerOptions.setLogActivity(logActivity);
    }

    public boolean isReusePort() {
        return httpServerOptions.isReusePort();
    }

    public void setReusePort(boolean reusePort) {
        httpServerOptions.setReusePort(reusePort);
    }
}
