/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.mapped.converter;

import me.filoghost.fcommons.config.ConfigValue;
import me.filoghost.fcommons.config.ConfigValueType;
import me.filoghost.fcommons.config.exception.ConfigMappingException;
import me.filoghost.fcommons.config.exception.ConfigPostLoadException;

public abstract class Converter<F, V> {

    protected final ConfigValueType<V> configValueType;

    protected Converter(ConfigValueType<V> configValueType) {
        this.configValueType = configValueType;
    }

    public final ConfigValue toConfigValue(F fieldValue) throws ConfigMappingException {
        V configValue = toConfigValue0(fieldValue);
        if (configValue != null) {
            return ConfigValue.of(configValueType, configValue);
        } else {
            return ConfigValue.NULL;
        }
    }

    protected abstract V toConfigValue0(F fieldValue) throws ConfigMappingException;

    public final F toFieldValue(ConfigValue configValue, Object context) throws ConfigMappingException, ConfigPostLoadException {
        V rawConfigValue = configValue.as(configValueType);
        if (rawConfigValue != null) {
            return toFieldValue0(rawConfigValue, context);
        } else {
            return null;
        }
    }

    protected abstract F toFieldValue0(V configValue, Object context) throws ConfigMappingException, ConfigPostLoadException;

    public final boolean equalsConfig(F fieldValue, ConfigValue configValue) throws ConfigMappingException {
        V rawConfigValue = configValue.as(configValueType);
        return equalsConfig0(fieldValue, rawConfigValue);
    }

    protected abstract boolean equalsConfig0(F fieldValue, V configValue) throws ConfigMappingException;

}
