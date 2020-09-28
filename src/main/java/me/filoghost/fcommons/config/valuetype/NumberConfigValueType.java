/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */

package me.filoghost.fcommons.config.valuetype;

import me.filoghost.fcommons.config.ConfigErrors;
import me.filoghost.fcommons.config.ConfigValueType;

import java.util.function.Function;

public class NumberConfigValueType<T extends Number> extends ConfigValueType<T> {

	private final Function<Number, T> toTypeFunction;

	public NumberConfigValueType(Function<Number, T> toTypeFunction) {
		super(ConfigErrors.valueNotNumber);
		this.toTypeFunction = toTypeFunction;
	}

	@Override
	protected boolean isValidConfigValue(Object value) {
		return value instanceof Number;
	}

	@Override
	protected T fromConfigValue(Object value) {
    	return toTypeFunction.apply((Number) value);
	}

	@Override
	protected Object toConfigValue(T value) {
    	return value;
	}

}
