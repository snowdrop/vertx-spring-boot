package me.snowdrop.vertx.core;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.vertx.core.VertxOptions;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.dns.AddressResolverOptions;
import io.vertx.core.file.FileSystemOptions;
import io.vertx.core.metrics.MetricsOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = VertxProperties.PROPERTIES_PREFIX)
public class VertxProperties {

    static final String PROPERTIES_PREFIX = "vertx.core";

    private int eventLoopPoolSize = VertxOptions.DEFAULT_EVENT_LOOP_POOL_SIZE;

    private int workerPoolSize = VertxOptions.DEFAULT_WORKER_POOL_SIZE;

    private int internalBlockingPoolSize = VertxOptions.DEFAULT_INTERNAL_BLOCKING_POOL_SIZE;

    private long blockedThreadCheckInterval = VertxOptions.DEFAULT_BLOCKED_THREAD_CHECK_INTERVAL;

    private long maxEventLoopExecuteTime = VertxOptions.DEFAULT_MAX_EVENT_LOOP_EXECUTE_TIME;

    private long maxWorkerExecuteTime = VertxOptions.DEFAULT_MAX_WORKER_EXECUTE_TIME;

    private boolean haEnabled = VertxOptions.DEFAULT_HA_ENABLED;

    private int quorumSize = VertxOptions.DEFAULT_QUORUM_SIZE;

    private String haGroup = VertxOptions.DEFAULT_HA_GROUP;

    private long warningExceptionTime = TimeUnit.SECONDS.toNanos(5);

    private boolean preferNativeTransport = VertxOptions.DEFAULT_PREFER_NATIVE_TRANSPORT;

    private TimeUnit maxEventLoopExecuteTimeUnit = VertxOptions.DEFAULT_MAX_EVENT_LOOP_EXECUTE_TIME_UNIT;

    private TimeUnit maxWorkerExecuteTimeUnit = VertxOptions.DEFAULT_MAX_WORKER_EXECUTE_TIME_UNIT;

    private TimeUnit warningExceptionTimeUnit = VertxOptions.DEFAULT_WARNING_EXCEPTION_TIME_UNIT;

    private TimeUnit blockedThreadCheckIntervalUnit = VertxOptions.DEFAULT_BLOCKED_THREAD_CHECK_INTERVAL_UNIT;

    private boolean metricsEnabled = MetricsOptions.DEFAULT_METRICS_ENABLED;

    private FileSystem fileSystem = new FileSystem();

    private AddressResolver addressResolver = new AddressResolver();

    public VertxOptions toVertxOptions() {
        VertxOptions vertxOptions = new VertxOptions();
        vertxOptions.setEventLoopPoolSize(eventLoopPoolSize);
        vertxOptions.setWorkerPoolSize(workerPoolSize);
        vertxOptions.setInternalBlockingPoolSize(internalBlockingPoolSize);
        vertxOptions.setBlockedThreadCheckInterval(blockedThreadCheckInterval);
        vertxOptions.setMaxEventLoopExecuteTime(maxEventLoopExecuteTime);
        vertxOptions.setMaxWorkerExecuteTime(maxWorkerExecuteTime);
        vertxOptions.setHAEnabled(haEnabled);
        vertxOptions.setQuorumSize(quorumSize);
        vertxOptions.setHAGroup(haGroup);
        vertxOptions.setWarningExceptionTime(warningExceptionTime);
        vertxOptions.setPreferNativeTransport(preferNativeTransport);
        vertxOptions.setMaxEventLoopExecuteTimeUnit(maxEventLoopExecuteTimeUnit);
        vertxOptions.setMaxWorkerExecuteTimeUnit(maxWorkerExecuteTimeUnit);
        vertxOptions.setWarningExceptionTimeUnit(warningExceptionTimeUnit);
        vertxOptions.setBlockedThreadCheckIntervalUnit(blockedThreadCheckIntervalUnit);

        MetricsOptions metricsOptions = new MetricsOptions();
        metricsOptions.setEnabled(metricsEnabled);
        vertxOptions.setMetricsOptions(metricsOptions);

        FileSystemOptions fileSystemOptions = new FileSystemOptions();
        fileSystemOptions.setClassPathResolvingEnabled(fileSystem.isClassPathResolvingEnabled());
        fileSystemOptions.setFileCachingEnabled(fileSystem.isFileCachingEnabled());
        vertxOptions.setFileSystemOptions(fileSystemOptions);

        AddressResolverOptions addressResolverOptions = new AddressResolverOptions();
        addressResolverOptions.setHostsPath(addressResolver.getHostsPath());
        addressResolverOptions.setHostsValue(addressResolver.getHostsValue());
        addressResolverOptions.setServers(addressResolver.getServers());
        addressResolverOptions.setOptResourceEnabled(addressResolver.isOptResourceEnabled());
        addressResolverOptions.setCacheMinTimeToLive(addressResolver.getCacheMinTimeToLive());
        addressResolverOptions.setCacheMaxTimeToLive(addressResolver.getCacheMaxTimeToLive());
        addressResolverOptions.setCacheNegativeTimeToLive(addressResolver.getCacheNegativeTimeToLive());
        addressResolverOptions.setQueryTimeout(addressResolver.getQueryTimeout());
        addressResolverOptions.setMaxQueries(addressResolver.getMaxQueries());
        addressResolverOptions.setRdFlag(addressResolver.isRdFlag());
        addressResolverOptions.setSearchDomains(addressResolver.getSearchDomains());
        addressResolverOptions.setNdots(addressResolver.getNdots());
        addressResolverOptions.setRotateServers(addressResolver.isRotateServers());
        vertxOptions.setAddressResolverOptions(addressResolverOptions);

        return vertxOptions;
    }

