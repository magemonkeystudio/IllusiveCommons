/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.mapped.converter;

import me.filoghost.fcommons.config.ConfigValueType;

import java.util.Objects;

public class ConfigValueTypeConverter<T> extends Converter<T, T> {

    private final Class<T> mainClass;
    private final Class<T> primitiveClass;

    public ConfigValueTypeConverter(ConfigValueType<T> configValueType, Class<T> mainClass) {
        this(configValueType, mainClass, null);
    }

    public ConfigValueTypeConverter(ConfigValueType<T> configValueType, Class<T> mainClass, Class<T> primitiveClass) {
        super(configValueType);
        this.mainClass = mainClass;
        this.primitiveClass = primitiveClass;
    }

    @Override
    protected T toConfigValue0(T fieldValue) {
        return fieldValue;
    }

    @Override
    protected T toFieldValue0(T configValue) {
        return configValue;
    }

    @Override
    protected boolean equalsConfig0(T fieldValue, T configValue) {
        return Objects.equals(fieldValue, configValue);
    }

    public boolean supports(Class<?> typeClass) {
        return typeClass == mainClass || (primitiveClass != null && typeClass == primitiveClass);
    }

}
