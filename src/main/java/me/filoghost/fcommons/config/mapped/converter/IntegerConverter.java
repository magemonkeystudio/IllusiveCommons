/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.mapped.converter;

import me.filoghost.fcommons.config.ConfigValueType;

import java.lang.reflect.Type;

public class IntegerConverter implements Converter {

	@Override
	public ConfigValueType<?> getConfigValueType(Type[] fieldGenericTypes) {
		return ConfigValueType.INTEGER;
	}

	@Override
	public boolean matches(Class<?> type) {
		return type == Integer.class || type == int.class;
	}

}
