package dev.snowdrop.vertx.postgres;

import org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport;
import org.springframework.data.repository.core.RepositoryMetadata;

public class ReactivePostgresRepositoryConfigurationExtension extends RepositoryConfigurationExtensionSupport {

    @Override
    protected String getModulePrefix() {
        return "postgres";
    }

    @Override
    public String getRepositoryFactoryBeanClassName() {
        return ReactivePostgresRepositoryFactoryBean.class.getName();
    }

    @Override
    protected boolean useRepositoryConfiguration(RepositoryMetadata metadata) {
        return metadata.isReactiveRepository();
    }
}
