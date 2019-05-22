package dev.snowdrop.vertx.http.test;

import java.util.List;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.test.context.ContextConfigurationAttributes;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.ContextCustomizerFactory;

public class VertxWebTestClientContextCustomizerFactory implements ContextCustomizerFactory {

    @Override
    public ContextCustomizer createContextCustomizer(Class<?> testClass,
        List<ContextConfigurationAttributes> configAttributes) {

        if (isEmbeddedSpringBootTest(testClass)) {
            return new VertxWebTestClientContextCustomizer();
        }

        return null;
    }

    private boolean isEmbeddedSpringBootTest(Class<?> testClass) {
        SpringBootTest annotation = AnnotatedElementUtils.getMergedAnnotation(testClass, SpringBootTest.class);

        return annotation != null && annotation.webEnvironment().isEmbedded();
    }
}
