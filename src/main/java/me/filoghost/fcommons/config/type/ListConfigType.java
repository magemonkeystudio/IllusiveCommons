/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.type;

import me.filoghost.fcommons.config.ConfigErrors;

import java.util.ArrayList;
import java.util.List;

class ListConfigType<E> extends ConfigType<List<E>> {

    private final ConfigType<E> elementType;

    public ListConfigType(String name, ConfigType<E> elementType) {
        super(name, ConfigErrors.valueNotList);
        this.elementType = elementType;
    }

    @Override
    public boolean isValidRawValue(Object rawValue) {
        return rawValue instanceof List;
    }

    @Override
    protected List<E> fromRawValue(Object rawValue) {
        List<E> configValue = new ArrayList<>();

        for (Object rawElement : (List<?>) rawValue) {
            E configElement = elementType.fromRawValueOrNull(rawElement);
            if (configElement != null) {
                configValue.add(configElement);
            }
        }

        return configValue;
    }

    @Override
    public Object toRawValue(List<E> configValue) {
        return new ArrayList<>(configValue);
    }

}