    public int getEventLoopPoolSize() {
        return eventLoopPoolSize;
    }

    public void setEventLoopPoolSize(int eventLoopPoolSize) {
        this.eventLoopPoolSize = eventLoopPoolSize;
    }

    public int getWorkerPoolSize() {
        return workerPoolSize;
    }

    public void setWorkerPoolSize(int workerPoolSize) {
        this.workerPoolSize = workerPoolSize;
    }

    public int getInternalBlockingPoolSize() {
        return internalBlockingPoolSize;
    }

    public void setInternalBlockingPoolSize(int internalBlockingPoolSize) {
        this.internalBlockingPoolSize = internalBlockingPoolSize;
    }

    public long getBlockedThreadCheckInterval() {
        return blockedThreadCheckInterval;
    }

    public void setBlockedThreadCheckInterval(long blockedThreadCheckInterval) {
        this.blockedThreadCheckInterval = blockedThreadCheckInterval;
    }

    public long getMaxEventLoopExecuteTime() {
        return maxEventLoopExecuteTime;
    }

    public void setMaxEventLoopExecuteTime(long maxEventLoopExecuteTime) {
        this.maxEventLoopExecuteTime = maxEventLoopExecuteTime;
    }

    public long getMaxWorkerExecuteTime() {
        return maxWorkerExecuteTime;
    }

    public void setMaxWorkerExecuteTime(long maxWorkerExecuteTime) {
        this.maxWorkerExecuteTime = maxWorkerExecuteTime;
    }

    public boolean isHaEnabled() {
        return haEnabled;
    }

    public void setHaEnabled(boolean haEnabled) {
        this.haEnabled = haEnabled;
    }

    public int getQuorumSize() {
        return quorumSize;
    }

    public void setQuorumSize(int quorumSize) {
        this.quorumSize = quorumSize;
    }

    public String getHaGroup() {
        return haGroup;
    }

    public void setHaGroup(String haGroup) {
        this.haGroup = haGroup;
    }

    public long getWarningExceptionTime() {
        return warningExceptionTime;
    }

    public void setWarningExceptionTime(long warningExceptionTime) {
        this.warningExceptionTime = warningExceptionTime;
    }

    public boolean isPreferNativeTransport() {
        return preferNativeTransport;
    }

    public void setPreferNativeTransport(boolean preferNativeTransport) {
        this.preferNativeTransport = preferNativeTransport;
    }

    public TimeUnit getMaxEventLoopExecuteTimeUnit() {
        return maxEventLoopExecuteTimeUnit;
    }

    public void setMaxEventLoopExecuteTimeUnit(TimeUnit maxEventLoopExecuteTimeUnit) {
        this.maxEventLoopExecuteTimeUnit = maxEventLoopExecuteTimeUnit;
    }

    public TimeUnit getMaxWorkerExecuteTimeUnit() {
        return maxWorkerExecuteTimeUnit;
    }

    public void setMaxWorkerExecuteTimeUnit(TimeUnit maxWorkerExecuteTimeUnit) {
        this.maxWorkerExecuteTimeUnit = maxWorkerExecuteTimeUnit;
    }

    public TimeUnit getWarningExceptionTimeUnit() {
        return warningExceptionTimeUnit;
    }

    public void setWarningExceptionTimeUnit(TimeUnit warningExceptionTimeUnit) {
        this.warningExceptionTimeUnit = warningExceptionTimeUnit;
    }

    public TimeUnit getBlockedThreadCheckIntervalUnit() {
        return blockedThreadCheckIntervalUnit;
    }

    public void setBlockedThreadCheckIntervalUnit(TimeUnit blockedThreadCheckIntervalUnit) {
        this.blockedThreadCheckIntervalUnit = blockedThreadCheckIntervalUnit;
    }

    public boolean isMetricsEnabled() {
        return metricsEnabled;
    }

