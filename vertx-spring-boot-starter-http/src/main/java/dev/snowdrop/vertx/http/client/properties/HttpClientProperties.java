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
     * @see HttpClientOptions#getSendBufferSize()
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
     * @see HttpClientOptions#getReceiveBufferSize()
     * @return the receive buffer size
     */
    public int getReceiveBufferSize() {
        return delegate.getReceiveBufferSize();
    }

    public void setReceiveBufferSize(int receiveBufferSize) {
        delegate.setReceiveBufferSize(receiveBufferSize);
    }

    /**
     * @see HttpClientOptions#isReuseAddress()
     * @return  the value of reuse address
     */
    public boolean isReuseAddress() {
        return delegate.isReuseAddress();
    }

    public void setReuseAddress(boolean reuseAddress) {
        delegate.setReuseAddress(reuseAddress);
    }

    /**
     * @see HttpClientOptions#isReusePort()
     * @return  the value of reuse address - only supported by native transports
     */
    public boolean isReusePort() {
        return delegate.isReusePort();
    }

    public void setReusePort(boolean reusePort) {
        delegate.setReusePort(reusePort);
    }

    /**
     * @see HttpClientOptions#getTrafficClass()
     * @return  the value of traffic class
     */
    public int getTrafficClass() {
        return delegate.getTrafficClass();
    }

    public void setTrafficClass(int trafficClass) {
        delegate.setTrafficClass(trafficClass);
    }

    /**
     * @see HttpClientOptions#getLogActivity()
     * @return true when network activity logging is enabled
     */
    public boolean getLogActivity() {
        return delegate.getLogActivity();
    }

    public void setLogActivity(boolean logEnabled) {
        delegate.setLogActivity(logEnabled);
    }

    // Vert.x TCPSSLOptions

    /**
     * @see HttpClientOptions#isTcpNoDelay()
     * @return TCP no delay enabled ?
     */
    public boolean isTcpNoDelay() {
        return delegate.isTcpNoDelay();
    }

    public void setTcpNoDelay(boolean tcpNoDelay) {
        delegate.setTcpNoDelay(tcpNoDelay);
    }

    /**
     * @see HttpClientOptions#isTcpKeepAlive()
     * @return is TCP keep alive enabled?
     */
    public boolean isTcpKeepAlive() {
        return delegate.isTcpKeepAlive();
    }

    public void setTcpKeepAlive(boolean tcpKeepAlive) {
        delegate.setTcpKeepAlive(tcpKeepAlive);
    }

    /**
     * @see HttpClientOptions#getSoLinger()
     * @return is SO_linger enabled
     */
    public int getSoLinger() {
        return delegate.getSoLinger();
    }

    public void setSoLinger(int soLinger) {
        delegate.setSoLinger(soLinger);
    }

    /**
     * @see HttpClientOptions#getIdleTimeout()
     * @return the idle timeout, in time unit specified by {@link #getIdleTimeoutUnit()}.
     */
    public int getIdleTimeout() {
        return delegate.getIdleTimeout();
    }

    public void setIdleTimeout(int idleTimeout) {
        delegate.setIdleTimeout(idleTimeout);
    }

    /**
     * @see HttpClientOptions#getIdleTimeoutUnit()
     * @return the idle timeout unit.
     */
    public TimeUnit getIdleTimeoutUnit() {
        return delegate.getIdleTimeoutUnit();
    }

    public void setIdleTimeoutUnit(TimeUnit idleTimeoutUnit) {
        delegate.setIdleTimeoutUnit(idleTimeoutUnit);
    }

    /**
     * @see HttpClientOptions#isSsl()
     * @return is SSL/TLS enabled?
     */
    public boolean isSsl() {
        return delegate.isSsl();
    }

    public void setSsl(boolean ssl) {
        delegate.setSsl(ssl);
    }

    /**
     * @see HttpClientOptions#getEnabledCipherSuites()
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
     * @see HttpClientOptions#isUseAlpn()
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
     * @see HttpClientOptions#getEnabledSecureTransportProtocols()
     * @return the enabled protocols
     */
    public Set<String> getEnabledSecureTransportProtocols() {
        return delegate.getEnabledSecureTransportProtocols();
    }

    public void setEnabledSecureTransportProtocols(Set<String> enabledSecureTransportProtocols) {
        delegate.setEnabledSecureTransportProtocols(enabledSecureTransportProtocols);
    }

    /**
     * @see HttpClientOptions#isTcpFastOpen()
     * @return whether {@code TCP_FASTOPEN} option is enabled
     */
    public boolean isTcpFastOpen() {
        return delegate.isTcpFastOpen();
    }

    public void setTcpFastOpen(boolean tcpFastOpen) {
        delegate.setTcpFastOpen(tcpFastOpen);
    }

    /**
     * @see HttpClientOptions#isTcpCork()
     * @return whether {@code TCP_CORK} option is enabled
     */
    public boolean isTcpCork() {
        return delegate.isTcpCork();
    }

    public void setTcpCork(boolean tcpCork) {
        delegate.setTcpCork(tcpCork);
    }

    /**
     * @see HttpClientOptions#isTcpQuickAck()
     * @return whether {@code TCP_QUICKACK} option is enabled
     */
    public boolean isTcpQuickAck() {
        return delegate.isTcpQuickAck();
    }

    public void setTcpQuickAck(boolean tcpQuickAck) {
        delegate.setTcpQuickAck(tcpQuickAck);
    }

    // Vert.x ClientOptionsBase

    /**
     * @see HttpClientOptions#getConnectTimeout()
     * @return the value of connect timeout
     */
    public int getConnectTimeout() {
        return delegate.getConnectTimeout();
    }

    public void setConnectTimeout(int connectTimeout) {
        delegate.setConnectTimeout(connectTimeout);
    }

    /**
     * @see HttpClientOptions#isTrustAll()
     * @return true if all server certificates should be trusted
     */
    public boolean isTrustAll() {
        return delegate.isTrustAll();
    }

    public void setTrustAll(boolean trustAll) {
        delegate.setTrustAll(trustAll);
    }

    /**
     * @see HttpClientOptions#getMetricsName()
     * @return the metrics name identifying the reported metrics.
     */
    public String getMetricsName() {
        return delegate.getMetricsName();
    }

    public void setMetricsName(String metricsName) {
        delegate.setMetricsName(metricsName);
    }

    /**
     * @see HttpClientOptions#getLocalAddress()
     * @return the local interface to bind for network connections.
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
     * @see HttpClientOptions#isVerifyHost()
     * @return {@code true} if enabled
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
     * @see HttpClientOptions#getMaxPoolSize()
     * @return  the maximum pool size
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
     * @see HttpClientOptions#isKeepAlive()
     * @return {@code true} if enabled
     */
    public boolean isKeepAlive() {
        return delegate.isKeepAlive();
    }

    public void setKeepAlive(boolean keepAlive) {
        delegate.setKeepAlive(keepAlive);
    }

    /**
     * @see HttpClientOptions#getKeepAliveTimeout()
     * @return the keep alive timeout value in seconds for HTTP/1.x connections
     */
    public int getKeepAliveTimeout() {
        return delegate.getKeepAliveTimeout();
    }

    public void setKeepAliveTimeout(int keepAliveTimeout) {
        delegate.setKeepAliveTimeout(keepAliveTimeout);
    }

    /**
     * @see HttpClientOptions#getPipeliningLimit()
     * @return the limit of pending requests a pipe-lined HTTP/1 connection can send
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
     * @see HttpClientOptions#isPipelining()
     * @return {@code true} if pipe-lining is enabled
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
     * @see HttpClientOptions#getHttp2MaxPoolSize()
     * @return  the maximum pool size
     */
    public int getHttp2MaxPoolSize() {
        return delegate.getHttp2MaxPoolSize();
    }

    public void setHttp2MaxPoolSize(int max) {
        delegate.setHttp2MaxPoolSize(max);
    }

    /**
     * @see HttpClientOptions#getHttp2MultiplexingLimit()
     * @return the maximum number of concurrent streams for an HTTP/2 connection, {@code -1} means
     * the value sent by the server
     */
    public int getHttp2MultiplexingLimit() {
        return delegate.getHttp2MultiplexingLimit();
    }

    public void setHttp2MultiplexingLimit(int limit) {
        delegate.setHttp2MultiplexingLimit(limit);
    }

    /**
     * @see HttpClientOptions#getHttp2ConnectionWindowSize()
     * @return the default HTTP/2 connection window size
     */
    public int getHttp2ConnectionWindowSize() {
        return delegate.getHttp2ConnectionWindowSize();
    }

    public void setHttp2ConnectionWindowSize(int http2ConnectionWindowSize) {
        delegate.setHttp2ConnectionWindowSize(http2ConnectionWindowSize);
    }

    /**
     * @see HttpClientOptions#getHttp2KeepAliveTimeout()
     * @return the keep alive timeout value in seconds for HTTP/2 connections
     */
    public int getHttp2KeepAliveTimeout() {
        return delegate.getHttp2KeepAliveTimeout();
    }

    public void setHttp2KeepAliveTimeout(int keepAliveTimeout) {
        delegate.setHttp2KeepAliveTimeout(keepAliveTimeout);
    }

    /**
     * @see HttpClientOptions#getPoolCleanerPeriod()
     * @return the connection pool cleaner period in ms.
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
     * @see HttpClientOptions#isTryUseCompression()
     * @return {@code true} if enabled
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
     * @see HttpClientOptions#getMaxWebsocketFrameSize()
     * @return  the max WebSocket frame size
     */
    public int getMaxWebsocketFrameSize() {
        return delegate.getMaxWebsocketFrameSize();
    }

    public void setMaxWebsocketFrameSize(int maxWebsocketFrameSize) {
        delegate.setMaxWebsocketFrameSize(maxWebsocketFrameSize);
    }

    /**
     * Get the maximum WebSocket message size to use
     *
     * @see HttpClientOptions#getMaxWebsocketMessageSize()
     * @return  the max WebSocket message size
     */
    public int getMaxWebsocketMessageSize() {
        return delegate.getMaxWebsocketMessageSize();
    }

    public void setMaxWebsocketMessageSize(int maxWebsocketMessageSize) {
        delegate.setMaxWebsocketMessageSize(maxWebsocketMessageSize);
    }

    /**
     * Get the default host name to be used by this client in requests if none is provided when making the request.
     *
     * @see HttpClientOptions#getDefaultHost()
     * @return  the default host name
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
     * @see HttpClientOptions#getDefaultPort()
     * @return  the default port
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
     * @see HttpClientOptions#getProtocolVersion()
     * @return the protocol version
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
     * @see HttpClientOptions#getMaxChunkSize()
     * @return the maximum HTTP chunk size
     */
    public int getMaxChunkSize() {
        return delegate.getMaxChunkSize();
    }

    public void setMaxChunkSize(int maxChunkSize) {
        delegate.setMaxChunkSize(maxChunkSize);
    }

    /**
     * @see HttpClientOptions#getMaxInitialLineLength()
     * @return the maximum length of the initial line for HTTP/1.x (e.g. {@code "GET / HTTP/1.0"})
     */
    public int getMaxInitialLineLength() {
        return delegate.getMaxInitialLineLength();
    }

    public void setMaxInitialLineLength(int maxInitialLineLength) {
        delegate.setMaxInitialLineLength(maxInitialLineLength);
    }

    /**
     * @see HttpClientOptions#getMaxHeaderSize()
     * @return Returns the maximum length of all headers for HTTP/1.x
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
     * @see HttpClientOptions#getMaxWaitQueueSize()
     * @return the maximum wait queue size
     */
    public int getMaxWaitQueueSize() {
        return delegate.getMaxWaitQueueSize();
    }

    public void setMaxWaitQueueSize(int maxWaitQueueSize) {
        delegate.setMaxWaitQueueSize(maxWaitQueueSize);
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

    /**
     * @see HttpClientOptions#getAlpnVersions()
     * @return the list of protocol versions to provide during the Application-Layer Protocol Negotiation. When
     * the list is empty, the client provides a best effort list according to {@link #setProtocolVersion}
     */
    public List<HttpVersion> getAlpnVersions() {
        return delegate.getAlpnVersions();
    }

    public void setAlpnVersions(List<HttpVersion> alpnVersions) {
        delegate.setAlpnVersions(alpnVersions);
    }

    /**
     * @see HttpClientOptions#isHttp2ClearTextUpgrade()
     * @return {@code true} when an <i>h2c</i> connection is established using an HTTP/1.1 upgrade request, {@code false} when directly
     */
    public boolean isHttp2ClearTextUpgrade() {
        return delegate.isHttp2ClearTextUpgrade();
    }

    public void setHttp2ClearTextUpgrade(boolean value) {
        delegate.setHttp2ClearTextUpgrade(value);
    }

    /**
     * @see HttpClientOptions#isSendUnmaskedFrames()
     * @return {@code true} when frame masking is skipped
     */
    public boolean isSendUnmaskedFrames() {
        return delegate.isSendUnmaskedFrames();
    }

    public void setSendUnmaskedFrames(boolean sendUnmaskedFrames) {
        delegate.setSendUnmaskedFrames(sendUnmaskedFrames);
    }

    /**
     * @see HttpClientOptions#getMaxRedirects()
     * @return the maximum number of redirection a request can follow
     */
    public int getMaxRedirects() {
        return delegate.getMaxRedirects();
    }

    public void setMaxRedirects(int maxRedirects) {
        delegate.setMaxRedirects(maxRedirects);
    }

    /**
     * @see HttpClientOptions#isForceSni()
     * @return whether the client should always use SNI on TLS/SSL connections
     */
    public boolean isForceSni() {
        return delegate.isForceSni();
    }

    public void setForceSni(boolean forceSni) {
        delegate.setForceSni(forceSni);
    }

    /**
     * @see HttpClientOptions#getDecoderInitialBufferSize()
     * @return the initial buffer size for the HTTP decoder
     */
    public int getDecoderInitialBufferSize() {
        return delegate.getDecoderInitialBufferSize();
    }

    public void setDecoderInitialBufferSize(int decoderInitialBufferSize) {
        delegate.setDecoderInitialBufferSize(decoderInitialBufferSize);
    }

    /**
     * @see HttpClientOptions#getTryWebsocketDeflateFrameCompression
     * @return {@code true} when the WebSocket per-frame deflate compression extension will be offered
     */
    public boolean isTryWebsocketDeflateFrameCompression() {
        return delegate.getTryWebsocketDeflateFrameCompression();
    }

    public void setTryUsePerFrameWebsocketCompression(boolean tryWebsocketDeflateFrameCompression) {
        delegate.setTryUsePerFrameWebsocketCompression(tryWebsocketDeflateFrameCompression);
    }

    /**
     * @see HttpClientOptions#getTryUsePerMessageWebsocketCompression()
     * @return {@code true} when the WebSocket per-message deflate compression extension will be offered
     */
    public boolean isTryUsePerMessageWebsocketCompression() {
        return delegate.getTryUsePerMessageWebsocketCompression();
    }

    public void setTryUsePerMessageWebsocketCompression(boolean tryUsePerMessageWebsocketCompression) {
        delegate.setTryUsePerMessageWebsocketCompression(tryUsePerMessageWebsocketCompression);
    }

    /**
     * @see HttpClientOptions#getWebsocketCompressionLevel()
     * @return the Websocket deflate compression level
     */
    public int getWebsocketCompressionLevel() {
        return delegate.getWebsocketCompressionLevel();
    }

    public void setWebsocketCompressionLevel(int websocketCompressionLevel) {
        delegate.setWebsocketCompressionLevel(websocketCompressionLevel);
    }

    /**
     * @see HttpClientOptions#getWebsocketCompressionAllowClientNoContext()
     * @return {@code true} when the {@code client_no_context_takeover} parameter for the WebSocket per-message
     * deflate compression extension will be offered
     */
    public boolean isWebsocketCompressionAllowClientNoContext() {
        return delegate.getWebsocketCompressionAllowClientNoContext();
    }

    public void setWebsocketCompressionAllowClientNoContext(boolean websocketCompressionAllowClientNoContext) {
        delegate.setWebsocketCompressionAllowClientNoContext(websocketCompressionAllowClientNoContext);
    }

    /**
     * @see HttpClientOptions#getWebsocketCompressionRequestServerNoContext()
     * @return {@code true} when the {@code server_no_context_takeover} parameter for the Websocket per-message
     * deflate compression extension will be offered
     */
    public boolean isWebsocketCompressionRequestServerNoContext() {
        return delegate.getWebsocketCompressionRequestServerNoContext();
    }

    public void setWebsocketCompressionRequestServerNoContext(boolean websocketCompressionRequestServerNoContext) {
        delegate.setWebsocketCompressionRequestServerNoContext(websocketCompressionRequestServerNoContext);
    }
}
