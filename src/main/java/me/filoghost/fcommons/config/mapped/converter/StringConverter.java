/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.mapped.converter;

import me.filoghost.fcommons.config.ConfigValueType;

import java.lang.reflect.Type;

public class StringConverter implements Converter {

	@Override
	public ConfigValueType<?> getConfigValueType(Type[] fieldGenericTypes) {
		return ConfigValueType.STRING;
	}

	@Override
	public boolean matches(Class<?> type) {
		return type == String.class;
	}

}
