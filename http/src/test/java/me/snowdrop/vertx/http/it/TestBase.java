package me.snowdrop.vertx.http.it;

import java.util.Properties;

import me.snowdrop.vertx.http.client.VertxWebSocketClient;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;

public class TestBase {

    protected static final String BASE_URL = "http://localhost:8080";

    protected static final String SSL_BASE_URL = "https://localhost:8080";

    protected static final String WS_BASE_URL = "ws://localhost:8080";

    @Rule
    public TestRule watcher = new TestNameLogger();

    private ConfigurableApplicationContext context;

    protected void startServer(Class<?>... sources) {
        startServer(new Properties(), sources);
    }

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

    protected void stopServer() {
        if (context != null) {
            context.close();
            context = null;
        }
    }

    protected <T> T getBean(Class<T> beanClass) {
        return context.getBean(beanClass);
    }

    protected WebClient getWebClient() {
        return getBean(WebClient.Builder.class)
            .baseUrl(isSsl() ? SSL_BASE_URL : BASE_URL)
            .build();
    }

    protected Properties defaultProperties() {
        return new Properties();
    }

    protected boolean isSsl() {
        return false;
    }

    protected WebSocketClient getWebSocketClient() {
        return getBean(VertxWebSocketClient.class);
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
