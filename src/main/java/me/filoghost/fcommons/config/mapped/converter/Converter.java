/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.mapped.converter;

import me.filoghost.fcommons.config.ConfigType;
import me.filoghost.fcommons.config.ConfigValue;
import me.filoghost.fcommons.config.exception.ConfigMappingException;
import me.filoghost.fcommons.config.exception.ConfigValidateException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import me.filoghost.fcommons.config.exception.ConfigValueException;

public abstract class Converter<F, C> {

    private final ConfigType<C> configType;

    protected Converter(ConfigType<C> configType) {
        this.configType = configType;
    }

    protected final boolean isValidConfigValue(@NotNull ConfigValue configValue) {
        return configValue.isPresentAs(configType);
    }

    public final @NotNull ConfigValue toConfigValue(@NotNull F fieldValue) throws ConfigMappingException {
        C configValue = toConfigValue0(fieldValue);
        return ConfigValue.of(configType, configValue);
    }

    protected abstract @NotNull C toConfigValue0(@NotNull F fieldValue) throws ConfigMappingException;

    public final @Nullable F toFieldValue(@NotNull ConfigValue wrappedConfigValue, boolean required)
            throws ConfigMappingException, ConfigValidateException, ConfigValueException {
        C configValue;
        if (required) {
            configValue = wrappedConfigValue.asRequired(configType);
        } else {
            configValue = wrappedConfigValue.as(configType);
        }
        if (configValue != null) {
            return toFieldValue0(configValue, required);
        } else {
            return null;
        }
    }

    protected abstract @NotNull F toFieldValue0(@NotNull C configValue, boolean required)
            throws ConfigMappingException, ConfigValidateException, ConfigValueException;

    public final boolean equalsConfig(@Nullable F fieldValue, @NotNull ConfigValue wrappedConfigValue) throws ConfigMappingException {
        C configValue = wrappedConfigValue.as(configType);

        if (fieldValue == null && configValue == null) {
            // Both null, they are considered equal
            return true;
        } else if (fieldValue == null || configValue == null) {
            // One is null and the other is not, they cannot be equal
            return false;
        } else {
            return equalsConfig0(fieldValue, configValue);
        }
    }

    protected abstract boolean equalsConfig0(@NotNull F fieldValue, @NotNull C configValue) throws ConfigMappingException;

}
