package dev.snowdrop.vertx.mail;

import io.smallrye.reactive.converters.ReactiveTypeConverter;
import io.vertx.axle.ext.mail.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MailAutoConfigurationTest {

    @Autowired
    private MailClient mailClient;

    @Autowired
    private ReactiveTypeConverter<Mono> monoConverter;

    @Autowired
    private EmailService emailService;

    @Test
    public void shouldInjectBeans() {
        assertThat(mailClient).isNotNull();
        assertThat(monoConverter).isNotNull();
        assertThat(emailService).isNotNull();
    }

    @SpringBootApplication
    public static class TestApplication {
        public static void main(String[] args) {
            SpringApplication.run(TestApplication.class, args);
        }
    }
}
