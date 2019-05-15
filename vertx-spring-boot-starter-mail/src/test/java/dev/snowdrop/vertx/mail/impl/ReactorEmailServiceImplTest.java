package dev.snowdrop.vertx.mail.impl;

import java.util.concurrent.CompletableFuture;

import io.smallrye.reactive.converters.ReactiveTypeConverter;
import io.smallrye.reactive.converters.Registry;
import dev.snowdrop.vertx.mail.ReactorEmailService;
import dev.snowdrop.vertx.mail.axel.Email;
import dev.snowdrop.vertx.mail.axel.ReactiveEmailService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ReactorEmailServiceImplTest {

    @Mock
    private ReactiveEmailService mockReactiveEmailService;

    @Mock
    private Email mockEmail;

    private ReactorEmailService reactorEmailService;

    @Before
    public void before() {
        ReactiveTypeConverter<Mono> monoConverter = Registry.lookup(Mono.class)
                .orElseThrow(() -> new AssertionError("Mono converter should be found"));
        reactorEmailService = new ReactorEmailServiceImpl(mockReactiveEmailService, monoConverter);
    }

    @Test
    public void shouldSendEmail() {
        CompletableFuture<Void> future = new CompletableFuture<>();
        future.complete(null);

        given(mockReactiveEmailService.send(mockEmail)).willReturn(future);

        Mono<Void> result = reactorEmailService.send(mockEmail);

        verify(mockReactiveEmailService).send(mockEmail);
        StepVerifier.create(result)
                .expectNextCount(0)
                .expectComplete();
    }

    @Test
    public void shouldFailToSendEmail() {
        CompletableFuture<Void> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("test"));

        given(mockReactiveEmailService.send(mockEmail)).willReturn(future);

        Mono<Void> result = reactorEmailService.send(mockEmail);

        verify(mockReactiveEmailService).send(mockEmail);
        StepVerifier.create(result)
                .expectNextCount(0)
                .expectErrorMessage("test");
    }

}
