/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.valuetype;

import me.filoghost.fcommons.config.ConfigErrors;
import me.filoghost.fcommons.config.ConfigValueType;

import java.util.ArrayList;
import java.util.List;

public class ListConfigValueType<E> extends ConfigValueType<List<E>> {

    private final ConfigValueType<E> elementType;

    public ListConfigValueType(String name, ConfigValueType<E> elementType) {
        super(name, ConfigErrors.valueNotList);
        this.elementType = elementType;
    }

    @Override
    protected boolean isValidConfigValue(Object value) {
        return value instanceof List;
    }

    @Override
    protected List<E> fromConfigValue(Object value) {
        List<E> result = new ArrayList<>();

        for (Object element : (List<?>) value) {
            E convertedElement = fromConfigValueOrNull(elementType, element);
            if (convertedElement != null) {
                result.add(convertedElement);
            }
        }

        return result;
    }

    @Override
    protected Object toConfigValue(List<E> value) {
        return value;
    }

}
