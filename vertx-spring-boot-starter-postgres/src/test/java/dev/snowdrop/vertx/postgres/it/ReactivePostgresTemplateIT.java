package dev.snowdrop.vertx.postgres.it;

import java.util.Arrays;
import java.util.List;

import dev.snowdrop.vertx.postgres.ReactivePostgresTemplate;
import io.vertx.axle.sqlclient.Tuple;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assume.assumeTrue;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ReactivePostgresTemplateIT {

    private static final String CREATE_QUERY =
        "CREATE TABLE person (id VARCHAR(255) PRIMARY KEY, name VARCHAR(255), age INT)";

    private static final String DROP_QUERY = "DROP TABLE person";

    private static final String SELECT_QUERY = "SELECT * from person WHERE id=$1";

    private static final String INSERT_QUERY = "INSERT INTO person (id, name, age) VALUES ($1, $2, $3)";

    private static final String UPDATE_AGE_QUERY = "UPDATE person SET age=$1 WHERE id=$2";

    private static final String DELETE_QUERY = "DELETE FROM person WHERE id=$1";

    @Autowired
    private ReactivePostgresTemplate template;

    @Before
    public void setUp() {
        assumeTrue("Database is not available", isDatabaseAvailable());

        StepVerifier.create(template.query(CREATE_QUERY))
            .verifyComplete();
    }

    @After
    public void tearDown() {
        if (isDatabaseAvailable()) {
            StepVerifier.create(template.query(DROP_QUERY))
                .verifyComplete();
        }
    }

    @Test
    public void shouldUpdate() {
        StepVerifier.create(template.preparedQuery(INSERT_QUERY, Tuple.of("1", "Jonas", 30)))
            .verifyComplete();

        StepVerifier.create(template.preparedQuery(UPDATE_AGE_QUERY, Tuple.of(40, "1")))
            .verifyComplete();

        StepVerifier.create(template.preparedQuery(SELECT_QUERY, Tuple.of("1")))
            .assertNext(row -> {
                assertThat(row.getString(0)).isEqualTo("1");
                assertThat(row.getString(1)).isEqualTo("Jonas");
                assertThat(row.getInteger(2)).isEqualTo(40);
            })
            .verifyComplete();
    }

    @Test
    public void shouldDelete() {
        StepVerifier.create(template.preparedQuery(INSERT_QUERY, Tuple.of("1", "Jonas", 30)))
            .verifyComplete();

        StepVerifier.create(template.preparedQuery(DELETE_QUERY, Tuple.of("1")))
            .verifyComplete();

        StepVerifier.create(template.preparedQuery(SELECT_QUERY, Tuple.of("1")))
            .verifyComplete();
    }

    @Test
    public void shouldInsertBatch() {
        List<Tuple> dataBatch = Arrays.asList(Tuple.of("1", "Jonas", 30), Tuple.of("2", "Petras", 40));
        StepVerifier.create(template.preparedBatch(INSERT_QUERY, dataBatch))
            .verifyComplete();

        StepVerifier.create(template.preparedQuery(SELECT_QUERY, Tuple.of("1")))
            .assertNext(row -> {
                assertThat(row.getString(0)).isEqualTo("1");
                assertThat(row.getString(1)).isEqualTo("Jonas");
                assertThat(row.getInteger(2)).isEqualTo(30);
            })
            .verifyComplete();

        StepVerifier.create(template.preparedQuery(SELECT_QUERY, Tuple.of("2")))
            .assertNext(row -> {
                assertThat(row.getString(0)).isEqualTo("2");
                assertThat(row.getString(1)).isEqualTo("Petras");
                assertThat(row.getInteger(2)).isEqualTo(40);
            })
            .verifyComplete();
    }

    @Test
    public void shouldSaveEntity() {
        Person person = new Person("1", "Jonas", 30);
        StepVerifier.create(template.save(person))
            .verifyComplete();
    }

    private boolean isDatabaseAvailable() {
        try {
            template.query("SELECT NOW()")
                .blockFirst();
            return true;
        } catch (RuntimeException e) {
            if (e.getCause().getMessage().toLowerCase().contains("connection refused")) {
                return false;
            }
            throw e;
        }
    }
}
