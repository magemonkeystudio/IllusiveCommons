/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.mapped;

import com.google.common.collect.Lists;
import me.filoghost.fcommons.config.ConfigSection;
import me.filoghost.fcommons.config.ConfigValueType;
import me.filoghost.fcommons.config.exception.ConfigMappingException;
import me.filoghost.fcommons.config.mapped.converter.ConfigValueTypeConverter;
import me.filoghost.fcommons.config.mapped.converter.Converter;
import me.filoghost.fcommons.config.mapped.converter.ListConverter;
import me.filoghost.fcommons.config.mapped.converter.MappedConfigSectionConverter;
import me.filoghost.fcommons.reflection.TypeInfo;

import java.util.List;

public class ConverterRegistry {

    private static final List<ConfigValueTypeConverter<?>> CONFIG_VALUE_TYPE_CONVERTERS = Lists.newArrayList(
            new ConfigValueTypeConverter<>(ConfigValueType.DOUBLE, Double.class, double.class),
            new ConfigValueTypeConverter<>(ConfigValueType.FLOAT, Float.class, float.class),
            new ConfigValueTypeConverter<>(ConfigValueType.LONG, Long.class, long.class),
            new ConfigValueTypeConverter<>(ConfigValueType.INTEGER, Integer.class, int.class),
            new ConfigValueTypeConverter<>(ConfigValueType.SHORT, Short.class, short.class),
            new ConfigValueTypeConverter<>(ConfigValueType.BYTE, Byte.class, byte.class),
            new ConfigValueTypeConverter<>(ConfigValueType.BOOLEAN, Boolean.class, boolean.class),

            new ConfigValueTypeConverter<>(ConfigValueType.STRING, String.class),
            new ConfigValueTypeConverter<>(ConfigValueType.SECTION, ConfigSection.class)
    );

    @SuppressWarnings("unchecked")
    public static <T> Converter<T, ?> create(TypeInfo<T> typeInfo) throws ConfigMappingException {
        Class<T> typeClass = typeInfo.getTypeClass();

        for (ConfigValueTypeConverter<?> configValueTypeConverter : CONFIG_VALUE_TYPE_CONVERTERS) {
            if (configValueTypeConverter.supports(typeClass)) {
                return (Converter<T, ?>) configValueTypeConverter;
            }
        }

        if (MappedConfigSectionConverter.supports(typeClass)) {
            return (Converter<T, ?>) new MappedConfigSectionConverter((TypeInfo<MappedConfigSection>) typeInfo);

        } else if (ListConverter.supports(typeClass)) {
            return (Converter<T, ?>) new ListConverter<>((TypeInfo<List<T>>) typeInfo);
        }

        throw new ConfigMappingException("cannot find suitable converter for class \"" + typeClass + "\"");
    }

}
