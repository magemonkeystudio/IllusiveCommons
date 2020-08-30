/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.mapped.converter;

import me.filoghost.fcommons.config.ConfigValue;
import me.filoghost.fcommons.config.exception.ConfigLoadException;
import me.filoghost.fcommons.config.exception.ConverterException;
import me.filoghost.fcommons.reflection.TypeInfo;

public interface Converter<T> {

	@SuppressWarnings("unchecked")
	default ConfigValue toConfigValueUnchecked(TypeInfo fieldTypeInfo, Object fieldValue) throws ConverterException, ConfigLoadException {
		return toConfigValue(fieldTypeInfo, (T) fieldValue);
	}

	ConfigValue toConfigValue(TypeInfo fieldTypeInfo, T fieldValue) throws ConverterException, ConfigLoadException;

	T toFieldValue(TypeInfo fieldTypeInfo, ConfigValue configValue) throws ConverterException, ConfigLoadException;

	boolean matches(Class<?> clazz);

}
