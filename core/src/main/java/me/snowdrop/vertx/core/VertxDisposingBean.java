package me.snowdrop.vertx.core;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import io.vertx.core.Vertx;
import org.springframework.beans.factory.DisposableBean;

public class VertxDisposingBean implements DisposableBean {

    private final Vertx vertx;

    public VertxDisposingBean(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public void destroy() throws ExecutionException, InterruptedException {
        CompletableFuture<Void> future = new CompletableFuture<>();
        vertx.close(ar -> future.complete(null));
        future.get();
    }

}
