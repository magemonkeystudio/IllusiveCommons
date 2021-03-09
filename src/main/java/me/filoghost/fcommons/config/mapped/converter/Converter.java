/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.mapped.converter;

import me.filoghost.fcommons.config.ConfigValue;
import me.filoghost.fcommons.config.type.ConfigType;
import me.filoghost.fcommons.config.exception.ConfigMappingException;
import me.filoghost.fcommons.config.exception.ConfigValidateException;

public abstract class Converter<F, C> {

    protected final ConfigType<C> configType;

    protected Converter(ConfigType<C> configType) {
        this.configType = configType;
    }

    public final ConfigValue toConfigValue(F fieldValue) throws ConfigMappingException {
        C configValue = toConfigValue0(fieldValue);
        if (configValue != null) {
            return ConfigValue.of(configType, configValue);
        } else {
            return ConfigValue.NULL;
        }
    }

    protected abstract C toConfigValue0(F fieldValue) throws ConfigMappingException;

    public final F toFieldValue(ConfigValue wrappedConfigValue) throws ConfigMappingException, ConfigValidateException {
        C configValue = wrappedConfigValue.as(configType);
        if (configValue != null) {
            return toFieldValue0(configValue);
        } else {
            return null;
        }
    }

    protected abstract F toFieldValue0(C configValue) throws ConfigMappingException, ConfigValidateException;

    public final boolean equalsConfig(F fieldValue, ConfigValue wrappedConfigValue) throws ConfigMappingException {
        C configValue = wrappedConfigValue.as(configType);
        return equalsConfig0(fieldValue, configValue);
    }

    protected abstract boolean equalsConfig0(F fieldValue, C configValue) throws ConfigMappingException;

}
