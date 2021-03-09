/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.type;

import me.filoghost.fcommons.config.ConfigErrors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class BooleanConfigType extends ConfigType<Boolean> {

    public BooleanConfigType(String name) {
        super(name, ConfigErrors.valueNotBoolean);
    }

    @Override
    public boolean isConvertibleRawValue(@Nullable Object rawValue) {
        return rawValue instanceof Boolean;
    }

    @Override
    protected Boolean fromRawValue(@NotNull Object rawValue) {
        return (Boolean) rawValue;
    }

    @Override
    public Object toRawValue(@NotNull Boolean configValue) {
        return configValue;
    }

}
