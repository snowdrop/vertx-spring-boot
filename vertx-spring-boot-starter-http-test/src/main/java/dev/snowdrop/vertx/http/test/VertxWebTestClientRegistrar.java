package dev.snowdrop.vertx.http.test;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.test.web.reactive.server.WebTestClient;

public class VertxWebTestClientRegistrar implements BeanDefinitionRegistryPostProcessor, Ordered, BeanFactoryAware,
    ApplicationContextAware {

    private BeanFactory beanFactory;

    private ApplicationContext applicationContext;


    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        if (!isWebTestClientRegistered()) {
            VertxWebTestClientSupplier supplier = new VertxWebTestClientSupplier(applicationContext);
            RootBeanDefinition definition = new RootBeanDefinition(WebTestClient.class, supplier);
            definition.setLazyInit(true);
            registry.registerBeanDefinition(WebTestClient.class.getName(), definition);
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {

    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    private boolean isWebTestClientRegistered() {
        return BeanFactoryUtils.beanNamesForTypeIncludingAncestors((ListableBeanFactory) this.beanFactory,
            WebTestClient.class, false, false).length > 0;
    }
}
