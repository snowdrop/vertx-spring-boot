package dev.snowdrop.vertx.postgres;

import io.vertx.axle.sqlclient.Row;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.mapping.PreferredConstructor;
import org.springframework.data.mapping.model.ParameterValueProvider;
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity;
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty;
import org.springframework.lang.Nullable;

public class RowParameterValueProvider implements ParameterValueProvider<RelationalPersistentProperty> {

    private final Row row;

    private final RelationalPersistentEntity<?> entity;

    private final ConversionService conversionService;

    public RowParameterValueProvider(Row row, RelationalPersistentEntity<?> entity,
        ConversionService conversionService) {
        this.row = row;
        this.entity = entity;
        this.conversionService = conversionService;
    }

    @Override
    public <T> T getParameterValue(PreferredConstructor.Parameter<T, RelationalPersistentProperty> parameter) {
        RelationalPersistentProperty property = entity.getRequiredPersistentProperty(parameter.getName());
        Object value = row.getValue(property.getColumnName());
        Class<T> type = parameter.getRawType();

        return convertIfNecessary(value, type);
    }

    @SuppressWarnings("unchecked")
    private <T> T convertIfNecessary(@Nullable Object source, Class<T> type) {
        if (source == null) {
            return null;
        }

        if (type.isAssignableFrom(source.getClass())) {
            return (T) source;
        }

        return conversionService.convert(source, type);
    }
}
