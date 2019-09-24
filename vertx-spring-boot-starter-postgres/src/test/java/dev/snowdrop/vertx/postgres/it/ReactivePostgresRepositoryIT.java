package dev.snowdrop.vertx.postgres.it;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ReactivePostgresRepositoryIT {

    @Autowired
    private TestReactivePostgresRepository repository;

    @Test
    public void shouldSave() {
        assertThat(repository.save(null)).isNull();
    }
}
