package dev.snowdrop.vertx.postgres;

import java.util.List;

import org.springframework.data.convert.EntityWriter;
import org.springframework.data.mapping.PersistentPropertyAccessor;
import org.springframework.data.relational.core.conversion.BasicRelationalConverter;
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity;
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty;
import org.springframework.util.ClassUtils;

public class ColumnEntityWriter implements EntityWriter<Object, List<ColumnValue>> {

    private final BasicRelationalConverter converter;

    public ColumnEntityWriter(BasicRelationalConverter converter) {
        this.converter = converter;
    }

    @Override
    public void write(Object source, List<ColumnValue> sink) {
        Class<?> userClass = ClassUtils.getUserClass(source);

        // TODO custom conversions

        RelationalPersistentEntity<?> entity = getPersistentEntity(userClass);
        PersistentPropertyAccessor<?> propertyAccessor = entity.getPropertyAccessor(source);

        writeProperties(sink, entity, propertyAccessor);
    }

    private void writeProperties(List<ColumnValue> sink, RelationalPersistentEntity<?> entity,
        PersistentPropertyAccessor<?> accessor) {

        for (RelationalPersistentProperty property : entity) {
            if (!property.isWritable()) {
                continue;
            }

            // TODO nested entities
            // TODO custom conversions

            Object value = accessor.getProperty(property);

            if (value != null) {
                sink.add(new ColumnValue(property.getColumnName(), value));
            }
        }
    }

    private <R> RelationalPersistentEntity<R> getPersistentEntity(Class<R> type) {
        return (RelationalPersistentEntity<R>) converter.getMappingContext().getRequiredPersistentEntity(type);
    }
}
