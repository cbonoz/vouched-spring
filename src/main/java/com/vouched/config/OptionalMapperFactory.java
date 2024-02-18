package com.vouched.config;

import java.lang.reflect.Type;
import java.util.Optional;
import org.jdbi.v3.core.config.ConfigRegistry;
import org.jdbi.v3.core.generic.GenericTypes;
import org.jdbi.v3.core.mapper.ColumnMapper;
import org.jdbi.v3.core.mapper.ColumnMapperFactory;
import org.jdbi.v3.core.mapper.ColumnMappers;

public enum OptionalMapperFactory implements ColumnMapperFactory {
  INSTANCE;

  @Override
  public Optional<ColumnMapper<?>> build(Type type, ConfigRegistry config) {
    return GenericTypes.findGenericParameter(type, Optional.class)
        .flatMap(actualType -> config.get(ColumnMappers.class).findFor(actualType))
        .map(mapper -> (rs, col, ctx) -> Optional.ofNullable(mapper.map(rs, col, ctx)));
  }
}