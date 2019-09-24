package dev.snowdrop.vertx.postgres;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

@NoRepositoryBean
public interface ReactivePostgresRepository<T, ID> extends ReactiveCrudRepository<T, ID> {

}
