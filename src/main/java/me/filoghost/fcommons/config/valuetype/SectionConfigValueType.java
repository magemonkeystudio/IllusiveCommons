/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.valuetype;

import me.filoghost.fcommons.config.ConfigErrors;
import me.filoghost.fcommons.config.ConfigSection;
import me.filoghost.fcommons.config.ConfigValueType;

public class SectionConfigValueType extends ConfigValueType<ConfigSection> {

    public SectionConfigValueType(String name) {
        super(name, ConfigErrors.valueNotSection);
    }

    @Override
    protected boolean isValidConfigValue(Object value) {
        return value instanceof ConfigSection;
    }

    @Override
    protected ConfigSection fromConfigValue(Object value) {
        return (ConfigSection) value;
    }

    @Override
    protected Object toConfigValue(ConfigSection value) {
        return value;
    }

}
