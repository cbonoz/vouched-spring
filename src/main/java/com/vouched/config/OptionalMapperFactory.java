package com.vouched.config;

import org.jdbi.v3.core.config.ConfigRegistry;
import org.jdbi.v3.core.mapper.ColumnMapper;
import org.jdbi.v3.core.mapper.ColumnMapperFactory;
import org.jdbi.v3.core.mapper.ColumnMappers;
import org.jdbi.v3.core.statement.StatementContext;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class OptionalMapperFactory implements ColumnMapperFactory {

    @Override
    public Optional<ColumnMapper<?>> build(Type type, ConfigRegistry config) {
        if (type instanceof ParameterizedType
                && Optional.class.equals(((ParameterizedType) type).getRawType())) {
            Type valueType = ((ParameterizedType) type).getActualTypeArguments()[0];
            ColumnMapper<?> valueMapper = config.get(ColumnMappers.class).findFor(valueType).orElse(null);
            return Optional.of(new OptionalMapper(valueMapper));
        }
        return Optional.empty();
    }

    private static class OptionalMapper implements ColumnMapper<Optional<?>> {
        private final ColumnMapper<?> valueMapper;

        private OptionalMapper(ColumnMapper<?> valueMapper) {
            this.valueMapper = valueMapper;
        }

        @Override
        public Optional<?> map(ResultSet r, int columnNumber, StatementContext ctx)
                throws SQLException {
            Object value = valueMapper.map(r, columnNumber, ctx);
            return Optional.ofNullable(value);
        }

        @Override
        public Optional<?> map(ResultSet r, String columnLabel, StatementContext ctx)
                throws SQLException {
            Object value = valueMapper.map(r, columnLabel, ctx);
            return Optional.ofNullable(value);
        }
    }
}
