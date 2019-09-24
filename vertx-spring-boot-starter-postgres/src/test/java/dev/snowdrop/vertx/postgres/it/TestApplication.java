package dev.snowdrop.vertx.postgres.it;

import dev.snowdrop.vertx.postgres.EnableReactivePostgresRepositories;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableReactivePostgresRepositories
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }
}
