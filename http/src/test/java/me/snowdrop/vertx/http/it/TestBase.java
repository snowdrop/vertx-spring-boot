package me.snowdrop.vertx.http.it;

import java.util.Properties;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClientOptions;
import me.snowdrop.vertx.http.client.VertxClientHttpConnector;
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

    protected void startServer(Properties properties, Class<?>... sources) {
        if (context != null) {
            throw new RuntimeException("Server is already running");
        }

        context = new SpringApplicationBuilder(TestApplication.class)
            .sources(sources)
            .web(WebApplicationType.REACTIVE)
            .properties(properties)
            .run();
    }

    protected void stopServer() {
        if (context == null) {
            throw new RuntimeException("Server is not running");
        }

        context.close();
        context = null;
    }

    protected <T> T getBean(Class<T> beanClass) {
        return context.getBean(beanClass);
    }

    protected WebClient getWebClient() {
        return getWebClient(new HttpClientOptions());
    }

    protected WebClient getWebClient(HttpClientOptions options) {
        Vertx vertx = getBean(Vertx.class);

        return WebClient.builder()
            .clientConnector(new VertxClientHttpConnector(vertx, options))
            .baseUrl(options.isSsl() ? SSL_BASE_URL : BASE_URL)
            .build();
    }

    protected WebSocketClient getWebSocketClient() {
        Vertx vertx = getBean(Vertx.class);

        return new VertxWebSocketClient(vertx);
    }

    protected WebSocketClient getWebSocketClient(HttpClientOptions options) {
        Vertx vertx = getBean(Vertx.class);

        return new VertxWebSocketClient(vertx, options);
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
