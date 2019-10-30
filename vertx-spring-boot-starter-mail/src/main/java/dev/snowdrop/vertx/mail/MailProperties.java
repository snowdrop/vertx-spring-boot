package dev.snowdrop.vertx.mail;

import io.vertx.ext.mail.LoginOption;
import io.vertx.ext.mail.MailConfig;
import io.vertx.ext.mail.StartTLSOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = MailProperties.PROPERTIES_PREFIX)
class MailProperties {

    static final String PROPERTIES_PREFIX = "vertx.mail";

    private boolean enabled = true;

    private MailConfig delegate = new MailConfig();

    /**
     * @return whether mail starter is enabled.
     */
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Get the hostname of the mail server.
     *
     * @return hostname
     * @see MailConfig#getHostname()
     */
    public String getHost() {
        return delegate.getHostname();
    }

    public void setHost(String host) {
        delegate.setHostname(host);
    }

    /**
     * Get the port of the mail server.
     *
     * @return port
     * @see MailConfig#getPort()
     */
    public int getPort() {
        return delegate.getPort();
    }

    public void setPort(int port) {
        delegate.setPort(port);
    }

    /**
     * Get security (TLS) options. Could be DISABLED, OPTIONAL or REQUIRED.
     *
     * @return the security options
     * @see MailConfig#getStarttls()
     */
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

    /**
     * Get login options. Could be DISABLED, NONE, REQUIRED or XOAUTH2.
     *
     * @return the login options
     * @see MailConfig#getLogin()
     */
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

    /**
     * Get string of allowed auth methods, if set only these methods will be used
     * if the server supports them. If null or empty all supported methods may be
     * used
     *
     * @return the auth methods
     * @see MailConfig#getAuthMethods()
     */
    public String getAuthMethods() {
        return delegate.getAuthMethods();
    }

    public void setAuthMethods(String authMethods) {
        delegate.setAuthMethods(authMethods);
    }

    /**
     * Get mail server username
     *
     * @return username
     * @see MailConfig#getUsername()
     */
    public String getUsername() {
        return delegate.getUsername();
    }

    public void setUsername(String username) {
        delegate.setUsername(username);
    }

    /**
     * Get mail server password
     *
     * @return password
     * @see MailConfig#getPassword()
     */
    public String getPassword() {
        return delegate.getPassword();
    }

    public void setPassword(String password) {
        delegate.setPassword(password);
    }

    /**
     * @return whether ssl is used on connect
     * @see MailConfig#isSsl()
     */
    public boolean isSsl() {
        return delegate.isSsl();
    }

    public void setSsl(boolean ssl) {
        delegate.setSsl(ssl);
    }

    /**
     * @return whether to trust all certificates on ssl connect
     * @see MailConfig#isTrustAll()
     */
    public boolean isTrustAll() {
        return delegate.isTrustAll();
    }

    public void setTrustAll(boolean trustAll) {
        delegate.setTrustAll(trustAll);
    }

    /**
     * Get a key store file name to be used when opening SMTP connections.
     *
     * @return key store
     * @see MailConfig#getKeyStore()
     */
    public String getKeystore() {
        return delegate.getKeyStore();
    }

    public void setKeystore(String keystore) {
        delegate.setKeyStore(keystore);
    }

    /**
     * Get a key store password to be used when opening SMTP connections.
     *
     * @return a key store password
     * @see MailConfig#getKeyStorePassword()
     */
    public String getKeystorePassword() {
        return delegate.getKeyStorePassword();
    }

    public void setKeystorePassword(String keystorePassword) {
        delegate.setKeyStorePassword(keystorePassword);
    }

    /**
     * Get a hostname to be used for HELO/EHLO and the Message-ID.
     *
     * @return my own hostname
     * @see MailConfig#getOwnHostname()
     */
    public String getOwnHostName() {
        return delegate.getOwnHostname();
    }

    public void setOwnHostName(String ownHostName) {
        delegate.setOwnHostname(ownHostName);
    }

    /**
     * Get the max allowed number of open connections to the mail server. if not set, the default is 10.
     *
     * @return max pool size value
     * @see MailConfig#getMaxPoolSize()
     */
    public int getMaxPoolSize() {
        return delegate.getMaxPoolSize();
    }

    public void setMaxPoolSize(int maxPoolSize) {
        delegate.setMaxPoolSize(maxPoolSize);
    }

    /**
     * Whether connection pool is enabled.
     * Default: true.
     * If the connection pooling is disabled, the max number of sockets is enforced nevertheless.
     *
     * @return keep alive value
     * @see MailConfig#isKeepAlive()
     */
    public boolean isKeepAlive() {
        return delegate.isKeepAlive();
    }

    public void setKeepAlive(boolean keepAlive) {
        delegate.setKeepAlive(keepAlive);
    }

    /**
     * Whether sending allows rcpt errors (default is false).
     * If true, the mail will be sent to the recipients that the server accepted, if any.
     *
     * @return whether sending allows rcpt errors
     * @see MailConfig#isAllowRcptErrors()
     */
    public boolean isAllowRcptErrors() {
        return delegate.isAllowRcptErrors();
    }

    public void setAllowRcptErrors(boolean allowRcptErrors) {
        delegate.setAllowRcptErrors(allowRcptErrors);
    }

    /**
     * Whether ESMTP should be tried as first command (EHLO) (default is true)
     * <p>
     * rfc 1869 states that clients should always attempt EHLO as first command to determine if ESMTP
     * is supported, if this returns an error code, HELO is tried to use old SMTP.
     * If there is a server that does not support EHLO and does not give an error code back, the connection
     * should be closed and retried with HELO. We do not do that and rather support turning off ESMTP with a
     * setting. The odds of this actually happening are very small since the client will not connect to arbitrary
     * smtp hosts on the internet. Since the client knows that is connects to a host that doesn't support ESMTP/EHLO
     * in that way, the property has to be set to false.
     *
     * @return Wwhether ESMTP should be tried as first command
     * @see MailConfig#isDisableEsmtp()
     */
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
