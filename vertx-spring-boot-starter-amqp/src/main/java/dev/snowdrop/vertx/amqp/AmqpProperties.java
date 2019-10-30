package dev.snowdrop.vertx.amqp;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = AmqpProperties.PROPERTIES_PREFIX)
public class AmqpProperties {

    static final String PROPERTIES_PREFIX = "vertx.amqp";

    /**
     * Enable AMQP starter.
     */
    private boolean enabled = true;

    // AmqpClientOptions

    /**
     * Broker host.
     * <p>
     * Default: localhost
     *
     * @see io.vertx.amqp.AmqpClientOptions#getHost()
     */
    private String host = "localhost";

    /**
     * Broker port.
     * <p>
     * Default: 5672
     *
     * @see io.vertx.amqp.AmqpClientOptions#getPort()
     */
    private int port = 5672;

    /**
     * Broker username.
     *
     * @see io.vertx.amqp.AmqpClientOptions#getUsername()
     */
    private String username;

    /**
     * Broker password.
     *
     * @see io.vertx.amqp.AmqpClientOptions#getPassword()
     */
    private String password;

    /**
     * Broker container id.
     *
     * @see io.vertx.amqp.AmqpClientOptions#getContainerId()
     */
    private String containerId = UUID.randomUUID().toString();

    // ProtonClientOptions

    /**
     * A mechanisms the client should be restricted to use.
     *
     * @see io.vertx.amqp.AmqpClientOptions#getEnabledSaslMechanisms()
     */
    private Set<String> enabledSaslMechanisms = new LinkedHashSet<>();

    /**
     * A heartbeat (in milliseconds) as maximum delay between sending frames for the remote peers.
     *
     * @see io.vertx.amqp.AmqpClientOptions#getHeartbeat()
     */
    private int heartbeat;

    /**
     * A maximum frame size for the connection.
     *
     * @see io.vertx.amqp.AmqpClientOptions#getMaxFrameSize()
     */
    private int maxFrameSize;

    /**
     * A virtual host name override for the connection AMQP Open frame and TLS SNI server name (if TLS is in use).
     *
     * @see io.vertx.amqp.AmqpClientOptions#getVirtualHost()
     */
    private String virtualHost;

    /**
     * A hostname override for TLS SNI Server Name.
     *
     * @see io.vertx.amqp.AmqpClientOptions#getSniServerName()
     */
    private String sniServerName;

    // NetClientOptions

    /**
     * A number of reconnect attempts.
     *
     * @see io.vertx.amqp.AmqpClientOptions#getReconnectAttempts()
     */
    private int reconnectAttempts;

    /**
     * A reconnect interval.
     *
     * @see io.vertx.amqp.AmqpClientOptions#getReconnectInterval()
     */
    private long reconnectInterval = 1000;

    /**
     * A hostname verification algorithm.
     *
     * @see io.vertx.amqp.AmqpClientOptions#getHostnameVerificationAlgorithm()
     */
    private String hostnameVerificationAlgorithm = "";

    // ClientOptionsBase

    /**
     * A connect timeout.
     *
     * @see io.vertx.amqp.AmqpClientOptions#getConnectTimeout()
     */
    private int connectTimeout = 60000;

    /**
     * Whether all server certificates should be trusted.
     *
     * @see io.vertx.amqp.AmqpClientOptions#isTrustAll()
     */
    private boolean trustAll;

    /**
     * A metrics name identifying the reported metrics.
     *
     * @see io.vertx.amqp.AmqpClientOptions#getMetricsName()
     */
    private String metricsName = "";

    /**
     * Connection proxy settings.
     */
    private Proxy proxy = new Proxy();

    /**
     * A local interface to bind for network connections.
     *
     * @see io.vertx.amqp.AmqpClientOptions#getLocalAddress()
     */
    private String localAddress;

    // TCPSSLOptions

    /**
     * Whether TCP no delay is enabled.
     *
     * @see io.vertx.amqp.AmqpClientOptions#isTcpNoDelay()
     */
    private boolean tcpNoDelay = true;

    /**
     * Whether TCP keep alive is enabled.
     *
     * @see io.vertx.amqp.AmqpClientOptions#isTcpKeepAlive()
     */
    private boolean tcpKeepAlive;

