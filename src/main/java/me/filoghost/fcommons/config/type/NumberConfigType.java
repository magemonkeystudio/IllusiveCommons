/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.type;

import me.filoghost.fcommons.config.ConfigErrors;

import java.util.function.Function;

class NumberConfigType<T extends Number> extends ConfigType<T> {

    private final Function<Number, T> toTypeFunction;

    public NumberConfigType(String name, Function<Number, T> toTypeFunction) {
        super(name, ConfigErrors.valueNotNumber);
        this.toTypeFunction = toTypeFunction;
    }

    @Override
    public boolean isValidRawValue(Object rawValue) {
        return rawValue instanceof Number;
    }

    @Override
    protected T fromRawValue(Object rawValue) {
        return toTypeFunction.apply((Number) rawValue);
    }

    @Override
    public Object toRawValue(T configValue) {
        return configValue;
    }

}