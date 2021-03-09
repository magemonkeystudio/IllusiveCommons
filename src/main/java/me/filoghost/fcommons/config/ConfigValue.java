/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config;

import me.filoghost.fcommons.Preconditions;
import me.filoghost.fcommons.config.exception.InvalidConfigValueException;
import me.filoghost.fcommons.config.exception.MissingConfigValueException;
import me.filoghost.fcommons.config.type.ConfigType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class ConfigValue {

    public static final ConfigValue NULL = new ConfigValue(null, null);
    
    private final ConfigPath sourcePath;
    private final Object rawValue;

    public static <T> ConfigValue of(ConfigType<T> type, @NotNull T value) {
        Preconditions.notNull(type, "type");
        Preconditions.notNull(value, "value");
        return new ConfigValue(null, type.toRawValue(value));
    }

    public static ConfigValue wrapRawConfigValue(ConfigPath configPath, @Nullable Object rawValue) {
        return new ConfigValue(configPath, rawValue);
    }

    private ConfigValue(@Nullable ConfigPath sourcePath, Object rawValue) {
        this.sourcePath = sourcePath;
        this.rawValue = rawValue;
    }

    public Object getRawValue() {
        return rawValue;
    }

    @Nullable
    public <T> T as(ConfigType<T> type) {
        return type.fromRawValueOrNull(rawValue);
    }

    @NotNull
    public <T> T asRequired(ConfigType<T> type) throws MissingConfigValueException, InvalidConfigValueException {
        return type.fromRawValueRequired(rawValue, sourcePath);
    }

    @Contract("_, !null -> !null")
    public <T> T asOrDefault(ConfigType<T> type, @Nullable T defaultValue) {
        return type.fromRawValueOrDefault(rawValue, defaultValue);
    }

    public boolean isPresentAs(ConfigType<?> type) {
        return type.isConvertibleRawValue(rawValue);
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
        return Objects.equals(this.rawValue, other.rawValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rawValue);
    }

    @Override
    public String toString() {
        return Objects.toString(rawValue);
    }

}