    /**
     * Whether SO_linger is enabled.
     *
     * @see io.vertx.amqp.AmqpClientOptions#getSoLinger()
     */
    private int soLinger = -1;

    private boolean usePooledBuffers;

    /**
     * An idle timeout, in time unit specified by {@link #idleTimeoutUnit}.
     *
     * @see io.vertx.amqp.AmqpClientOptions#getIdleTimeout()
     */
    private int idleTimeout;

    /**
     * An idle timeout unit.
     *
     * @see io.vertx.amqp.AmqpClientOptions#getIdleTimeoutUnit()
     */
    private TimeUnit idleTimeoutUnit = TimeUnit.SECONDS;

    /**
     * Whether SSL/TLS is enabled.
     *
     * @see io.vertx.amqp.AmqpClientOptions#isSsl()
     */
    private boolean ssl;

    /**
     * An SSL handshake timeout, in time unit specified by {@link #sslHandshakeTimeoutUnit}.
     *
     * @see io.vertx.amqp.AmqpClientOptions#getSslHandshakeTimeout()
     */
    private long sslHandshakeTimeout = 10L;

    /**
     * An SSL handshake timeout unit.
     *
     * @see io.vertx.amqp.AmqpClientOptions#getSslHandshakeTimeoutUnit() ()
     */
    private TimeUnit sslHandshakeTimeoutUnit = TimeUnit.SECONDS;

    /**
     * JKS key store properties.
     */
    private SslStore jksKeyStore = new SslStore();

    /**
     * JKS trust store properties.
     */
    private SslStore jksTrustStore = new SslStore();

    /**
     * JFX key store properties.
     */
    private SslStore pfxKeyStore = new SslStore();

    /**
     * PFX trust store properties.
     */
    private SslStore pfxTrustStore = new SslStore();

    /**
     * The enabled cipher suites
     *
     * @see io.vertx.amqp.AmqpClientOptions#getEnabledCipherSuites()
     */
    private Set<String> enabledCipherSuites = new LinkedHashSet<>();

    /**
     * JDK SSL engine properties.
     */
    private JdkSslEngine jdkSslEngine = new JdkSslEngine();

    /**
     * OpenSSL engine properties.
     */
    private OpenSslEngine openSslEngine = new OpenSslEngine();

    /**
     * The enabled SSL/TLS protocols.
     *
     * @see io.vertx.amqp.AmqpClientOptions#getEnabledSecureTransportProtocols()
     */
    private Set<String> enabledSecureTransportProtocols =
        new LinkedHashSet<>(Arrays.asList("TLSv1", "TLSv1.1", "TLSv1.2"));

    /**
     * Whether {@code TCP_FASTOPEN} option is enabled
     *
     * @see io.vertx.amqp.AmqpClientOptions#isTcpFastOpen()
     */
    private boolean tcpFastOpen;

    /**
     * Whether {@code TCP_CORK} option is enabled.
     *
     * @see io.vertx.amqp.AmqpClientOptions#isTcpCork()
     */
    private boolean tcpCork;

    /**
     * Whether {@code TCP_QUICKACK} option is enabled.
     *
     * @see io.vertx.amqp.AmqpClientOptions#isTcpQuickAck()
     */
    private boolean tcpQuickAck;

    // NetworkOptions

    /**
     * A TCP send buffer size in bytes.
     *
     * @see io.vertx.amqp.AmqpClientOptions#getSendBufferSize()
     */
    private int sendBufferSize = -1;

    /**
     * A TCP receive buffer size in bytes
     *
     * @see io.vertx.amqp.AmqpClientOptions#getReceiveBufferSize()
     */
    private int receiveBufferSize = -1;

    /**
     * A traffic class.
     *
     * @see io.vertx.amqp.AmqpClientOptions#getTrafficClass()
     */
    private int trafficClass = -1;

    /**
     * Whether address should be reused.
     *
     * @see io.vertx.amqp.AmqpClientOptions#isReuseAddress()
     */
    private boolean reuseAddress = true;

    /**
     * Whether network activity logging should be enabled.
     *
     * @see io.vertx.amqp.AmqpClientOptions#getLogActivity()
     */
    private boolean logActivity;

