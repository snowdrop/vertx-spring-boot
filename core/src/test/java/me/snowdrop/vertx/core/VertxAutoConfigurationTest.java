package me.snowdrop.vertx.core;

import io.vertx.core.Vertx;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@Import(VertxAutoConfiguration.class)
public class VertxAutoConfigurationTest {

    @Autowired
    private Vertx vertx;

    @Test
    public void shouldInjectVertxInstance() {
        assertThat(vertx).isNotNull();
    }

}
