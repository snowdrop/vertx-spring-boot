package dev.snowdrop.vertx.postgres;

import io.vertx.axle.pgclient.PgPool;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.sqlclient.PoolOptions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnBean(Vertx.class)
@ConditionalOnProperty(prefix = "vertx.postgres", value = "enabled", matchIfMissing = true)
public class PostgresAutoConfiguration {

    @Bean
    public SimpleReactivePostgresTemplate reactivePostgresTemplate(Vertx vertx) {
        PgConnectOptions connectOptions = new PgConnectOptions()
            .setUser("sa")
            .setPassword("sa")
            .setDatabase("sa");
        PoolOptions poolOptions = new PoolOptions();
        PgPool pgPool = PgPool.pool(new io.vertx.axle.core.Vertx(vertx), connectOptions, poolOptions);

        return new SimpleReactivePostgresTemplate(pgPool);
    }
}
