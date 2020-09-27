/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.mapped.converter;

import me.filoghost.fcommons.config.ConfigValue;
import me.filoghost.fcommons.config.ConfigValueType;
import me.filoghost.fcommons.config.exception.ConfigMappingException;
import me.filoghost.fcommons.config.exception.ConfigPostLoadException;
import me.filoghost.fcommons.reflection.TypeInfo;

public interface Converter<T, V> {

	ConfigValueType<V> getRequiredConfigValueType();

	default ConfigValue toConfigValue(TypeInfo<T> fieldTypeInfo, T fieldValue) throws ConfigMappingException {
		V configValue = toConfigValue0(fieldTypeInfo, fieldValue);
		if (configValue != null) {
			return ConfigValue.of(getRequiredConfigValueType(), configValue);
		} else {
			return ConfigValue.NULL;
		}
	}

	V toConfigValue0(TypeInfo<T> fieldTypeInfo, T fieldValue) throws ConfigMappingException;

	default T toFieldValue(TypeInfo<T> fieldTypeInfo, ConfigValue configValue, Object context) throws ConfigMappingException, ConfigPostLoadException {
		V expectedConfigValue = configValue.as(getRequiredConfigValueType());
		if (expectedConfigValue != null) {
			return toFieldValue0(fieldTypeInfo, expectedConfigValue, context);
		} else {
			return null;
		}
	}

	T toFieldValue0(TypeInfo<T> fieldTypeInfo, V configValue, Object context) throws ConfigMappingException, ConfigPostLoadException;

	default boolean equalsConfig(TypeInfo<T> fieldTypeInfo, T fieldValue, ConfigValue configValue) throws ConfigMappingException {
		V expectedConfigValue = configValue.as(getRequiredConfigValueType());
		return equalsConfig0(fieldTypeInfo, fieldValue, expectedConfigValue);
	}

	boolean equalsConfig0(TypeInfo<T> fieldTypeInfo, T fieldValue, V configValue) throws ConfigMappingException;

	boolean matches(Class<?> clazz);

}
