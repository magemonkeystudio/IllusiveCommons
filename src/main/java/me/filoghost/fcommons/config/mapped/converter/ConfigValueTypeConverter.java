/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.mapped.converter;

import me.filoghost.fcommons.config.ConfigValue;
import me.filoghost.fcommons.config.ConfigValueType;
import me.filoghost.fcommons.reflection.TypeInfo;

import java.util.Objects;

public class ConfigValueTypeConverter<T> implements Converter<T> {

	private final Class<T> mainClass;
	private final Class<?> primitiveClass;
	private final ConfigValueType<T> configValueType;

	public ConfigValueTypeConverter(ConfigValueType<T> configValueType, Class<T> mainClass) {
		this(configValueType, mainClass, null);
	}

	public ConfigValueTypeConverter(ConfigValueType<T> configValueType, Class<T> mainClass, Class<?> primitiveClass) {
		this.mainClass = mainClass;
		this.primitiveClass = primitiveClass;
		this.configValueType = configValueType;
	}

	@Override
	public ConfigValue toConfigValue(TypeInfo<T> fieldTypeInfo, T fieldValue) {
		if (fieldValue != null) {
			return ConfigValue.of(configValueType, fieldValue);
		} else {
			return ConfigValue.NULL;
		}
	}

	@Override
	public T toFieldValue(TypeInfo<T> fieldTypeInfo, ConfigValue configValue) {
		return configValue.as(configValueType);
	}

	@Override
	public boolean equals(TypeInfo<T> fieldTypeInfo, T o1, T o2) {
		return Objects.equals(o1, o2);
	}

	@Override
	public boolean matches(Class<?> clazz) {
		return clazz == mainClass || (primitiveClass != null && clazz == primitiveClass);
	}

}
