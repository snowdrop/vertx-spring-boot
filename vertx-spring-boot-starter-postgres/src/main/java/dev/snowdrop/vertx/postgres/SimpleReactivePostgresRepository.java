package dev.snowdrop.vertx.postgres;

import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class SimpleReactivePostgresRepository<T, ID> implements ReactivePostgresRepository<T, ID> {

    @Override
    public <S extends T> Mono<S> save(S entity) {
        return null;
    }

    @Override
    public <S extends T> Flux<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public <S extends T> Flux<S> saveAll(Publisher<S> entityStream) {
        return null;
    }

    @Override
    public Mono<T> findById(ID id) {
        return null;
    }

    @Override
    public Mono<T> findById(Publisher<ID> id) {
        return null;
    }

    @Override
    public Mono<Boolean> existsById(ID id) {
        return null;
    }

    @Override
    public Mono<Boolean> existsById(Publisher<ID> id) {
        return null;
    }

    @Override
    public Flux<T> findAll() {
        return null;
    }

    @Override
    public Flux<T> findAllById(Iterable<ID> ids) {
        return null;
    }

    @Override
    public Flux<T> findAllById(Publisher<ID> idStream) {
        return null;
    }

    @Override
    public Mono<Long> count() {
        return null;
    }

    @Override
    public Mono<Void> deleteById(ID id) {
        return null;
    }

    @Override
    public Mono<Void> deleteById(Publisher<ID> id) {
        return null;
    }

    @Override
    public Mono<Void> delete(T entity) {
        return null;
    }

    @Override
    public Mono<Void> deleteAll(Iterable<? extends T> entities) {
        return null;
    }

    @Override
    public Mono<Void> deleteAll(Publisher<? extends T> entityStream) {
        return null;
    }

    @Override
    public Mono<Void> deleteAll() {
        return null;
    }
}
