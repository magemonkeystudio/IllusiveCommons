/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.mapped.converter;

import me.filoghost.fcommons.config.ConfigValueType;

import java.lang.reflect.Type;

public class BooleanConverter implements Converter {

	@Override
	public ConfigValueType<?> getConfigValueType(Type[] fieldGenericTypes) {
		return ConfigValueType.BOOLEAN;
	}

	@Override
	public boolean matches(Class<?> type) {
		return type == Boolean.class || type == boolean.class;
	}

}
