package dev.snowdrop.vertx.http.it;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import dev.snowdrop.vertx.http.client.VertxWebSocketClient;
import dev.snowdrop.vertx.http.client.VertxClientHttpConnector;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;

public class TestBase {

    protected static final String BASE_URL = "http://localhost:8080";

    protected static final String SSL_BASE_URL = "https://localhost:8080";

    protected static final String WS_BASE_URL = "ws://localhost:8080";

    @Rule
    public TestRule watcher = new TestNameLogger();

    private ConfigurableApplicationContext context;

    /**
     * Start a server with default properties and provided set of configuration classes.
     */
    protected void startServer(Class<?>... sources) {
        startServer(new Properties(), sources);
    }

    /**
     * Start a server with user properties merged with default properties and provided set of configuration classes.
     */
    protected void startServer(Properties userProperties, Class<?>... sources) {
        if (context != null) {
            throw new RuntimeException("Server is already running");
        }

        Properties properties = new Properties(defaultProperties());
        properties.putAll(userProperties);

        context = new SpringApplicationBuilder(TestApplication.class)
            .sources(sources)
            .web(WebApplicationType.REACTIVE)
            .properties(properties)
            .run();
    }

    /**
     * Start a server with default properties, provided set of configuration classes and disabled spring security.
     */
    protected void startServerWithoutSecurity(Class<?>... sources) {
        startServerWithoutSecurity(new Properties(), sources);
    }

    /**
     * Start a server with user properties merged with default properties, provided set of configuration classes and
     * disabled spring security.
     */
    protected void startServerWithoutSecurity(Properties userProperties, Class<?>... sources) {
        List<Class<?>> sourcesList = new ArrayList<>(Arrays.asList(sources));
        sourcesList.add(DisableSecurity.class);

        startServer(userProperties, sourcesList.toArray(new Class[0]));
    }

    protected void stopServer() {
        if (context != null) {
            context.close();
            context = null;
        }
    }

    protected <T> T getBean(Class<T> beanClass) {
        return context.getBean(beanClass);
    }

    /**
     * Get a preconfigured WebClient. Base URL is set to BASE_URL or SSL_BASE_URL depending on isSsl() value.
     */
    protected WebClient getWebClient() {
        return getBean(WebClient.Builder.class)
            .baseUrl(isSsl() ? SSL_BASE_URL : BASE_URL)
            .build();
    }

    /**
     * Get a preconfigured WebTestClient. Base URL is set to BASE_URL or SSL_BASE_URL depending on isSsl() value.
     */
    protected WebTestClient getWebTestClient() {
        VertxClientHttpConnector connector = getBean(VertxClientHttpConnector.class);

        return WebTestClient.bindToServer(connector)
            .baseUrl(isSsl() ? SSL_BASE_URL : BASE_URL)
            .build();
    }

    /**
     * Get a preconfigured WebSocketClient.
     */
    protected WebSocketClient getWebSocketClient() {
        return getBean(VertxWebSocketClient.class);
    }

    /**
     * Override this method to provide default set of properties e.g. set protocol version to HTTP_2.
     */
    protected Properties defaultProperties() {
        return new Properties();
    }

    /**
     * Override this method in classes that require SSL.
     */
    protected boolean isSsl() {
        return false;
    }

    @Configuration
    static class DisableSecurity {
        @Bean
        public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
            return http
                .csrf().disable()
                .authorizeExchange().anyExchange().permitAll()
                .and()
                .build();
        }
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    private static class TestApplication {
        public TestApplication() {

        }
    }

    private static class TestNameLogger extends TestWatcher {

        protected void starting(Description description) {
            System.out.println("Starting test: " + description.getClassName() + "#" + description.getMethodName());
        }

        protected void finished(Description description) {
            System.out.println("Finished test: " + description.getClassName() + "#" + description.getMethodName());
        }
    }
}
