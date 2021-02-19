/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.mapped.converter;

import me.filoghost.fcommons.config.ConfigValue;
import me.filoghost.fcommons.config.ConfigValueType;
import me.filoghost.fcommons.config.exception.ConfigMappingException;
import me.filoghost.fcommons.config.exception.ConfigValidateException;

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

    public final F toFieldValue(ConfigValue configValue) throws ConfigMappingException, ConfigValidateException {
        V rawConfigValue = configValue.as(configValueType);
        if (rawConfigValue != null) {
            return toFieldValue0(rawConfigValue);
        } else {
            return null;
        }
    }

    protected abstract F toFieldValue0(V configValue) throws ConfigMappingException, ConfigValidateException;

    public final boolean equalsConfig(F fieldValue, ConfigValue configValue) throws ConfigMappingException {
        V rawConfigValue = configValue.as(configValueType);
        return equalsConfig0(fieldValue, rawConfigValue);
    }

    protected abstract boolean equalsConfig0(F fieldValue, V configValue) throws ConfigMappingException;

}
