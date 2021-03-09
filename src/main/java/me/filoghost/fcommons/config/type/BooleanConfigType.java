/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.type;

import me.filoghost.fcommons.config.ConfigErrors;

class BooleanConfigType extends ConfigType<Boolean> {

    public BooleanConfigType(String name) {
        super(name, ConfigErrors.valueNotBoolean);
    }

    @Override
    public boolean isValidRawValue(Object rawValue) {
        return rawValue instanceof Boolean;
    }

    @Override
    protected Boolean fromRawValue(Object rawValue) {
        return (Boolean) rawValue;
    }

    @Override
    public Object toRawValue(Boolean configValue) {
        return configValue;
    }

}