    /**
     * Whether port should be reused.
     *
     * @see io.vertx.amqp.AmqpClientOptions#isReusePort()
     */
    private boolean reusePort;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public Set<String> getEnabledSaslMechanisms() {
        return enabledSaslMechanisms;
    }

    public void setEnabledSaslMechanisms(Set<String> enabledSaslMechanisms) {
        if (enabledSaslMechanisms == null) {
            this.enabledSaslMechanisms = new LinkedHashSet<>();
        } else {
            this.enabledSaslMechanisms = enabledSaslMechanisms;
        }
    }

    public int getHeartbeat() {
        return heartbeat;
    }

    public void setHeartbeat(int heartbeat) {
        this.heartbeat = heartbeat;
    }

    public int getMaxFrameSize() {
        return maxFrameSize;
    }

    public void setMaxFrameSize(int maxFrameSize) {
        this.maxFrameSize = maxFrameSize;
    }

    public String getVirtualHost() {
        return virtualHost;
    }

    public void setVirtualHost(String virtualHost) {
        this.virtualHost = virtualHost;
    }

    public String getSniServerName() {
        return sniServerName;
    }

    public void setSniServerName(String sniServerName) {
        this.sniServerName = sniServerName;
    }

    public int getReconnectAttempts() {
        return reconnectAttempts;
    }

    public void setReconnectAttempts(int reconnectAttempts) {
        this.reconnectAttempts = reconnectAttempts;
    }

    public long getReconnectInterval() {
        return reconnectInterval;
    }

    public void setReconnectInterval(long reconnectInterval) {
        this.reconnectInterval = reconnectInterval;
    }

    public String getHostnameVerificationAlgorithm() {
        return hostnameVerificationAlgorithm;
    }

