package dev.snowdrop.vertx.http.test;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.MergedContextConfiguration;

public class VertxWebTestClientContextCustomizer implements ContextCustomizer {

    @Override
    public void customizeContext(ConfigurableApplicationContext context, MergedContextConfiguration mergedConfig) {
        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();

        if (beanFactory instanceof BeanDefinitionRegistry) {
            registerWebTestClient((BeanDefinitionRegistry) beanFactory);
        }
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.getClass() == getClass();
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    private void registerWebTestClient(BeanDefinitionRegistry registry) {
        RootBeanDefinition definition = new RootBeanDefinition(VertxWebTestClientRegistrar.class);
        definition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        registry.registerBeanDefinition(VertxWebTestClientRegistrar.class.getName(), definition);
    }
}
