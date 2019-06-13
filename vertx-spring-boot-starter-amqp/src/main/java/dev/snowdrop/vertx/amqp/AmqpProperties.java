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

    private boolean enabled = true;

    // AmqpClientOptions

    private String host = "localhost";

    private int port = 5672;

    private String username;

    private String password;

    private String containerId = UUID.randomUUID().toString();

    // ProtonClientOptions

    private Set<String> enabledSaslMechanisms = new LinkedHashSet<>();

    private int heartbeat;

    private int maxFrameSize;

    private String virtualHost;

    private String sniServerName;

    // NetClientOptions

    private int reconnectAttempts;

    private long reconnectInterval = 1000;

    private String hostnameVerificationAlgorithm = "";

    // ClientOptionsBase

    private int connectTimeout = 60000;

    private boolean trustAll;

    private String metricsName = "";

    private Proxy proxy = new Proxy();

    private String localAddress;

    // TCPSSLOptions

    private boolean tcpNoDelay = true;

    private boolean tcpKeepAlive;

    private int soLinger = -1;

    private boolean usePooledBuffers;

    private int idleTimeout;

    private TimeUnit idleTimeoutUnit = TimeUnit.SECONDS;

    private boolean ssl;

    private long sslHandshakeTimeout = 10L;

    private TimeUnit sslHandshakeTimeoutUnit = TimeUnit.SECONDS;

    private SslStore jksKeyStore = new SslStore();

    private SslStore jksTrustStore = new SslStore();

    private SslStore pfxKeyStore = new SslStore();

    private SslStore pfxTrustStore = new SslStore();

    private Set<String> enabledCipherSuites = new LinkedHashSet<>();

    private JdkSslEngine jdkSslEngine = new JdkSslEngine();

    private OpenSslEngine openSslEngine = new OpenSslEngine();

    private Set<String> enabledSecureTransportProtocols = new LinkedHashSet<>(Arrays.asList("TLSv1", "TLSv1.1", "TLSv1.2"));

    private boolean tcpFastOpen;

    private boolean tcpCork;

    private boolean tcpQuickAck;

    // NetworkOptions

    private int sendBufferSize = -1;

    private int receiveBufferSize = -1;

    private int trafficClass = -1;

    private boolean reuseAddress = true;

    private boolean logActivity;

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

        private boolean enabled;

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
