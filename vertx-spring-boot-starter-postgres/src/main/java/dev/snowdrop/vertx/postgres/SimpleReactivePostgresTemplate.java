package dev.snowdrop.vertx.postgres;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import io.vertx.axle.pgclient.PgPool;
import io.vertx.axle.sqlclient.Row;
import io.vertx.axle.sqlclient.Tuple;
import org.springframework.data.relational.core.conversion.BasicRelationalConverter;
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class SimpleReactivePostgresTemplate implements ReactivePostgresTemplate {

    private final PgPool pgPool;

    private final ColumnEntityWriter entityWriter;

    private final BasicRelationalConverter converter;

    SimpleReactivePostgresTemplate(PgPool pgPool, ColumnEntityWriter entityWriter, BasicRelationalConverter converter) {
        this.pgPool = pgPool;
        this.entityWriter = entityWriter;
        this.converter = converter;
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
        System.out.println(sql + ", " + arguments);
        return Mono.fromCompletionStage(pgPool.preparedQuery(sql, arguments))
            .flatMapIterable(Function.identity());
    }

    @Override
    public Flux<Row> preparedBatch(String sql, List<Tuple> batch) {
        return Mono.fromCompletionStage(pgPool.preparedBatch(sql, batch))
            .flatMapIterable(Function.identity());
    }

    @Override
    public <T> Flux<Row> save(T entity) {
        RelationalPersistentEntity<?> persistentProperties =
            converter.getMappingContext().getRequiredPersistentEntity(entity.getClass());

//        if (!persistentProperties.isNew(entity)) {
//            throw new RuntimeException("Update not implemented");
//        }

        List<ColumnValue> columnValues = new LinkedList<>();
        entityWriter.write(entity, columnValues);

        List<String> columnNames = new LinkedList<>();
        List<Object> values = new LinkedList<>();
        List<String> valueMarkers = new LinkedList<>();

        for (int i = 0; i < columnValues.size(); i++) {
            columnNames.add(columnValues.get(i).getName());
            values.add(columnValues.get(i).getValue());
            valueMarkers.add("$" + (i + 1));
        }

        Tuple tuple = Tuple.tuple();
        values.forEach(tuple::addValue);
        String query = String.format("INSERT INTO %s (%s) VALUES (%s)", persistentProperties.getTableName(),
            String.join(",", columnNames), String.join(",", valueMarkers));

        return preparedQuery(query, tuple);
    }
}
