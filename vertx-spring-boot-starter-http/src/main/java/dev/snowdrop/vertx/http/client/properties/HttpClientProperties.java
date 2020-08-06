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

    /**
     * Return the TCP send buffer size, in bytes.
     *
     * @return the send buffer size
     * @see HttpClientOptions#getSendBufferSize()
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
     * @see HttpClientOptions#getReceiveBufferSize()
     */
    public int getReceiveBufferSize() {
        return delegate.getReceiveBufferSize();
    }

    public void setReceiveBufferSize(int receiveBufferSize) {
        delegate.setReceiveBufferSize(receiveBufferSize);
    }

    /**
     * @return the value of reuse address
     * @see HttpClientOptions#isReuseAddress()
     */
    public boolean isReuseAddress() {
        return delegate.isReuseAddress();
    }

    public void setReuseAddress(boolean reuseAddress) {
        delegate.setReuseAddress(reuseAddress);
    }

    /**
     * @return the value of reuse address - only supported by native transports
     * @see HttpClientOptions#isReusePort()
     */
    public boolean isReusePort() {
        return delegate.isReusePort();
    }

    public void setReusePort(boolean reusePort) {
        delegate.setReusePort(reusePort);
    }

    /**
     * @return the value of traffic class
     * @see HttpClientOptions#getTrafficClass()
     */
    public int getTrafficClass() {
        return delegate.getTrafficClass();
    }

    public void setTrafficClass(int trafficClass) {
        delegate.setTrafficClass(trafficClass);
    }

    /**
     * @return true when network activity logging is enabled
     * @see HttpClientOptions#getLogActivity()
     */
    public boolean getLogActivity() {
        return delegate.getLogActivity();
    }

    public void setLogActivity(boolean logEnabled) {
        delegate.setLogActivity(logEnabled);
    }

    // Vert.x TCPSSLOptions

    /**
     * @return TCP no delay enabled ?
     * @see HttpClientOptions#isTcpNoDelay()
     */
    public boolean isTcpNoDelay() {
        return delegate.isTcpNoDelay();
    }

    public void setTcpNoDelay(boolean tcpNoDelay) {
        delegate.setTcpNoDelay(tcpNoDelay);
    }

    /**
     * @return is TCP keep alive enabled?
     * @see HttpClientOptions#isTcpKeepAlive()
     */
    public boolean isTcpKeepAlive() {
        return delegate.isTcpKeepAlive();
    }

    public void setTcpKeepAlive(boolean tcpKeepAlive) {
        delegate.setTcpKeepAlive(tcpKeepAlive);
    }

    /**
     * @return is SO_linger enabled
     * @see HttpClientOptions#getSoLinger()
     */
    public int getSoLinger() {
        return delegate.getSoLinger();
    }

    public void setSoLinger(int soLinger) {
        delegate.setSoLinger(soLinger);
    }

    /**
     * @return the idle timeout, in time unit specified by {@link #getIdleTimeoutUnit()}.
     * @see HttpClientOptions#getIdleTimeout()
     */
    public int getIdleTimeout() {
        return delegate.getIdleTimeout();
    }

    public void setIdleTimeout(int idleTimeout) {
        delegate.setIdleTimeout(idleTimeout);
    }

    /**
     * @return the idle timeout unit.
     * @see HttpClientOptions#getIdleTimeoutUnit()
     */
    public TimeUnit getIdleTimeoutUnit() {
        return delegate.getIdleTimeoutUnit();
    }

    public void setIdleTimeoutUnit(TimeUnit idleTimeoutUnit) {
        delegate.setIdleTimeoutUnit(idleTimeoutUnit);
    }

    /**
     * @return is SSL/TLS enabled?
     * @see HttpClientOptions#isSsl()
     */
    public boolean isSsl() {
        return delegate.isSsl();
    }

    public void setSsl(boolean ssl) {
        delegate.setSsl(ssl);
    }

    /**
     * @return the enabled cipher suites
     * @see HttpClientOptions#getEnabledCipherSuites()
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
     * @see HttpClientOptions#isUseAlpn()
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
     * @see HttpClientOptions#getEnabledSecureTransportProtocols()
     */
    public Set<String> getEnabledSecureTransportProtocols() {
        return delegate.getEnabledSecureTransportProtocols();
    }

    public void setEnabledSecureTransportProtocols(Set<String> enabledSecureTransportProtocols) {
        delegate.setEnabledSecureTransportProtocols(enabledSecureTransportProtocols);
    }

    /**
     * @return whether {@code TCP_FASTOPEN} option is enabled
     * @see HttpClientOptions#isTcpFastOpen()
     */
    public boolean isTcpFastOpen() {
        return delegate.isTcpFastOpen();
    }

    public void setTcpFastOpen(boolean tcpFastOpen) {
        delegate.setTcpFastOpen(tcpFastOpen);
    }

    /**
     * @return whether {@code TCP_CORK} option is enabled
     * @see HttpClientOptions#isTcpCork()
     */
    public boolean isTcpCork() {
        return delegate.isTcpCork();
    }

    public void setTcpCork(boolean tcpCork) {
        delegate.setTcpCork(tcpCork);
    }

    /**
     * @return whether {@code TCP_QUICKACK} option is enabled
     * @see HttpClientOptions#isTcpQuickAck()
     */
    public boolean isTcpQuickAck() {
        return delegate.isTcpQuickAck();
    }

    public void setTcpQuickAck(boolean tcpQuickAck) {
        delegate.setTcpQuickAck(tcpQuickAck);
    }

    // Vert.x ClientOptionsBase

    /**
     * @return the value of connect timeout
     * @see HttpClientOptions#getConnectTimeout()
     */
    public int getConnectTimeout() {
        return delegate.getConnectTimeout();
    }

    public void setConnectTimeout(int connectTimeout) {
        delegate.setConnectTimeout(connectTimeout);
    }

    /**
     * @return true if all server certificates should be trusted
     * @see HttpClientOptions#isTrustAll()
     */
    public boolean isTrustAll() {
        return delegate.isTrustAll();
    }

    public void setTrustAll(boolean trustAll) {
        delegate.setTrustAll(trustAll);
    }

    /**
     * @return the metrics name identifying the reported metrics.
     * @see HttpClientOptions#getMetricsName()
     */
    public String getMetricsName() {
        return delegate.getMetricsName();
    }

    public void setMetricsName(String metricsName) {
        delegate.setMetricsName(metricsName);
    }

    /**
     * @return the local interface to bind for network connections.
     * @see HttpClientOptions#getLocalAddress()
     */
    public String getLocalAddress() {
        return delegate.getLocalAddress();
    }

    public void setLocalAddress(String localAddress) {
        delegate.setLocalAddress(localAddress);
    }

    // Vert.x HttpClientOptions

    /**
     * Is hostname verification (for SSL/TLS) enabled?
     *
     * @return {@code true} if enabled
     * @see HttpClientOptions#isVerifyHost()
     */
    public boolean isVerifyHost() {
        return delegate.isVerifyHost();
    }

    public void setVerifyHost(boolean verifyHost) {
        delegate.setVerifyHost(verifyHost);
    }

    /**
     * Get the maximum pool size for connections
     *
     * @return the maximum pool size
     * @see HttpClientOptions#getMaxPoolSize()
     */
    public int getMaxPoolSize() {
        return delegate.getMaxPoolSize();
    }

    public void setMaxPoolSize(int maxPoolSize) {
        delegate.setMaxPoolSize(maxPoolSize);
    }

    /**
     * Is keep alive enabled on the client?
     *
     * @return {@code true} if enabled
     * @see HttpClientOptions#isKeepAlive()
     */
    public boolean isKeepAlive() {
        return delegate.isKeepAlive();
    }

    public void setKeepAlive(boolean keepAlive) {
        delegate.setKeepAlive(keepAlive);
    }

    /**
     * @return the keep alive timeout value in seconds for HTTP/1.x connections
     * @see HttpClientOptions#getKeepAliveTimeout()
     */
    public int getKeepAliveTimeout() {
        return delegate.getKeepAliveTimeout();
    }

    public void setKeepAliveTimeout(int keepAliveTimeout) {
        delegate.setKeepAliveTimeout(keepAliveTimeout);
    }

    /**
     * @return the limit of pending requests a pipe-lined HTTP/1 connection can send
     * @see HttpClientOptions#getPipeliningLimit()
     */
    public int getPipeliningLimit() {
        return delegate.getPipeliningLimit();
    }

    public void setPipeliningLimit(int limit) {
        delegate.setPipeliningLimit(limit);
    }

    /**
     * Is pipe-lining enabled on the client
     *
     * @return {@code true} if pipe-lining is enabled
     * @see HttpClientOptions#isPipelining()
     */
    public boolean isPipelining() {
        return delegate.isPipelining();
    }

    public void setPipelining(boolean pipelining) {
        delegate.setPipelining(pipelining);
    }

    /**
     * Get the maximum pool size for HTTP/2 connections
     *
     * @return the maximum pool size
     * @see HttpClientOptions#getHttp2MaxPoolSize()
     */
    public int getHttp2MaxPoolSize() {
        return delegate.getHttp2MaxPoolSize();
    }

    public void setHttp2MaxPoolSize(int max) {
        delegate.setHttp2MaxPoolSize(max);
    }

    /**
     * @return the maximum number of concurrent streams for an HTTP/2 connection, {@code -1} means
     * the value sent by the server
     * @see HttpClientOptions#getHttp2MultiplexingLimit()
     */
    public int getHttp2MultiplexingLimit() {
        return delegate.getHttp2MultiplexingLimit();
    }

    public void setHttp2MultiplexingLimit(int limit) {
        delegate.setHttp2MultiplexingLimit(limit);
    }

    /**
     * @return the default HTTP/2 connection window size
     * @see HttpClientOptions#getHttp2ConnectionWindowSize()
     */
    public int getHttp2ConnectionWindowSize() {
        return delegate.getHttp2ConnectionWindowSize();
    }

    public void setHttp2ConnectionWindowSize(int http2ConnectionWindowSize) {
        delegate.setHttp2ConnectionWindowSize(http2ConnectionWindowSize);
    }

    /**
     * @return the keep alive timeout value in seconds for HTTP/2 connections
     * @see HttpClientOptions#getHttp2KeepAliveTimeout()
     */
    public int getHttp2KeepAliveTimeout() {
        return delegate.getHttp2KeepAliveTimeout();
    }

    public void setHttp2KeepAliveTimeout(int keepAliveTimeout) {
        delegate.setHttp2KeepAliveTimeout(keepAliveTimeout);
    }

    /**
     * @return the connection pool cleaner period in ms.
     * @see HttpClientOptions#getPoolCleanerPeriod()
     */
    public int getPoolCleanerPeriod() {
        return delegate.getPoolCleanerPeriod();
    }

    public void setPoolCleanerPeriod(int poolCleanerPeriod) {
        delegate.setPoolCleanerPeriod(poolCleanerPeriod);
    }

    /**
     * Is compression enabled on the client?
     *
     * @return {@code true} if enabled
     * @see HttpClientOptions#isTryUseCompression()
     */
    public boolean isTryUseCompression() {
        return delegate.isTryUseCompression();
    }

    public void setTryUseCompression(boolean tryUseCompression) {
        delegate.setTryUseCompression(tryUseCompression);
    }

    /**
     * Get the maximum WebSocket frame size to use
     *
     * @return the max WebSocket frame size
     * @see HttpClientOptions#getMaxWebSocketFrameSize()
     * @deprecated use {@link #getMaxWebSocketFrameSize()}
     */
    @Deprecated
    public int getMaxWebsocketFrameSize() {
        return delegate.getMaxWebSocketFrameSize();
    }

    /**
     * Get the maximum WebSocket frame size to use
     *
     * @return the max WebSocket frame size
     * @see HttpClientOptions#getMaxWebSocketFrameSize()
     */
    public int getMaxWebSocketFrameSize() {
        return delegate.getMaxWebSocketFrameSize();
    }

    @Deprecated
    public void setMaxWebsocketFrameSize(int maxWebSocketFrameSize) {
        delegate.setMaxWebSocketFrameSize(maxWebSocketFrameSize);
    }

    public void setMaxWebSocketFrameSize(int maxWebSocketFrameSize) {
        delegate.setMaxWebSocketFrameSize(maxWebSocketFrameSize);
    }

    /**
     * Get the maximum WebSocket message size to use
     *
     * @return the max WebSocket message size
     * @see HttpClientOptions#getMaxWebSocketMessageSize()
     * @deprecated use {@link #getMaxWebSocketMessageSize()}
     */
    @Deprecated
    public int getMaxWebsocketMessageSize() {
        return delegate.getMaxWebSocketMessageSize();
    }

    /**
     * Get the maximum WebSocket message size to use
     *
     * @return the max WebSocket message size
     * @see HttpClientOptions#getMaxWebSocketMessageSize()
     */
    public int getMaxWebSocketMessageSize() {
        return delegate.getMaxWebSocketMessageSize();
    }

    @Deprecated
    public void setMaxWebsocketMessageSize(int maxWebSocketMessageSize) {
        delegate.setMaxWebSocketMessageSize(maxWebSocketMessageSize);
    }

    public void setMaxWebSocketMessageSize(int maxWebSocketMessageSize) {
        delegate.setMaxWebSocketMessageSize(maxWebSocketMessageSize);
    }

    /**
     * Get the default host name to be used by this client in requests if none is provided when making the request.
     *
     * @return the default host name
     * @see HttpClientOptions#getDefaultHost()
     */
    public String getDefaultHost() {
        return delegate.getDefaultHost();
    }

    public void setDefaultHost(String defaultHost) {
        delegate.setDefaultHost(defaultHost);
    }

    /**
     * Get the default port to be used by this client in requests if none is provided when making the request.
     *
     * @return the default port
     * @see HttpClientOptions#getDefaultPort()
     */
    public int getDefaultPort() {
        return delegate.getDefaultPort();
    }

    public void setDefaultPort(int defaultPort) {
        delegate.setDefaultPort(defaultPort);
    }

    /**
     * Get the protocol version.
     *
     * @return the protocol version
     * @see HttpClientOptions#getProtocolVersion()
     */
    public HttpVersion getProtocolVersion() {
        return delegate.getProtocolVersion();
    }

    public void setProtocolVersion(HttpVersion protocolVersion) {
        delegate.setProtocolVersion(protocolVersion);
    }

    /**
     * Returns the maximum HTTP chunk size
     *
     * @return the maximum HTTP chunk size
     * @see HttpClientOptions#getMaxChunkSize()
     */
    public int getMaxChunkSize() {
        return delegate.getMaxChunkSize();
    }

    public void setMaxChunkSize(int maxChunkSize) {
        delegate.setMaxChunkSize(maxChunkSize);
    }

    /**
     * @return the maximum length of the initial line for HTTP/1.x (e.g. {@code "GET / HTTP/1.0"})
     * @see HttpClientOptions#getMaxInitialLineLength()
     */
    public int getMaxInitialLineLength() {
        return delegate.getMaxInitialLineLength();
    }

    public void setMaxInitialLineLength(int maxInitialLineLength) {
        delegate.setMaxInitialLineLength(maxInitialLineLength);
    }

    /**
     * @return Returns the maximum length of all headers for HTTP/1.x
     * @see HttpClientOptions#getMaxHeaderSize()
     */
    public int getMaxHeaderSize() {
        return delegate.getMaxHeaderSize();
    }

    public void setMaxHeaderSize(int maxHeaderSize) {
        delegate.setMaxHeaderSize(maxHeaderSize);
    }

    /**
     * Returns the maximum wait queue size
     *
     * @return the maximum wait queue size
     * @see HttpClientOptions#getMaxWaitQueueSize()
     */
    public int getMaxWaitQueueSize() {
        return delegate.getMaxWaitQueueSize();
    }

    public void setMaxWaitQueueSize(int maxWaitQueueSize) {
        delegate.setMaxWaitQueueSize(maxWaitQueueSize);
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

    /**
     * @return the list of protocol versions to provide during the Application-Layer Protocol Negotiation. When
     * the list is empty, the client provides a best effort list according to {@link #setProtocolVersion}
     * @see HttpClientOptions#getAlpnVersions()
     */
    public List<HttpVersion> getAlpnVersions() {
        return delegate.getAlpnVersions();
    }

    public void setAlpnVersions(List<HttpVersion> alpnVersions) {
        delegate.setAlpnVersions(alpnVersions);
    }

    /**
     * @return {@code true} when an <i>h2c</i> connection is established using an HTTP/1.1 upgrade request, {@code false} when directly
     * @see HttpClientOptions#isHttp2ClearTextUpgrade()
     */
    public boolean isHttp2ClearTextUpgrade() {
        return delegate.isHttp2ClearTextUpgrade();
    }

    public void setHttp2ClearTextUpgrade(boolean value) {
        delegate.setHttp2ClearTextUpgrade(value);
    }

    /**
     * @return {@code true} when frame masking is skipped
     * @see HttpClientOptions#isSendUnmaskedFrames()
     */
    public boolean isSendUnmaskedFrames() {
        return delegate.isSendUnmaskedFrames();
    }

    public void setSendUnmaskedFrames(boolean sendUnmaskedFrames) {
        delegate.setSendUnmaskedFrames(sendUnmaskedFrames);
    }

    /**
     * @return the maximum number of redirection a request can follow
     * @see HttpClientOptions#getMaxRedirects()
     */
    public int getMaxRedirects() {
        return delegate.getMaxRedirects();
    }

    public void setMaxRedirects(int maxRedirects) {
        delegate.setMaxRedirects(maxRedirects);
    }

    /**
     * @return whether the client should always use SNI on TLS/SSL connections
     * @see HttpClientOptions#isForceSni()
     */
    public boolean isForceSni() {
        return delegate.isForceSni();
    }

    public void setForceSni(boolean forceSni) {
        delegate.setForceSni(forceSni);
    }

    /**
     * @return the initial buffer size for the HTTP decoder
     * @see HttpClientOptions#getDecoderInitialBufferSize()
     */
    public int getDecoderInitialBufferSize() {
        return delegate.getDecoderInitialBufferSize();
    }

    public void setDecoderInitialBufferSize(int decoderInitialBufferSize) {
        delegate.setDecoderInitialBufferSize(decoderInitialBufferSize);
    }

    /**
     * @return {@code true} when the WebSocket per-frame deflate compression extension will be offered
     * @see HttpClientOptions#getTryWebSocketDeflateFrameCompression
     * @deprecated use {@link #isTryWebSocketDeflateFrameCompression()}
     */
    @Deprecated
    public boolean isTryWebsocketDeflateFrameCompression() {
        return delegate.getTryWebSocketDeflateFrameCompression();
    }

    /**
     * @return {@code true} when the WebSocket per-frame deflate compression extension will be offered
     * @see HttpClientOptions#getTryWebSocketDeflateFrameCompression
     */
    public boolean isTryWebSocketDeflateFrameCompression() {
        return delegate.getTryWebSocketDeflateFrameCompression();
    }

    @Deprecated
    public void setTryUsePerFrameWebsocketCompression(boolean tryWebSocketDeflateFrameCompression) {
        delegate.setTryUsePerFrameWebSocketCompression(tryWebSocketDeflateFrameCompression);
    }

    public void setTryUsePerFrameWebSocketCompression(boolean tryWebSocketDeflateFrameCompression) {
        delegate.setTryUsePerFrameWebSocketCompression(tryWebSocketDeflateFrameCompression);
    }

    /**
     * @return {@code true} when the WebSocket per-message deflate compression extension will be offered
     * @see HttpClientOptions#getTryUsePerMessageWebSocketCompression()
     * @deprecated use {@link #isTryUsePerMessageWebSocketCompression()}
     */
    @Deprecated
    public boolean isTryUsePerMessageWebsocketCompression() {
        return delegate.getTryUsePerMessageWebSocketCompression();
    }

    /**
     * @return {@code true} when the WebSocket per-message deflate compression extension will be offered
     * @see HttpClientOptions#getTryUsePerMessageWebSocketCompression()
     */
    public boolean isTryUsePerMessageWebSocketCompression() {
        return delegate.getTryUsePerMessageWebSocketCompression();
    }

    @Deprecated
    public void setTryUsePerMessageWebsocketCompression(boolean tryUsePerMessageWebSocketCompression) {
        delegate.setTryUsePerMessageWebSocketCompression(tryUsePerMessageWebSocketCompression);
    }

    public void setTryUsePerMessageWebSocketCompression(boolean tryUsePerMessageWebSocketCompression) {
        delegate.setTryUsePerMessageWebSocketCompression(tryUsePerMessageWebSocketCompression);
    }

    /**
     * @return the WebSocket deflate compression level
     * @see HttpClientOptions#getWebSocketCompressionLevel()
     * @deprecated use {@link #getWebSocketCompressionLevel()}
     */
    @Deprecated
    public int getWebsocketCompressionLevel() {
        return delegate.getWebSocketCompressionLevel();
    }

    /**
     * @return the WebSocket deflate compression level
     * @see HttpClientOptions#getWebSocketCompressionLevel()
     */
    public int getWebSocketCompressionLevel() {
        return delegate.getWebSocketCompressionLevel();
    }

    @Deprecated
    public void setWebsocketCompressionLevel(int webSocketCompressionLevel) {
        delegate.setWebSocketCompressionLevel(webSocketCompressionLevel);
    }

    public void setWebSocketCompressionLevel(int webSocketCompressionLevel) {
        delegate.setWebSocketCompressionLevel(webSocketCompressionLevel);
    }

    /**
     * @return {@code true} when the {@code client_no_context_takeover} parameter for the WebSocket per-message
     * deflate compression extension will be offered
     * @see HttpClientOptions#getWebSocketCompressionAllowClientNoContext()
     * @deprecated use {@link #isWebSocketCompressionAllowClientNoContext()}
     */
    @Deprecated
    public boolean isWebsocketCompressionAllowClientNoContext() {
        return delegate.getWebSocketCompressionAllowClientNoContext();
    }

    /**
     * @return {@code true} when the {@code client_no_context_takeover} parameter for the WebSocket per-message
     * deflate compression extension will be offered
     * @see HttpClientOptions#getWebSocketCompressionAllowClientNoContext()
     */
    public boolean isWebSocketCompressionAllowClientNoContext() {
        return delegate.getWebSocketCompressionAllowClientNoContext();
    }

    @Deprecated
    public void setWebsocketCompressionAllowClientNoContext(boolean webSocketCompressionAllowClientNoContext) {
        delegate.setWebSocketCompressionAllowClientNoContext(webSocketCompressionAllowClientNoContext);
    }

    public void setWebSocketCompressionAllowClientNoContext(boolean webSocketCompressionAllowClientNoContext) {
        delegate.setWebSocketCompressionAllowClientNoContext(webSocketCompressionAllowClientNoContext);
    }

    /**
     * @return {@code true} when the {@code server_no_context_takeover} parameter for the WebSocket per-message
     * deflate compression extension will be offered
     * @see HttpClientOptions#getWebSocketCompressionRequestServerNoContext()
     * @deprecated use {@link #isWebSocketCompressionRequestServerNoContext()}
     */
    @Deprecated
    public boolean isWebsocketCompressionRequestServerNoContext() {
        return delegate.getWebSocketCompressionRequestServerNoContext();
    }

    /**
     * @return {@code true} when the {@code server_no_context_takeover} parameter for the WebSocket per-message
     * deflate compression extension will be offered
     * @see HttpClientOptions#getWebSocketCompressionRequestServerNoContext()
     */
    public boolean isWebSocketCompressionRequestServerNoContext() {
        return delegate.getWebSocketCompressionRequestServerNoContext();
    }

    @Deprecated
    public void setWebsocketCompressionRequestServerNoContext(boolean webSocketCompressionRequestServerNoContext) {
        delegate.setWebSocketCompressionRequestServerNoContext(webSocketCompressionRequestServerNoContext);
    }

    public void setWebSocketCompressionRequestServerNoContext(boolean webSocketCompressionRequestServerNoContext) {
        delegate.setWebSocketCompressionRequestServerNoContext(webSocketCompressionRequestServerNoContext);
    }
}
