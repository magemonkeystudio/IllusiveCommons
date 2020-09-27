/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.mapped.converter;

import me.filoghost.fcommons.config.ConfigValueType;
import me.filoghost.fcommons.reflection.TypeInfo;

import java.util.Objects;

public class ConfigValueTypeConverter<T> implements Converter<T, T> {

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
	public ConfigValueType<T> getRequiredConfigValueType() {
		return configValueType;
	}

	@Override
	public T toConfigValue0(TypeInfo<T> fieldTypeInfo, T fieldValue) {
		return fieldValue;
	}

	@Override
	public T toFieldValue0(TypeInfo<T> fieldTypeInfo, T configValue, Object context) {
		return configValue;
	}

	@Override
	public boolean equalsConfig0(TypeInfo<T> fieldTypeInfo, T fieldValue, T configValue) {
		return Objects.equals(fieldValue, configValue);
	}

	@Override
	public boolean matches(Class<?> clazz) {
		return clazz == mainClass || (primitiveClass != null && clazz == primitiveClass);
	}

}