    public void setMetricsEnabled(boolean metricsEnabled) {
        this.metricsEnabled = metricsEnabled;
    }

    public FileSystem getFileSystem() {
        return fileSystem;
    }

    public AddressResolver getAddressResolver() {
        return addressResolver;
    }

    public static class FileSystem {

        private boolean classPathResolvingEnabled = FileSystemOptions.DEFAULT_CLASS_PATH_RESOLVING_ENABLED;

        private boolean fileCachingEnabled = FileSystemOptions.DEFAULT_FILE_CACHING_ENABLED;

        public boolean isClassPathResolvingEnabled() {
            return classPathResolvingEnabled;
        }

        public void setClassPathResolvingEnabled(boolean classPathResolvingEnabled) {
            this.classPathResolvingEnabled = classPathResolvingEnabled;
        }

        public boolean isFileCachingEnabled() {
            return fileCachingEnabled;
        }

        public void setFileCachingEnabled(boolean fileCachingEnabled) {
            this.fileCachingEnabled = fileCachingEnabled;
        }
    }

    public static class AddressResolver {

        private String hostsPath;

        private Buffer hostsValue;

        private List<String> servers = AddressResolverOptions.DEFAULT_SERVERS;

        private boolean optResourceEnabled = AddressResolverOptions.DEFAULT_OPT_RESOURCE_ENABLED;

        private int cacheMinTimeToLive = AddressResolverOptions.DEFAULT_CACHE_MIN_TIME_TO_LIVE;

        private int cacheMaxTimeToLive = AddressResolverOptions.DEFAULT_CACHE_MAX_TIME_TO_LIVE;

        private int cacheNegativeTimeToLive = AddressResolverOptions.DEFAULT_CACHE_NEGATIVE_TIME_TO_LIVE;

        private long queryTimeout = AddressResolverOptions.DEFAULT_QUERY_TIMEOUT;

        private int maxQueries = AddressResolverOptions.DEFAULT_MAX_QUERIES;

        private boolean rdFlag = AddressResolverOptions.DEFAULT_RD_FLAG;

        private List<String> searchDomains = AddressResolverOptions.DEFAULT_SEACH_DOMAINS;

        private int ndots = AddressResolverOptions.DEFAULT_NDOTS;

        private boolean rotateServers = AddressResolverOptions.DEFAULT_ROTATE_SERVERS;

        public String getHostsPath() {
            return hostsPath;
        }

        public void setHostsPath(String hostsPath) {
            this.hostsPath = hostsPath;
        }

        public Buffer getHostsValue() {
            return hostsValue;
        }

        public void setHostsValue(Buffer hostsValue) {
            this.hostsValue = hostsValue;
        }

        public List<String> getServers() {
            return servers;
        }

        public void setServers(List<String> servers) {
            this.servers = servers;
        }

        public boolean isOptResourceEnabled() {
            return optResourceEnabled;
        }

        public void setOptResourceEnabled(boolean optResourceEnabled) {
            this.optResourceEnabled = optResourceEnabled;
        }

        public int getCacheMinTimeToLive() {
            return cacheMinTimeToLive;
        }

        public void setCacheMinTimeToLive(int cacheMinTimeToLive) {
            this.cacheMinTimeToLive = cacheMinTimeToLive;
        }

        public int getCacheMaxTimeToLive() {
            return cacheMaxTimeToLive;
        }

        public void setCacheMaxTimeToLive(int cacheMaxTimeToLive) {
            this.cacheMaxTimeToLive = cacheMaxTimeToLive;
        }

        public int getCacheNegativeTimeToLive() {
            return cacheNegativeTimeToLive;
        }

        public void setCacheNegativeTimeToLive(int cacheNegativeTimeToLive) {
            this.cacheNegativeTimeToLive = cacheNegativeTimeToLive;
        }

        public long getQueryTimeout() {
            return queryTimeout;
        }

        public void setQueryTimeout(long queryTimeout) {
            this.queryTimeout = queryTimeout;
        }

        public int getMaxQueries() {
            return maxQueries;
        }

        public void setMaxQueries(int maxQueries) {
            this.maxQueries = maxQueries;
        }

        public boolean isRdFlag() {
            return rdFlag;
        }

        public void setRdFlag(boolean rdFlag) {
            this.rdFlag = rdFlag;
        }

        public List<String> getSearchDomains() {
            return searchDomains;
        }

        public void setSearchDomains(List<String> searchDomains) {
            this.searchDomains = searchDomains;
        }

        public int getNdots() {
            return ndots;
        }

        public void setNdots(int ndots) {
            this.ndots = ndots;
        }

        public boolean isRotateServers() {
            return rotateServers;
        }

        public void setRotateServers(boolean rotateServers) {
            this.rotateServers = rotateServers;
        }
    }

}
