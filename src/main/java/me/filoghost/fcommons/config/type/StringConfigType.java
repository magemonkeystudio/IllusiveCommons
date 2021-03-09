/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.type;

import me.filoghost.fcommons.config.ConfigErrors;

class StringConfigType extends ConfigType<String> {

    public StringConfigType(String name) {
        super(name, ConfigErrors.valueNotString);
    }

    @Override
    public boolean isValidRawValue(Object rawValue) {
        return rawValue instanceof String || rawValue instanceof Number || rawValue instanceof Boolean || rawValue instanceof Character;
    }

    @Override
    protected String fromRawValue(Object rawValue) {
        return rawValue.toString();
    }

    @Override
    public Object toRawValue(String configValue) {
        return configValue;
    }

}
