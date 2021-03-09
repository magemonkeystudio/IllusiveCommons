/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.type;

import me.filoghost.fcommons.config.ConfigErrors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class StringConfigType extends ConfigType<String> {

    public StringConfigType(String name) {
        super(name, ConfigErrors.valueNotString);
    }

    @Override
    public boolean isConvertibleRawValue(@Nullable Object rawValue) {
        return rawValue instanceof String || rawValue instanceof Number || rawValue instanceof Boolean || rawValue instanceof Character;
    }

    @Override
    protected String fromRawValue(@NotNull Object rawValue) {
        return rawValue.toString();
    }

    @Override
    public Object toRawValue(@NotNull String configValue) {
        return configValue;
    }

}
