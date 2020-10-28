/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.valuetype;

import me.filoghost.fcommons.config.ConfigErrors;
import me.filoghost.fcommons.config.ConfigValueType;

public class BooleanConfigValueType extends ConfigValueType<Boolean> {

    public BooleanConfigValueType(String name) {
        super(name, ConfigErrors.valueNotBoolean);
    }

    @Override
    protected boolean isValidConfigValue(Object value) {
        return value instanceof Boolean;
    }

    @Override
    protected Boolean fromConfigValue(Object value) {
        return (Boolean) value;
    }

    @Override
    protected Object toConfigValue(Boolean value) {
        return value;
    }

}
