package dev.snowdrop.vertx.http.server.actuator;

import dev.snowdrop.vertx.http.server.VertxReactiveWebServerFactoryCustomizer;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.boot.actuate.autoconfigure.web.ManagementContextConfiguration;
import org.springframework.boot.actuate.autoconfigure.web.ManagementContextType;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementWebServerFactoryCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.reactive.ReactiveWebServerFactoryCustomizer;
import org.springframework.boot.web.reactive.server.ConfigurableReactiveWebServerFactory;
import org.springframework.context.annotation.Bean;

@ManagementContextConfiguration(ManagementContextType.CHILD)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class VertxManagementChildContextConfiguration {

    @Bean
    public VertxManagementChildContextConfiguration.VertxManagementWebServerFactoryCustomizer vertxManagementWebServerFactoryCustomizer(
        ListableBeanFactory beanFactory) {
        return new VertxManagementChildContextConfiguration.VertxManagementWebServerFactoryCustomizer(beanFactory);
    }

    class VertxManagementWebServerFactoryCustomizer extends
        ManagementWebServerFactoryCustomizer<ConfigurableReactiveWebServerFactory> {

        VertxManagementWebServerFactoryCustomizer(ListableBeanFactory beanFactory) {
            super(beanFactory, ReactiveWebServerFactoryCustomizer.class, VertxReactiveWebServerFactoryCustomizer.class);
        }
    }
}
