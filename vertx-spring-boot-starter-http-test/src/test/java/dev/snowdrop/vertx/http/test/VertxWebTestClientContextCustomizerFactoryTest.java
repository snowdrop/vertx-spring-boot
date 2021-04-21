package dev.snowdrop.vertx.http.test;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextCustomizer;

import static org.assertj.core.api.Assertions.assertThat;

public class VertxWebTestClientContextCustomizerFactoryTest {

    private final VertxWebTestClientContextCustomizerFactory factory = new VertxWebTestClientContextCustomizerFactory();

    @Test
    public void shouldCreateCustomizer() {
        ContextCustomizer customizer = factory.createContextCustomizer(EmbeddedServerTestClass.class, null);

        assertThat(customizer).isNotNull();
        assertThat(customizer).isInstanceOf(VertxWebTestClientContextCustomizer.class);
    }

    @Test
    public void shouldIgnoreNonSpringBootTestClass() {
        ContextCustomizer customizer = factory.createContextCustomizer(NonEmbeddedServerTestClass.class, null);

        assertThat(customizer).isNull();
    }

    @Test
    public void shouldIgnoreNonEmbeddedServerTestClass() {
        ContextCustomizer customizer = factory.createContextCustomizer(NonTestClass.class, null);

        assertThat(customizer).isNull();
    }

    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
    private static class EmbeddedServerTestClass {}

    @SpringBootTest
    private static class NonEmbeddedServerTestClass {}

    private static class NonTestClass {}
}
