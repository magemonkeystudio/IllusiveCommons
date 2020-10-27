/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config;

import me.filoghost.fcommons.Preconditions;
import me.filoghost.fcommons.config.exception.InvalidConfigValueException;
import me.filoghost.fcommons.config.exception.MissingConfigValueException;

import java.util.Objects;

public final class ConfigValue {

    public static final ConfigValue NULL = new ConfigValue(null, null);

    private final String path;
    private final Object rawConfigValue;

    public static <T> ConfigValue of(ConfigValueType<T> valueType, T value) {
        Preconditions.notNull(valueType, "valueType");
        Preconditions.notNull(value, "value");
        return new ConfigValue(null, valueType.toConfigValue(value));
    }

    protected static ConfigValue ofRawConfigValue(String path, Object rawConfigValue) {
        return new ConfigValue(path, rawConfigValue);
    }

    private ConfigValue(String path, Object rawConfigValue) {
        this.path = path;
        this.rawConfigValue = rawConfigValue;
    }

    protected Object getRawConfigValue() {
        return rawConfigValue;
    }

    public final <T> T as(ConfigValueType<T> valueType) {
        return valueType.fromConfigValueOrDefault(rawConfigValue, null);
    }

    public final <T> T asRequired(ConfigValueType<T> valueType) throws MissingConfigValueException, InvalidConfigValueException {
        return valueType.fromConfigValueRequired(path, rawConfigValue);
    }

    public final <T> T asOrDefault(ConfigValueType<T> valueType, T defaultValue) {
        return valueType.fromConfigValueOrDefault(rawConfigValue, defaultValue);
    }

    public boolean isPresentAs(ConfigValueType<?> configValueType) {
        return configValueType.isValidNonNullConfigValue(rawConfigValue);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ConfigValue other = (ConfigValue) obj;
        return Objects.equals(this.rawConfigValue, other.rawConfigValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rawConfigValue);
    }

    @Override
    public String toString() {
        return Objects.toString(rawConfigValue);
    }

}
