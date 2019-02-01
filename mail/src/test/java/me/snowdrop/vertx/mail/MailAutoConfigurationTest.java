package me.snowdrop.vertx.mail;

import io.smallrye.reactive.converters.ReactiveTypeConverter;
import io.vertx.ext.mail.MailClient;
import me.snowdrop.vertx.core.VertxAutoConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@Import({ VertxAutoConfiguration.class, MailAutoConfiguration.class })
public class MailAutoConfigurationTest {

    @Autowired
    private MailClient mailClient;

    @Autowired
    private ReactiveTypeConverter<Mono> monoConverter;

    @Autowired
    private ReactorEmailService emailService;

    @Test
    public void shouldInjectBeans() {
        assertThat(mailClient).isNotNull();
        assertThat(monoConverter).isNotNull();
        assertThat(emailService).isNotNull();
    }

}
