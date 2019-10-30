package dev.snowdrop.vertx.http.server.properties;

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

    /**
     * @return the port
     * @see HttpServerProperties#getPort()
     */
    public int getPort() {
        return delegate.getPort();
    }

    public void setPort(int port) {
        delegate.setPort(port);
    }

    /**
     * @return the host
     * @see HttpServerProperties#getHost()
     */
    public String getHost() {
        return delegate.getHost();
    }

    public void setHost(String host) {
        delegate.setHost(host);
    }

    /**
     * @return the value of accept backlog
     * @see HttpServerProperties#getAcceptBacklog()
     */
    public int getAcceptBacklog() {
        return delegate.getAcceptBacklog();
    }

    public void setAcceptBacklog(int acceptBacklog) {
        delegate.setAcceptBacklog(acceptBacklog);
    }

    /**
     * One of "NONE, REQUEST, REQUIRED". If it's set to "REQUIRED" then server will require the
     * SSL cert to be presented otherwise it won't accept the request. If it's set to "REQUEST" then
     * it won't mandate the certificate to be presented, basically make it optional.
     *
     * @return client auth value
     * @see HttpServerProperties#getClientAuth()
     */
    public ClientAuth getClientAuth() {
        return delegate.getClientAuth();
    }

    public void setClientAuth(ClientAuth clientAuth) {
        delegate.setClientAuth(clientAuth);
    }

    /**
     * @return whether the server supports Server Name Indication
     * @see HttpServerProperties#isSni()
     */
    public boolean isSni() {
        return delegate.isSni();
    }

    public void setSni(boolean sni) {
        delegate.setSni(sni);
    }

    // Vert.x HttpServerOptions

    /**
     * @return {@code true} if the server supports gzip/deflate compression
     * @see HttpServerProperties#isCompressionSupported()
     */
    public boolean isCompressionSupported() {
        return delegate.isCompressionSupported();
    }

    public void setCompressionSupported(boolean compressionSupported) {
        delegate.setCompressionSupported(compressionSupported);
    }

    /**
     * @return the server gzip/deflate 'compression level' to be used in responses when client and server support is turned on
     * @see HttpServerProperties#getCompressionLevel()
     */
    public int getCompressionLevel() {
        return delegate.getCompressionLevel();
    }

    public void setCompressionLevel(int compressionLevel) {
        delegate.setCompressionLevel(compressionLevel);
    }

    /**
     * @return whether server accepts unmasked frames
     * @see HttpServerProperties#isAcceptUnmaskedFrames()
     */
    public boolean isAcceptUnmaskedFrames() {
        return delegate.isAcceptUnmaskedFrames();
    }

    public void setAcceptUnmaskedFrames(boolean acceptUnmaskedFrames) {
        delegate.setAcceptUnmaskedFrames(acceptUnmaskedFrames);
    }

    /**
     * @return the maximum WebSocket frame size
     * @see HttpServerProperties#getMaxWebsocketFrameSize()
     */
    public int getMaxWebsocketFrameSize() {
        return delegate.getMaxWebsocketFrameSize();
    }

    public void setMaxWebsocketFrameSize(int maxWebsocketFrameSize) {
        delegate.setMaxWebsocketFrameSize(maxWebsocketFrameSize);
    }

    /**
     * @return the maximum WebSocket message size
     * @see HttpServerProperties#getMaxWebsocketMessageSize()
     */
    public int getMaxWebsocketMessageSize() {
        return delegate.getMaxWebsocketMessageSize();
    }

    public void setMaxWebsocketMessageSize(int maxWebsocketMessageSize) {
        delegate.setMaxWebsocketMessageSize(maxWebsocketMessageSize);
    }

    /**
     * @return Get the WebSocket sub-protocols
     * @see HttpServerProperties#getWebsocketSubProtocols()
     */
    public String getWebsocketSubProtocols() {
        return delegate.getWebsocketSubProtocols();
    }

    public void setWebsocketSubProtocols(String subProtocols) {
        delegate.setWebsocketSubProtocols(subProtocols);
    }

    /**
     * @return whether 100 Continue should be handled automatically
     * @see HttpServerProperties#isHandle100ContinueAutomatically()
     */
    public boolean isHandle100ContinueAutomatically() {
        return delegate.isHandle100ContinueAutomatically();
    }

    public void setHandle100ContinueAutomatically(boolean handle100ContinueAutomatically) {
        delegate.setHandle100ContinueAutomatically(handle100ContinueAutomatically);
    }

    /**
     * @return the maximum HTTP chunk size
     * @see HttpServerProperties#getMaxChunkSize()
     */
    public int getMaxChunkSize() {
        return delegate.getMaxChunkSize();
    }

    public void setMaxChunkSize(int maxChunkSize) {
        delegate.setMaxChunkSize(maxChunkSize);
    }

    /**
     * @return the maximum length of the initial line for HTTP/1.x (e.g. {@code "GET / HTTP/1.0"})
     * @see HttpServerProperties#getMaxInitialLineLength()
     */
    public int getMaxInitialLineLength() {
        return delegate.getMaxInitialLineLength();
    }

    public void setMaxInitialLineLength(int maxInitialLineLength) {
        delegate.setMaxInitialLineLength(maxInitialLineLength);
    }

    /**
     * @return Returns the maximum length of all headers for HTTP/1.x
     * @see HttpServerProperties#getMaxHeaderSize()
     */
    public int getMaxHeaderSize() {
        return delegate.getMaxHeaderSize();
    }

    public void setMaxHeaderSize(int maxHeaderSize) {
        delegate.setMaxHeaderSize(maxHeaderSize);
    }

    /**
     * @return the list of protocol versions to provide during the Application-Layer Protocol Negotiatiation
     * @see HttpServerProperties#getAlpnVersions()
     */
    public List<HttpVersion> getAlpnVersions() {
        return delegate.getAlpnVersions();
    }

    public void setAlpnVersions(List<HttpVersion> alpnVersions) {
        delegate.setAlpnVersions(alpnVersions);
    }

    /**
     * @return the default HTTP/2 connection window size
     * @see HttpServerProperties#getHttp2ConnectionWindowSize()
     */
    public int getHttp2ConnectionWindowSize() {
        return delegate.getHttp2ConnectionWindowSize();
    }

    public void setHttp2ConnectionWindowSize(int http2ConnectionWindowSize) {
        delegate.setHttp2ConnectionWindowSize(http2ConnectionWindowSize);
    }

    /**
     * @return {@code true} if the server supports decompression
     * @see HttpServerProperties#isDecompressionSupported()
     */
    public boolean isDecompressionSupported() {
        return delegate.isDecompressionSupported();
    }

    public void setDecompressionSupported(boolean decompressionSupported) {
        delegate.setDecompressionSupported(decompressionSupported);
    }

    /**
     * @return the initial buffer size for the HTTP decoder
     * @see HttpServerProperties#getDecoderInitialBufferSize()
     */
    public int getDecoderInitialBufferSize() {
        return delegate.getDecoderInitialBufferSize();
    }

    public void setDecoderInitialBufferSize(int decoderInitialBufferSize) {
        delegate.setDecoderInitialBufferSize(decoderInitialBufferSize);
    }

    /**
     * Get whether WebSocket the per-frame deflate compression extension is supported.
     *
     * @return {@code true} if the http server will accept the per-frame deflate compression extension
     * @see HttpServerProperties#isPerFrameWebsocketCompressionSupported()
     */
    public boolean isPerFrameWebsocketCompressionSupported() {
        return delegate.getPerFrameWebsocketCompressionSupported();
    }

    public void setPerFrameWebsocketCompressionSupported(boolean perFrameWebsocketCompressionSupported) {
        delegate.setPerFrameWebsocketCompressionSupported(perFrameWebsocketCompressionSupported);
    }

    /**
     * Get whether WebSocket per-message deflate compression extension is supported.
     *
     * @return {@code true} if the http server will accept the per-message deflate compression extension
     * @see HttpServerProperties#isPerMessageWebsocketCompressionSupported()
     */
    public boolean isPerMessageWebsocketCompressionSupported() {
        return delegate.getPerMessageWebsocketCompressionSupported();
    }

    public void setPerMessageWebsocketCompressionSupported(boolean perMessageWebsocketCompressionSupported) {
        delegate.setPerMessageWebsocketCompressionSupported(perMessageWebsocketCompressionSupported);
    }

    /**
     * @return the current WebSocket deflate compression level
     * @see HttpServerProperties#getWebsocketCompressionLevel()
     */
    public int getWebsocketCompressionLevel() {
        return delegate.getWebsocketCompressionLevel();
    }

    public void setWebsocketCompressionLevel(int websocketCompressionLevel) {
        delegate.setWebsocketCompressionLevel(websocketCompressionLevel);
    }

    /**
     * @return {@code true} when the WebSocket server will accept the {@code server_no_context_takeover} parameter for the per-message
     * deflate compression extension offered by the client
     * @see HttpServerProperties#isWebsocketAllowServerNoContext()
     */
    public boolean isWebsocketAllowServerNoContext() {
        return delegate.getWebsocketAllowServerNoContext();
    }

    public void setWebsocketAllowServerNoContext(boolean allowServerNoContext) {
        delegate.setWebsocketAllowServerNoContext(allowServerNoContext);
    }

    /**
     * @return {@code true} when the WebSocket server will accept the {@code client_no_context_takeover} parameter for the per-message
     * deflate compression extension offered by the client
     * @see HttpServerProperties#isWebsocketPreferredClientNoContext()
     */
    public boolean isWebsocketPreferredClientNoContext() {
        return delegate.getWebsocketPreferredClientNoContext();
    }

    public void setWebsocketPreferredClientNoContext(boolean preferredClientNoContext) {
        delegate.setWebsocketPreferredClientNoContext(preferredClientNoContext);
    }

    /**
     * @return the {@literal SETTINGS_HEADER_TABLE_SIZE} HTTP/2 setting
     * @see io.vertx.core.http.Http2Settings#getHeaderTableSize()
     */
    public long getHeaderTableSize() {
        return delegate.getInitialSettings().getHeaderTableSize();
    }

    public void setHeaderTableSize(long headerTableSize) {
        delegate.getInitialSettings().setHeaderTableSize(headerTableSize);
    }

    /**
     * @return the {@literal SETTINGS_ENABLE_PUSH} HTTP/2 setting
     * @see io.vertx.core.http.Http2Settings#isPushEnabled()
     */
    public boolean isPushEnabled() {
        return delegate.getInitialSettings().isPushEnabled();
    }

    public void setPushEnabled(boolean pushEnabled) {
        delegate.getInitialSettings().setPushEnabled(pushEnabled);
    }

    /**
     * @return the {@literal SETTINGS_MAX_CONCURRENT_STREAMS} HTTP/2 setting
     * @see io.vertx.core.http.Http2Settings#getMaxConcurrentStreams()
     */
    public long getMaxConcurrentStreams() {
        return delegate.getInitialSettings().getMaxConcurrentStreams();
    }

    public void setMaxConcurrentStreams(long maxConcurrentStreams) {
        delegate.getInitialSettings().setMaxConcurrentStreams(maxConcurrentStreams);
    }

    /**
     * @return the {@literal SETTINGS_INITIAL_WINDOW_SIZE} HTTP/2 setting
     * @see io.vertx.core.http.Http2Settings#getInitialWindowSize()
     */
    public int getInitialWindowSize() {
        return delegate.getInitialSettings().getInitialWindowSize();
    }

    public void setInitialWindowSize(int initialWindowSize) {
        delegate.getInitialSettings().setInitialWindowSize(initialWindowSize);
    }

    /**
     * @return the {@literal SETTINGS_MAX_FRAME_SIZE} HTTP/2 setting
     * @see io.vertx.core.http.Http2Settings#getMaxFrameSize()
     */
    public int getMaxFrameSize() {
        return delegate.getInitialSettings().getMaxFrameSize();
    }

    public void setMaxFrameSize(int maxFrameSize) {
        delegate.getInitialSettings().setMaxFrameSize(maxFrameSize);
    }

    /**
     * @return the {@literal SETTINGS_MAX_HEADER_LIST_SIZE} HTTP/2 setting
     * @see io.vertx.core.http.Http2Settings#getMaxHeaderListSize()
     */
    public long getMaxHeaderListSize() {
        return delegate.getInitialSettings().getMaxHeaderListSize();
    }

    public void setMaxHeaderListSize(long maxHeaderListSize) {
        delegate.getInitialSettings().setMaxHeaderListSize(maxHeaderListSize);
    }

    /**
     * @return the extra settings used for extending HTTP/2
     * @see io.vertx.core.http.Http2Settings#getExtraSettings()
     */
    public Map<Integer, Long> getHttp2ExtraSettings() {
        return delegate.getInitialSettings().getExtraSettings();
    }

    public void setHttp2ExtraSettings(Map<Integer, Long> http2ExtraSettings) {
        delegate.getInitialSettings().setExtraSettings(http2ExtraSettings);
    }

    // Vert.x TCPSSLOptions

    /**
     * @return TCP no delay enabled ?
     * @see HttpServerProperties#isTcpNoDelay()
     */
    public boolean isTcpNoDelay() {
        return delegate.isTcpNoDelay();
    }

    public void setTcpNoDelay(boolean tcpNoDelay) {
        delegate.setTcpNoDelay(tcpNoDelay);
    }

    /**
     * @return is TCP keep alive enabled?
     * @see HttpServerProperties#isTcpKeepAlive()
     */
    public boolean isTcpKeepAlive() {
        return delegate.isTcpKeepAlive();
    }

    public void setTcpKeepAlive(boolean tcpKeepAlive) {
        delegate.setTcpKeepAlive(tcpKeepAlive);
    }

    /**
     * @return is SO_linger enabled
     * @see HttpServerProperties#getSoLinger()
     */
    public int getSoLinger() {
        return delegate.getSoLinger();
    }

    public void setSoLinger(int soLinger) {
        delegate.setSoLinger(soLinger);
    }

    /**
     * @return the idle timeout, in time unit specified by {@link #getIdleTimeoutUnit()}.
     * @see HttpServerProperties#getIdleTimeout()
     */
    public int getIdleTimeout() {
        return delegate.getIdleTimeout();
    }

    public void setIdleTimeout(int idleTimeout) {
        delegate.setIdleTimeout(idleTimeout);
    }

    /**
     * @return the idle timeout unit.
     * @see HttpServerProperties#getIdleTimeoutUnit()
     */
    public TimeUnit getIdleTimeoutUnit() {
        return delegate.getIdleTimeoutUnit();
    }

    public void setIdleTimeoutUnit(TimeUnit idleTimeoutUnit) {
        delegate.setIdleTimeoutUnit(idleTimeoutUnit);
    }

    /**
     * @return is SSL/TLS enabled?
     * @see HttpServerProperties#isSsl()
     */
    public boolean isSsl() {
        return delegate.isSsl();
    }

    public void setSsl(boolean ssl) {
        delegate.setSsl(ssl);
    }

    /**
     * @return the enabled cipher suites
     * @see HttpServerProperties#getEnabledCipherSuites()
     */
    public Set<String> getEnabledCipherSuites() {
        return delegate.getEnabledCipherSuites();
    }

    public void setEnabledCipherSuites(Set<String> enabledCipherSuites) {
        if (enabledCipherSuites != null) {
            enabledCipherSuites.forEach(delegate::addEnabledCipherSuite);
        }
    }

    /**
     * @return whether to use or not Application-Layer Protocol Negotiation
     * @see HttpServerProperties#isUseAlpn()
     */
    public boolean isUseAlpn() {
        return delegate.isUseAlpn();
    }

    public void setUseAlpn(boolean useAlpn) {
        delegate.setUseAlpn(useAlpn);
    }

    /**
     * Returns the enabled SSL/TLS protocols
     *
     * @return the enabled protocols
     * @see HttpServerProperties#getEnabledSecureTransportProtocols()
     */
    public Set<String> getEnabledSecureTransportProtocols() {
        return delegate.getEnabledSecureTransportProtocols();
    }

    public void setEnabledSecureTransportProtocols(Set<String> enabledSecureTransportProtocols) {
        delegate.setEnabledSecureTransportProtocols(enabledSecureTransportProtocols);
    }

    /**
     * @return wether {@code TCP_FASTOPEN} option is enabled
     * @see HttpServerProperties#isTcpFastOpen()
     */
    public boolean isTcpFastOpen() {
        return delegate.isTcpFastOpen();
    }

    public void setTcpFastOpen(boolean tcpFastOpen) {
        delegate.setTcpFastOpen(tcpFastOpen);
    }

    /**
     * @return wether {@code TCP_CORK} option is enabled
     * @see HttpServerProperties#isTcpCork()
     */
    public boolean isTcpCork() {
        return delegate.isTcpCork();
    }

    public void setTcpCork(boolean tcpCork) {
        delegate.setTcpCork(tcpCork);
    }

    /**
     * @return wether {@code TCP_QUICKACK} option is enabled
     * @see HttpServerProperties#isTcpQuickAck()
     */
    public boolean isTcpQuickAck() {
        return delegate.isTcpQuickAck();
    }

    public void setTcpQuickAck(boolean tcpQuickAck) {
        delegate.setTcpQuickAck(tcpQuickAck);
    }

    // NetworkOptions

    /**
     * Return the TCP send buffer size, in bytes.
     *
     * @return the send buffer size
     * @see HttpServerProperties#getSendBufferSize()
     */
    public int getSendBufferSize() {
        return delegate.getSendBufferSize();
    }

    public void setSendBufferSize(int sendBufferSize) {
        delegate.setSendBufferSize(sendBufferSize);
    }

    /**
     * Return the TCP receive buffer size, in bytes
     *
     * @return the receive buffer size
     * @see HttpServerProperties#getReceiveBufferSize()
     */
    public int getReceiveBufferSize() {
        return delegate.getReceiveBufferSize();
    }

    public void setReceiveBufferSize(int receiveBufferSize) {
        delegate.setReceiveBufferSize(receiveBufferSize);
    }

    /**
     * @return the value of traffic class
     * @see HttpServerProperties#getTrafficClass()
     */
    public int getTrafficClass() {
        return delegate.getTrafficClass();
    }

    public void setTrafficClass(int trafficClass) {
        delegate.setTrafficClass(trafficClass);
    }

    /**
     * @return the value of reuse address
     * @see HttpServerProperties#isReuseAddress()
     */
    public boolean isReuseAddress() {
        return delegate.isReuseAddress();
    }

    public void setReuseAddress(boolean reuseAddress) {
        delegate.setReuseAddress(reuseAddress);
    }

    /**
     * @return true when network activity logging is enabled
     * @see HttpServerProperties#getLogActivity()
     */
    public boolean getLogActivity() {
        return delegate.getLogActivity();
    }

    public void setLogActivity(boolean logActivity) {
        delegate.setLogActivity(logActivity);
    }

    /**
     * @return the value of reuse address - only supported by native transports
     * @see HttpServerProperties#isReusePort()
     */
    public boolean isReusePort() {
        return delegate.isReusePort();
    }

    public void setReusePort(boolean reusePort) {
        delegate.setReusePort(reusePort);
    }
}
