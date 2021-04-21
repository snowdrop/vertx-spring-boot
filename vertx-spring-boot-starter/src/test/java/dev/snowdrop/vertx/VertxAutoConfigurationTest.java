package dev.snowdrop.vertx;

import io.vertx.core.Vertx;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = VertxAutoConfiguration.class)
public class VertxAutoConfigurationTest {

    @Autowired
    private Vertx vertx;

    @Test
    public void shouldInjectVertxInstance() {
        assertThat(vertx).isNotNull();
    }
}
