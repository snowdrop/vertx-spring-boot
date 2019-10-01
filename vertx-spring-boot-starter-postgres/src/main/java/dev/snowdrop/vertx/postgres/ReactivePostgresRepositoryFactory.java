package dev.snowdrop.vertx.postgres;

import org.springframework.data.relational.repository.query.RelationalEntityInformation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.ReactiveRepositoryFactorySupport;

public class ReactivePostgresRepositoryFactory extends ReactiveRepositoryFactorySupport {

    @Override
    public <T, ID> RelationalEntityInformation<T, ID> getEntityInformation(Class<T> domainClass) {
        return null;
    }

    @Override
    protected Object getTargetRepository(RepositoryInformation metadata) {
//        RelationalEntityInformation<?, ?> entityInformation = getEntityInformation(metadata.getDomainType());

        return getTargetRepositoryViaReflection(metadata);
    }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        return SimpleReactivePostgresRepository.class;
    }
}
