package dev.snowdrop.vertx;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(
    classes = VertxAutoConfiguration.class,
    properties = {
        "vertx.eventLoopPoolSize=1",
        "vertx.maxWorkerExecuteTimeUnit=SECONDS",
        "vertx.fileSystem.fileCachingEnabled=false"
    })
public class VertxPropertiesTest {

    @Autowired
    private VertxProperties properties;

    @Test
    public void shouldGetProperties() {
        // Verify default primitive value
        assertThat(properties.getWorkerPoolSize()).isEqualTo(20);
        // Verify overwritten primitive value
        assertThat(properties.getEventLoopPoolSize()).isEqualTo(1);
        // Verify default enum value
        assertThat(properties.getMaxEventLoopExecuteTimeUnit()).isEqualTo(TimeUnit.NANOSECONDS);
        // Verify overwritten enum value
        assertThat(properties.getMaxWorkerExecuteTimeUnit()).isEqualTo(TimeUnit.SECONDS);
        // Verify default file system value
        assertThat(properties.getFileSystem().isClassPathResolvingEnabled()).isTrue();
        // Verify overwritten file system value
        assertThat(properties.getFileSystem().isFileCachingEnabled()).isFalse();
    }

}
