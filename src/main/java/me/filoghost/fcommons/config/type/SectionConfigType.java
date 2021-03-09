/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.type;

import me.filoghost.fcommons.config.ConfigErrors;
import me.filoghost.fcommons.config.ConfigSection;

class SectionConfigType extends ConfigType<ConfigSection> {

    public SectionConfigType(String name) {
        super(name, ConfigErrors.valueNotSection);
    }

    @Override
    public boolean isValidRawValue(Object rawValue) {
        return rawValue instanceof ConfigSection;
    }

    @Override
    protected ConfigSection fromRawValue(Object rawValue) {
        return (ConfigSection) rawValue;
    }

    @Override
    public Object toRawValue(ConfigSection configValue) {
        return configValue;
    }

}