    public void setHostnameVerificationAlgorithm(String hostnameVerificationAlgorithm) {
        this.hostnameVerificationAlgorithm = hostnameVerificationAlgorithm;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public boolean isTrustAll() {
        return trustAll;
    }

    public void setTrustAll(boolean trustAll) {
        this.trustAll = trustAll;
    }

    public String getMetricsName() {
        return metricsName;
    }

    public void setMetricsName(String metricsName) {
        this.metricsName = metricsName;
    }

    public Proxy getProxy() {
        return proxy;
    }

    public String getLocalAddress() {
        return localAddress;
    }

    public void setLocalAddress(String localAddress) {
        this.localAddress = localAddress;
    }

    public boolean isTcpNoDelay() {
        return tcpNoDelay;
    }

    public void setTcpNoDelay(boolean tcpNoDelay) {
        this.tcpNoDelay = tcpNoDelay;
    }

    public boolean isTcpKeepAlive() {
        return tcpKeepAlive;
    }

    public void setTcpKeepAlive(boolean tcpKeepAlive) {
        this.tcpKeepAlive = tcpKeepAlive;
    }

    public int getSoLinger() {
        return soLinger;
    }

    public void setSoLinger(int soLinger) {
        this.soLinger = soLinger;
    }

    public boolean isUsePooledBuffers() {
        return usePooledBuffers;
    }

    public void setUsePooledBuffers(boolean usePooledBuffers) {
        this.usePooledBuffers = usePooledBuffers;
    }

    public int getIdleTimeout() {
        return idleTimeout;
    }

    public void setIdleTimeout(int idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    public TimeUnit getIdleTimeoutUnit() {
        return idleTimeoutUnit;
    }

    public void setIdleTimeoutUnit(TimeUnit idleTimeoutUnit) {
        this.idleTimeoutUnit = idleTimeoutUnit;
    }

    public boolean isSsl() {
        return ssl;
    }

    public void setSsl(boolean ssl) {
        this.ssl = ssl;
    }

    public long getSslHandshakeTimeout() {
        return sslHandshakeTimeout;
    }

    public void setSslHandshakeTimeout(long sslHandshakeTimeout) {
        this.sslHandshakeTimeout = sslHandshakeTimeout;
    }

    public TimeUnit getSslHandshakeTimeoutUnit() {
        return sslHandshakeTimeoutUnit;
    }

    public void setSslHandshakeTimeoutUnit(TimeUnit sslHandshakeTimeoutUnit) {
        this.sslHandshakeTimeoutUnit = sslHandshakeTimeoutUnit;
    }

    public SslStore getJksKeyStore() {
        return jksKeyStore;
    }

    public SslStore getJksTrustStore() {
        return jksTrustStore;
    }

    public SslStore getPfxKeyStore() {
        return pfxKeyStore;
    }

    public SslStore getPfxTrustStore() {
        return pfxTrustStore;
    }

    public Set<String> getEnabledCipherSuites() {
        return enabledCipherSuites;
    }

    public void setEnabledCipherSuites(Set<String> enabledCipherSuites) {
        if (enabledCipherSuites == null) {
            this.enabledCipherSuites = new LinkedHashSet<>();
        } else {
            this.enabledCipherSuites = enabledCipherSuites;
        }
    }

    public JdkSslEngine getJdkSslEngine() {
        return jdkSslEngine;
    }

    public OpenSslEngine getOpenSslEngine() {
        return openSslEngine;
    }

    public Set<String> getEnabledSecureTransportProtocols() {
        return enabledSecureTransportProtocols;
    }

    public void setEnabledSecureTransportProtocols(Set<String> enabledSecureTransportProtocols) {
        if (enabledSecureTransportProtocols == null) {
            this.enabledSecureTransportProtocols = new LinkedHashSet<>();
        } else {
            this.enabledSecureTransportProtocols = enabledSecureTransportProtocols;
        }
    }

    public boolean isTcpFastOpen() {
        return tcpFastOpen;
    }

    public void setTcpFastOpen(boolean tcpFastOpen) {
        this.tcpFastOpen = tcpFastOpen;
    }

    public boolean isTcpCork() {
        return tcpCork;
    }

    public void setTcpCork(boolean tcpCork) {
        this.tcpCork = tcpCork;
    }

    public boolean isTcpQuickAck() {
        return tcpQuickAck;
    }

    public void setTcpQuickAck(boolean tcpQuickAck) {
        this.tcpQuickAck = tcpQuickAck;
    }

    public int getSendBufferSize() {
        return sendBufferSize;
    }

    public void setSendBufferSize(int sendBufferSize) {
        this.sendBufferSize = sendBufferSize;
    }

    public int getReceiveBufferSize() {
        return receiveBufferSize;
    }

    public void setReceiveBufferSize(int receiveBufferSize) {
        this.receiveBufferSize = receiveBufferSize;
    }

    public int getTrafficClass() {
        return trafficClass;
    }

    public void setTrafficClass(int trafficClass) {
        this.trafficClass = trafficClass;
    }

    public boolean isReuseAddress() {
        return reuseAddress;
    }

    public void setReuseAddress(boolean reuseAddress) {
        this.reuseAddress = reuseAddress;
    }

    public boolean isLogActivity() {
        return logActivity;
    }

    public void setLogActivity(boolean logActivity) {
        this.logActivity = logActivity;
    }

    public boolean isReusePort() {
        return reusePort;
    }

    public void setReusePort(boolean reusePort) {
        this.reusePort = reusePort;
    }

    public static class Proxy {

        private boolean enabled;

        private String host = "localhost";

        private int port = 3128;

        private String username;

        private String password;

        private ProxyType type = ProxyType.HTTP;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public ProxyType getType() {
            return type;
        }

        public void setType(ProxyType type) {
            this.type = type;
        }
    }

    public static class SslStore {

        private boolean enabled;

        private String password;

        private String path;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }

    public static class OpenSslEngine {

        /**
         * Whether OpenSSL engine is enabled.
         */
        private boolean enabled;

        /**
         * Whether session cache is enabled in open SSL session server context.
         *
         * @see io.vertx.core.net.OpenSSLEngineOptions#isSessionCacheEnabled()
         */
        private boolean sessionCacheEnabled;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isSessionCacheEnabled() {
            return sessionCacheEnabled;
        }

        public void setSessionCacheEnabled(boolean sessionCacheEnabled) {
            this.sessionCacheEnabled = sessionCacheEnabled;
        }
    }

    public static class JdkSslEngine {

        /**
         * Whether JDK SSL engine is enabled.
         */
        private boolean enabled;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    public enum ProxyType {
        HTTP,
        SOCKS4,
        SOCKS5
    }
}
