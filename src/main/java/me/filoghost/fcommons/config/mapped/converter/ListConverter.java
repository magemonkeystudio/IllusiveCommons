/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.mapped.converter;

import me.filoghost.fcommons.Preconditions;
import me.filoghost.fcommons.config.ConfigValueType;

import java.lang.reflect.Type;
import java.util.List;

public class ListConverter implements Converter {

	@Override
	public ConfigValueType<?> getConfigValueType(Type[] fieldGenericTypes) {
		Preconditions.notNull(fieldGenericTypes, "fieldGenericTypes");
		Preconditions.checkArgument(fieldGenericTypes.length == 1, "fieldGenericTypes length must be 1");

		Type listType = fieldGenericTypes[0];

		if (listType == Integer.class) {
			return ConfigValueType.INTEGER_LIST;
		} else if (listType == String.class) {
			return ConfigValueType.STRING_LIST;
		} else {
			throw new IllegalArgumentException("unsupported list type: " + listType);
		}
	}

	@Override
	public boolean matches(Class<?> type) {
		return type == List.class;
	}

}
