/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.type;

import me.filoghost.fcommons.config.ConfigErrors;
import me.filoghost.fcommons.config.ConfigValue;

import java.util.ArrayList;
import java.util.List;

class WrappedListConfigType extends ConfigType<List<ConfigValue>> {

    public WrappedListConfigType(String name) {
        super(name, ConfigErrors.valueNotList);
    }

    @Override
    public boolean isValidRawValue(Object rawValue) {
        return rawValue instanceof List;
    }

    @Override
    protected List<ConfigValue> fromRawValue(Object rawValue) {
        List<ConfigValue> configValue = new ArrayList<>();

        for (Object element : (List<?>) rawValue) {
            configValue.add(ConfigValue.wrapRawConfigValue(null, element));
        }

        return configValue;
    }

    @Override
    public Object toRawValue(List<ConfigValue> configValue) {
        List<Object> rawValue = new ArrayList<>();

        for (ConfigValue element : configValue) {
            rawValue.add(element.getRawValue());
        }

        return rawValue;
    }

}
