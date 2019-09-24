package dev.snowdrop.vertx.postgres.it;

import dev.snowdrop.vertx.postgres.ReactivePostgresRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestReactivePostgresRepository extends ReactivePostgresRepository<Person, String> {
}
