package dev.snowdrop.vertx.mail;

import java.util.Optional;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = MailProperties.PROPERTIES_PREFIX)
public class MailProperties {

    static final String PROPERTIES_PREFIX = "vertx.mail";

    private boolean enabled = true;

    private String host = "localhost"; // Setting to localhost because that's the default in MailConfig

    private String username;

    private String password;

    private String keystore;

    private String keystorePassword;

    private String authMethods;

    private String loginOption;

    private String startTls;

    private int port = 25;

    private boolean ssl;

    private boolean trustAll;

    private boolean keepAlive = true;

    private boolean esmtp = true;

    private boolean allowRcptErrors = true;

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

    public Optional<String> getUsername() {
        return Optional.ofNullable(username);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Optional<String> getPassword() {
        return Optional.ofNullable(password);
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Optional<String> getKeystore() {
        return Optional.ofNullable(keystore);
    }

    public void setKeystore(String keystore) {
        this.keystore = keystore;
    }

    public Optional<String> getKeystorePassword() {
        return Optional.ofNullable(keystorePassword);
    }

    public void setKeystorePassword(String keystorePassword) {
        this.keystorePassword = keystorePassword;
    }

    public Optional<String> getAuthMethods() {
        return Optional.ofNullable(authMethods);
    }

    public void setAuthMethods(String authMethods) {
        this.authMethods = authMethods;
    }

    public Optional<String> getLoginOption() {
        return Optional.ofNullable(loginOption);
    }

    public void setLoginOption(String loginOption) {
        this.loginOption = loginOption;
    }

    public Optional<String> getStartTls() {
        return Optional.ofNullable(startTls);
    }

    public void setStartTls(String startTls) {
        this.startTls = startTls;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isSsl() {
        return ssl;
    }

    public void setSsl(boolean ssl) {
        this.ssl = ssl;
    }

    public boolean isTrustAll() {
        return trustAll;
    }

    public void setTrustAll(boolean trustAll) {
        this.trustAll = trustAll;
    }

    public boolean isKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public boolean isEsmtp() {
        return esmtp;
    }

    public void setEsmtp(boolean esmtp) {
        this.esmtp = esmtp;
    }

    public boolean isAllowRcptErrors() {
        return allowRcptErrors;
    }

    public void setAllowRcptErrors(boolean allowRcptErrors) {
        this.allowRcptErrors = allowRcptErrors;
    }
}
