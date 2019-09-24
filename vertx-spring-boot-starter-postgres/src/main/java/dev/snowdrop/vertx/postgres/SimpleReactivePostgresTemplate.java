package dev.snowdrop.vertx.postgres;

import java.util.List;
import java.util.function.Function;

import io.vertx.axle.pgclient.PgPool;
import io.vertx.axle.sqlclient.Row;
import io.vertx.axle.sqlclient.Tuple;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class SimpleReactivePostgresTemplate implements ReactivePostgresTemplate {

    private final PgPool pgPool;

    SimpleReactivePostgresTemplate(PgPool pgPool) {
        this.pgPool = pgPool;
    }

    @Override
    public Flux<Row> query(String sql) {
        return Mono.fromCompletionStage(pgPool.query(sql))
            .flatMapIterable(Function.identity());
    }

    @Override
    public Flux<Row> preparedQuery(String sql) {
        return Mono.fromCompletionStage(pgPool.preparedQuery(sql))
            .flatMapIterable(Function.identity());
    }

    @Override
    public Flux<Row> preparedQuery(String sql, Tuple arguments) {
        return Mono.fromCompletionStage(pgPool.preparedQuery(sql, arguments))
            .flatMapIterable(Function.identity());
    }

    @Override
    public Flux<Row> preparedBatch(String sql, List<Tuple> batch) {
        return Mono.fromCompletionStage(pgPool.preparedBatch(sql, batch))
            .flatMapIterable(Function.identity());
    }
}
