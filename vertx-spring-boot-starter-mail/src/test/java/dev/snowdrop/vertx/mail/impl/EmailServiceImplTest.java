package dev.snowdrop.vertx.mail.impl;

import java.util.concurrent.CompletableFuture;

import dev.snowdrop.vertx.mail.EmailService;
import io.smallrye.reactive.converters.ReactiveTypeConverter;
import io.smallrye.reactive.converters.Registry;
import io.vertx.axle.ext.mail.MailClient;
import io.vertx.ext.mail.MailMessage;
import io.vertx.ext.mail.MailResult;
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
public class EmailServiceImplTest {

    @Mock
    private MailClient mockMailClient;

    @Mock
    private MailMessage mockMailMessage;

    @Mock
    private MailResult mockMailResult;

    private EmailService emailService;

    @Before
    public void before() {
        ReactiveTypeConverter<Mono> monoConverter = Registry.lookup(Mono.class)
            .orElseThrow(() -> new AssertionError("Mono converter should be found"));
        emailService = new EmailServiceImpl(mockMailClient, monoConverter);
    }

    @Test
    public void shouldSend() {
        CompletableFuture<MailResult> future = new CompletableFuture<>();
        future.complete(mockMailResult);

        given(mockMailClient.sendMail(mockMailMessage)).willReturn(future);

        Mono<MailResult> result = emailService.send(mockMailMessage);

        verify(mockMailClient).sendMail(mockMailMessage);
        StepVerifier.create(result)
            .expectNext(mockMailResult)
            .expectComplete();
    }

    @Test
    public void shouldFailToSend() {
        CompletableFuture<MailResult> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("test"));

        given(mockMailClient.sendMail(mockMailMessage)).willReturn(future);

        Mono<MailResult> result = emailService.send(mockMailMessage);

        verify(mockMailClient).sendMail(mockMailMessage);
        StepVerifier.create(result)
            .expectNextCount(0)
            .expectErrorMessage("test");
    }

}
