/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.type;

import me.filoghost.fcommons.config.ConfigErrors;
import me.filoghost.fcommons.config.ConfigSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class SectionConfigType extends ConfigType<ConfigSection> {

    public SectionConfigType(String name) {
        super(name, ConfigErrors.valueNotSection);
    }

    @Override
    public boolean isConvertibleRawValue(@Nullable Object rawValue) {
        return rawValue instanceof ConfigSection;
    }

    @Override
    protected ConfigSection fromRawValue(@NotNull Object rawValue) {
        return (ConfigSection) rawValue;
    }

    @Override
    public Object toRawValue(@NotNull ConfigSection configValue) {
        return configValue;
    }

}
