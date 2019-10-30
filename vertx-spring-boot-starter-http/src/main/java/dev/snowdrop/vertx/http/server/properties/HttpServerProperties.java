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
     * @see HttpServerProperties#getPort()
     * @return the port
     */
    public int getPort() {
        return delegate.getPort();
    }

    public void setPort(int port) {
        delegate.setPort(port);
    }

    /**
     * @see HttpServerProperties#getHost()
     * @return the host
     */
    public String getHost() {
        return delegate.getHost();
    }

    public void setHost(String host) {
        delegate.setHost(host);
    }

    /**
     * @see HttpServerProperties#getAcceptBacklog()
     * @return the value of accept backlog
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
     * @see HttpServerProperties#getClientAuth()
     * @return client auth value
     */
    public ClientAuth getClientAuth() {
        return delegate.getClientAuth();
    }

    public void setClientAuth(ClientAuth clientAuth) {
        delegate.setClientAuth(clientAuth);
    }

    /**
     * @see HttpServerProperties#isSni()
     * @return whether the server supports Server Name Indication
     */
    public boolean isSni() {
        return delegate.isSni();
    }

    public void setSni(boolean sni) {
        delegate.setSni(sni);
    }

    // Vert.x HttpServerOptions

    /**
     * @see HttpServerProperties#isCompressionSupported()
     * @return {@code true} if the server supports gzip/deflate compression
     */
    public boolean isCompressionSupported() {
        return delegate.isCompressionSupported();
    }

    public void setCompressionSupported(boolean compressionSupported) {
        delegate.setCompressionSupported(compressionSupported);
    }

    /**
     * @see HttpServerProperties#getCompressionLevel()
     * @return the server gzip/deflate 'compression level' to be used in responses when client and server support is turned on
     */
    public int getCompressionLevel() {
        return delegate.getCompressionLevel();
    }

    public void setCompressionLevel(int compressionLevel) {
        delegate.setCompressionLevel(compressionLevel);
    }

    /**
     * @see HttpServerProperties#isAcceptUnmaskedFrames()
     * @return whether server accepts unmasked frames
     */
    public boolean isAcceptUnmaskedFrames() {
        return delegate.isAcceptUnmaskedFrames();
    }

    public void setAcceptUnmaskedFrames(boolean acceptUnmaskedFrames) {
        delegate.setAcceptUnmaskedFrames(acceptUnmaskedFrames);
    }

    /**
     * @see HttpServerProperties#getMaxWebsocketFrameSize()
     * @return  the maximum WebSocket frame size
     */
    public int getMaxWebsocketFrameSize() {
        return delegate.getMaxWebsocketFrameSize();
    }

    public void setMaxWebsocketFrameSize(int maxWebsocketFrameSize) {
        delegate.setMaxWebsocketFrameSize(maxWebsocketFrameSize);
    }

    /**
     * @see HttpServerProperties#getMaxWebsocketMessageSize()
     * @return  the maximum WebSocket message size
     */
    public int getMaxWebsocketMessageSize() {
        return delegate.getMaxWebsocketMessageSize();
    }

    public void setMaxWebsocketMessageSize(int maxWebsocketMessageSize) {
        delegate.setMaxWebsocketMessageSize(maxWebsocketMessageSize);
    }

    /**
     * @see HttpServerProperties#getWebsocketSubProtocols()
     * @return Get the WebSocket sub-protocols
     */
    public String getWebsocketSubProtocols() {
        return delegate.getWebsocketSubProtocols();
    }

    public void setWebsocketSubProtocols(String subProtocols) {
        delegate.setWebsocketSubProtocols(subProtocols);
    }

    /**
     * @see HttpServerProperties#isHandle100ContinueAutomatically()
     * @return whether 100 Continue should be handled automatically
     */
    public boolean isHandle100ContinueAutomatically() {
        return delegate.isHandle100ContinueAutomatically();
    }

    public void setHandle100ContinueAutomatically(boolean handle100ContinueAutomatically) {
        delegate.setHandle100ContinueAutomatically(handle100ContinueAutomatically);
    }

    /**
     * @see HttpServerProperties#getMaxChunkSize()
     * @return the maximum HTTP chunk size
     */
    public int getMaxChunkSize() {
        return delegate.getMaxChunkSize();
    }

    public void setMaxChunkSize(int maxChunkSize) {
        delegate.setMaxChunkSize(maxChunkSize);
    }

    /**
     * @see HttpServerProperties#getMaxInitialLineLength()
     * @return the maximum length of the initial line for HTTP/1.x (e.g. {@code "GET / HTTP/1.0"})
     */
    public int getMaxInitialLineLength() {
        return delegate.getMaxInitialLineLength();
    }

    public void setMaxInitialLineLength(int maxInitialLineLength) {
        delegate.setMaxInitialLineLength(maxInitialLineLength);
    }

    /**
     * @see HttpServerProperties#getMaxHeaderSize()
     * @return Returns the maximum length of all headers for HTTP/1.x
     */
    public int getMaxHeaderSize() {
        return delegate.getMaxHeaderSize();
    }

    public void setMaxHeaderSize(int maxHeaderSize) {
        delegate.setMaxHeaderSize(maxHeaderSize);
    }

    /**
     * @see HttpServerProperties#getAlpnVersions()
     * @return the list of protocol versions to provide during the Application-Layer Protocol Negotiatiation
     */
    public List<HttpVersion> getAlpnVersions() {
        return delegate.getAlpnVersions();
    }

    public void setAlpnVersions(List<HttpVersion> alpnVersions) {
        delegate.setAlpnVersions(alpnVersions);
    }

    /**
     * @see HttpServerProperties#getHttp2ConnectionWindowSize()
     * @return the default HTTP/2 connection window size
     */
    public int getHttp2ConnectionWindowSize() {
        return delegate.getHttp2ConnectionWindowSize();
    }

    public void setHttp2ConnectionWindowSize(int http2ConnectionWindowSize) {
        delegate.setHttp2ConnectionWindowSize(http2ConnectionWindowSize);
    }

    /**
     * @see HttpServerProperties#isDecompressionSupported()
     * @return {@code true} if the server supports decompression
     */
    public boolean isDecompressionSupported() {
        return delegate.isDecompressionSupported();
    }

    public void setDecompressionSupported(boolean decompressionSupported) {
        delegate.setDecompressionSupported(decompressionSupported);
    }

    /**
     * @see HttpServerProperties#getDecoderInitialBufferSize()
     * @return the initial buffer size for the HTTP decoder
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
     * @see HttpServerProperties#isPerFrameWebsocketCompressionSupported()
     * @return {@code true} if the http server will accept the per-frame deflate compression extension
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
     * @see HttpServerProperties#isPerMessageWebsocketCompressionSupported()
     * @return {@code true} if the http server will accept the per-message deflate compression extension
     */
    public boolean isPerMessageWebsocketCompressionSupported() {
        return delegate.getPerMessageWebsocketCompressionSupported();
    }

    public void setPerMessageWebsocketCompressionSupported(boolean perMessageWebsocketCompressionSupported) {
        delegate.setPerMessageWebsocketCompressionSupported(perMessageWebsocketCompressionSupported);
    }

    /**
     * @see HttpServerProperties#getWebsocketCompressionLevel()
     * @return the current WebSocket deflate compression level
     */
    public int getWebsocketCompressionLevel() {
        return delegate.getWebsocketCompressionLevel();
    }

    public void setWebsocketCompressionLevel(int websocketCompressionLevel) {
        delegate.setWebsocketCompressionLevel(websocketCompressionLevel);
    }

    /**
     * @see HttpServerProperties#isWebsocketAllowServerNoContext()
     * @return {@code true} when the WebSocket server will accept the {@code server_no_context_takeover} parameter for the per-message
     * deflate compression extension offered by the client
     */
    public boolean isWebsocketAllowServerNoContext() {
        return delegate.getWebsocketAllowServerNoContext();
    }

    public void setWebsocketAllowServerNoContext(boolean allowServerNoContext) {
        delegate.setWebsocketAllowServerNoContext(allowServerNoContext);
    }

    /**
     * @see HttpServerProperties#isWebsocketPreferredClientNoContext()
     * @return {@code true} when the WebSocket server will accept the {@code client_no_context_takeover} parameter for the per-message
     * deflate compression extension offered by the client
     */
    public boolean isWebsocketPreferredClientNoContext() {
        return delegate.getWebsocketPreferredClientNoContext();
    }

    public void setWebsocketPreferredClientNoContext(boolean preferredClientNoContext) {
        delegate.setWebsocketPreferredClientNoContext(preferredClientNoContext);
    }

    /**
     * @see io.vertx.core.http.Http2Settings#getHeaderTableSize()
     * @return the {@literal SETTINGS_HEADER_TABLE_SIZE} HTTP/2 setting
     */
    public long getHeaderTableSize() {
        return delegate.getInitialSettings().getHeaderTableSize();
    }

    public void setHeaderTableSize(long headerTableSize) {
        delegate.getInitialSettings().setHeaderTableSize(headerTableSize);
    }

    /**
     * @see io.vertx.core.http.Http2Settings#isPushEnabled()
     * @return the {@literal SETTINGS_ENABLE_PUSH} HTTP/2 setting
     */
    public boolean isPushEnabled() {
        return delegate.getInitialSettings().isPushEnabled();
    }

    public void setPushEnabled(boolean pushEnabled) {
        delegate.getInitialSettings().setPushEnabled(pushEnabled);
    }

    /**
     * @see io.vertx.core.http.Http2Settings#getMaxConcurrentStreams()
     * @return the {@literal SETTINGS_MAX_CONCURRENT_STREAMS} HTTP/2 setting
     */
    public long getMaxConcurrentStreams() {
        return delegate.getInitialSettings().getMaxConcurrentStreams();
    }

    public void setMaxConcurrentStreams(long maxConcurrentStreams) {
        delegate.getInitialSettings().setMaxConcurrentStreams(maxConcurrentStreams);
    }

    /**
     * @see io.vertx.core.http.Http2Settings#getInitialWindowSize()
     * @return the {@literal SETTINGS_INITIAL_WINDOW_SIZE} HTTP/2 setting
     */
    public int getInitialWindowSize() {
        return delegate.getInitialSettings().getInitialWindowSize();
    }

    public void setInitialWindowSize(int initialWindowSize) {
        delegate.getInitialSettings().setInitialWindowSize(initialWindowSize);
    }

    /**
     * @see io.vertx.core.http.Http2Settings#getMaxFrameSize()
     * @return the {@literal SETTINGS_MAX_FRAME_SIZE} HTTP/2 setting
     */
    public int getMaxFrameSize() {
        return delegate.getInitialSettings().getMaxFrameSize();
    }

    public void setMaxFrameSize(int maxFrameSize) {
        delegate.getInitialSettings().setMaxFrameSize(maxFrameSize);
    }

    /**
     * @see io.vertx.core.http.Http2Settings#getMaxHeaderListSize()
     * @return the {@literal SETTINGS_MAX_HEADER_LIST_SIZE} HTTP/2 setting
     */
    public long getMaxHeaderListSize() {
        return delegate.getInitialSettings().getMaxHeaderListSize();
    }

    public void setMaxHeaderListSize(long maxHeaderListSize) {
        delegate.getInitialSettings().setMaxHeaderListSize(maxHeaderListSize);
    }

    /**
     * @see io.vertx.core.http.Http2Settings#getExtraSettings()
     * @return the extra settings used for extending HTTP/2
     */
    public Map<Integer, Long> getHttp2ExtraSettings() {
        return delegate.getInitialSettings().getExtraSettings();
    }

    public void setHttp2ExtraSettings(Map<Integer, Long> http2ExtraSettings) {
        delegate.getInitialSettings().setExtraSettings(http2ExtraSettings);
    }

    // Vert.x TCPSSLOptions

    /**
     * @see HttpServerProperties#isTcpNoDelay()
     * @return TCP no delay enabled ?
     */
    public boolean isTcpNoDelay() {
        return delegate.isTcpNoDelay();
    }

    public void setTcpNoDelay(boolean tcpNoDelay) {
        delegate.setTcpNoDelay(tcpNoDelay);
    }

    /**
     * @see HttpServerProperties#isTcpKeepAlive()
     * @return is TCP keep alive enabled?
     */
    public boolean isTcpKeepAlive() {
        return delegate.isTcpKeepAlive();
    }

    public void setTcpKeepAlive(boolean tcpKeepAlive) {
        delegate.setTcpKeepAlive(tcpKeepAlive);
    }

    /**
     * @see HttpServerProperties#getSoLinger()
     * @return is SO_linger enabled
     */
    public int getSoLinger() {
        return delegate.getSoLinger();
    }

    public void setSoLinger(int soLinger) {
        delegate.setSoLinger(soLinger);
    }

    /**
     * @see HttpServerProperties#getIdleTimeout()
     * @return the idle timeout, in time unit specified by {@link #getIdleTimeoutUnit()}.
     */
    public int getIdleTimeout() {
        return delegate.getIdleTimeout();
    }

    public void setIdleTimeout(int idleTimeout) {
        delegate.setIdleTimeout(idleTimeout);
    }

    /**
     * @see HttpServerProperties#getIdleTimeoutUnit()
     * @return the idle timeout unit.
     */
    public TimeUnit getIdleTimeoutUnit() {
        return delegate.getIdleTimeoutUnit();
    }

    public void setIdleTimeoutUnit(TimeUnit idleTimeoutUnit) {
        delegate.setIdleTimeoutUnit(idleTimeoutUnit);
    }

    /**
     * @see HttpServerProperties#isSsl()
     * @return is SSL/TLS enabled?
     */
    public boolean isSsl() {
        return delegate.isSsl();
    }

    public void setSsl(boolean ssl) {
        delegate.setSsl(ssl);
    }

    /**
     * @see HttpServerProperties#getEnabledCipherSuites()
     * @return the enabled cipher suites
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
     * @see HttpServerProperties#isUseAlpn()
     * @return whether to use or not Application-Layer Protocol Negotiation
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
     * @see HttpServerProperties#getEnabledSecureTransportProtocols()
     * @return the enabled protocols
     */
    public Set<String> getEnabledSecureTransportProtocols() {
        return delegate.getEnabledSecureTransportProtocols();
    }

    public void setEnabledSecureTransportProtocols(Set<String> enabledSecureTransportProtocols) {
        delegate.setEnabledSecureTransportProtocols(enabledSecureTransportProtocols);
    }

    /**
     * @see HttpServerProperties#isTcpFastOpen()
     * @return wether {@code TCP_FASTOPEN} option is enabled
     */
    public boolean isTcpFastOpen() {
        return delegate.isTcpFastOpen();
    }

    public void setTcpFastOpen(boolean tcpFastOpen) {
        delegate.setTcpFastOpen(tcpFastOpen);
    }

    /**
     * @see HttpServerProperties#isTcpCork()
     * @return wether {@code TCP_CORK} option is enabled
     */
    public boolean isTcpCork() {
        return delegate.isTcpCork();
    }

    public void setTcpCork(boolean tcpCork) {
        delegate.setTcpCork(tcpCork);
    }

    /**
     * @see HttpServerProperties#isTcpQuickAck()
     * @return wether {@code TCP_QUICKACK} option is enabled
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
     * @see HttpServerProperties#getSendBufferSize()
     * @return the send buffer size
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
     * @see HttpServerProperties#getReceiveBufferSize()
     * @return the receive buffer size
     */
    public int getReceiveBufferSize() {
        return delegate.getReceiveBufferSize();
    }

    public void setReceiveBufferSize(int receiveBufferSize) {
        delegate.setReceiveBufferSize(receiveBufferSize);
    }

    /**
     * @see HttpServerProperties#getTrafficClass()
     * @return  the value of traffic class
     */
    public int getTrafficClass() {
        return delegate.getTrafficClass();
    }

    public void setTrafficClass(int trafficClass) {
        delegate.setTrafficClass(trafficClass);
    }

    /**
     * @see HttpServerProperties#isReuseAddress()
     * @return  the value of reuse address
     */
    public boolean isReuseAddress() {
        return delegate.isReuseAddress();
    }

    public void setReuseAddress(boolean reuseAddress) {
        delegate.setReuseAddress(reuseAddress);
    }

    /**
     * @see HttpServerProperties#getLogActivity()
     * @return true when network activity logging is enabled
     */
    public boolean getLogActivity() {
        return delegate.getLogActivity();
    }

    public void setLogActivity(boolean logActivity) {
        delegate.setLogActivity(logActivity);
    }

    /**
     * @see HttpServerProperties#isReusePort()
     * @return  the value of reuse address - only supported by native transports
     */
    public boolean isReusePort() {
        return delegate.isReusePort();
    }

    public void setReusePort(boolean reusePort) {
        delegate.setReusePort(reusePort);
    }
}
