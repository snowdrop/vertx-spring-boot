package dev.snowdrop.vertx.mail;

import io.vertx.ext.mail.LoginOption;
import io.vertx.ext.mail.MailConfig;
import io.vertx.ext.mail.StartTLSOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = MailProperties.PROPERTIES_PREFIX)
public class MailProperties {

    static final String PROPERTIES_PREFIX = "vertx.mail";

    private boolean enabled = true;

    private MailConfig delegate = new MailConfig();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getHost() {
        return delegate.getHostname();
    }

    public void setHost(String host) {
        delegate.setHostname(host);
    }

    public int getPort() {
        return delegate.getPort();
    }

    public void setPort(int port) {
        delegate.setPort(port);
    }

    public String getStartTls() {
        if (delegate.getStarttls() == null) {
            return null;
        }

        return delegate.getStarttls().name();
    }

    public void setStartTls(String startTls) {
        if (startTls != null) {
            delegate.setStarttls(StartTLSOptions.valueOf(startTls.toUpperCase()));
        }
    }

    public String getLoginOption() {
        if (delegate.getLogin() == null) {
            return null;
        }

        return delegate.getLogin().name();
    }

    public void setLoginOption(String loginOption) {
        if (loginOption != null) {
            delegate.setLogin(LoginOption.valueOf(loginOption.toUpperCase()));
        }
    }

    public String getAuthMethods() {
        return delegate.getAuthMethods();
    }

    public void setAuthMethods(String authMethods) {
        delegate.setAuthMethods(authMethods);
    }

    public String getUsername() {
        return delegate.getUsername();
    }

    public void setUsername(String username) {
        delegate.setUsername(username);
    }

    public String getPassword() {
        return delegate.getPassword();
    }

    public void setPassword(String password) {
        delegate.setPassword(password);
    }

    public boolean isSsl() {
        return delegate.isSsl();
    }

    public void setSsl(boolean ssl) {
        delegate.setSsl(ssl);
    }

    public boolean isTrustAll() {
        return delegate.isTrustAll();
    }

    public void setTrustAll(boolean trustAll) {
        delegate.setTrustAll(trustAll);
    }

    public String getKeystore() {
        return delegate.getKeyStore();
    }

    public void setKeystore(String keystore) {
        delegate.setKeyStore(keystore);
    }

    public String getKeystorePassword() {
        return delegate.getKeyStorePassword();
    }

    public void setKeystorePassword(String keystorePassword) {
        delegate.setKeyStorePassword(keystorePassword);
    }

    public String getOwnHostName() {
        return delegate.getOwnHostname();
    }

    public void setOwnHostName(String ownHostName) {
        delegate.setOwnHostname(ownHostName);
    }

    public int getMaxPoolSize() {
        return delegate.getMaxPoolSize();
    }

    public void setMaxPoolSize(int maxPoolSize) {
        delegate.setMaxPoolSize(maxPoolSize);
    }

    public boolean isKeepAlive() {
        return delegate.isKeepAlive();
    }

    public void setKeepAlive(boolean keepAlive) {
        delegate.setKeepAlive(keepAlive);
    }

    public boolean isAllowRcptErrors() {
        return delegate.isAllowRcptErrors();
    }

    public void setAllowRcptErrors(boolean allowRcptErrors) {
        delegate.setAllowRcptErrors(allowRcptErrors);
    }

    public boolean isDisableEsmtp() {
        return delegate.isDisableEsmtp();
    }

    public void setDisableEsmtp(boolean disableEsmtp) {
        delegate.setDisableEsmtp(disableEsmtp);
    }

    MailConfig getMailConfig() {
        return delegate;
    }
}
