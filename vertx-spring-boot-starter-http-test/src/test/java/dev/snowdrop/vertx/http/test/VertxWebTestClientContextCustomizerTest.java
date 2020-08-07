package dev.snowdrop.vertx.http.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ConfigurableApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@RunWith(MockitoJUnitRunner.class)
public class VertxWebTestClientContextCustomizerTest {

    @Mock
    private ConfigurableApplicationContext mockContext;

    @Mock
    private ConfigurableListableBeanFactory mockFactory;

    @Mock
    private DefaultListableBeanFactory mockRegistry;

    private VertxWebTestClientContextCustomizer customizer = new VertxWebTestClientContextCustomizer();

    @Test
    public void shouldRegisterBean() {
        given(mockContext.getBeanFactory()).willReturn(mockRegistry);

        customizer.customizeContext(mockContext, null);

        ArgumentCaptor<RootBeanDefinition> definitionCaptor = ArgumentCaptor.forClass(RootBeanDefinition.class);
        verify(mockRegistry)
            .registerBeanDefinition(eq(VertxWebTestClientRegistrar.class.getName()), definitionCaptor.capture());

        RootBeanDefinition definition = definitionCaptor.getValue();
        assertThat(definition.getBeanClass()).isEqualTo(VertxWebTestClientRegistrar.class);
        assertThat(definition.getRole()).isEqualTo(BeanDefinition.ROLE_INFRASTRUCTURE);
    }

    @Test
    public void shouldIgnoreNonRegistryBeanFactory() {
        given(mockContext.getBeanFactory()).willReturn(mockFactory);

        customizer.customizeContext(mockContext, null);

        verifyNoInteractions(mockRegistry);
    }

}
