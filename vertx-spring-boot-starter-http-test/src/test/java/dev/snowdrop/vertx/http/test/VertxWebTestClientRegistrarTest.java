package dev.snowdrop.vertx.http.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
public class VertxWebTestClientRegistrarTest {

    @Mock
    private ListableBeanFactory mockFactory;

    @Mock
    private ApplicationContext mockContext;

    @Mock
    private BeanDefinitionRegistry mockRegistry;

    @Mock
    private WebTestClient mockClient;

    private final VertxWebTestClientRegistrar registrar = new VertxWebTestClientRegistrar();

    @BeforeEach
    public void setUp() {
        registrar.setBeanFactory(mockFactory);
        registrar.setApplicationContext(mockContext);
    }

    @Test
    public void shouldHaveLowestPrecedence() {
        assertThat(registrar.getOrder()).isEqualTo(Ordered.LOWEST_PRECEDENCE);
    }

    @Test
    public void shouldRegisterBean() {
        given(mockFactory.getBeanNamesForType(WebTestClient.class, false, false))
            .willReturn(new String[]{});

        registrar.postProcessBeanDefinitionRegistry(mockRegistry);

        ArgumentCaptor<RootBeanDefinition> definitionCaptor = ArgumentCaptor.forClass(RootBeanDefinition.class);
        verify(mockRegistry).registerBeanDefinition(eq(WebTestClient.class.getName()), definitionCaptor.capture());

        RootBeanDefinition definition = definitionCaptor.getValue();
        assertThat(definition.getBeanClass()).isEqualTo(WebTestClient.class);
        assertThat(definition.getInstanceSupplier()).isInstanceOf(VertxWebTestClientSupplier.class);
        assertThat(definition.isLazyInit()).isTrue();
    }

    @Test
    public void shouldSkipIfBeanExists() {
        given(mockFactory.getBeanNamesForType(WebTestClient.class, false, false))
            .willReturn(new String[]{ WebTestClient.class.getName() });

        registrar.postProcessBeanDefinitionRegistry(mockRegistry);
        verifyNoInteractions(mockRegistry);
    }
}
