/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.mapped.converter;

import me.filoghost.fcommons.config.ConfigErrors;
import me.filoghost.fcommons.config.ConfigValue;
import me.filoghost.fcommons.config.ConfigValueType;
import me.filoghost.fcommons.config.exception.ConverterCastException;
import me.filoghost.fcommons.config.mapped.MappedField;

import java.lang.reflect.Type;

public interface Converter {

	ConfigValueType<?> getConfigValueType(Type[] fieldGenericTypes);

	boolean matches(Class<?> type);

	default ConfigValue toConfigValue(MappedField mappedField, Object value) throws ConverterCastException {
		ConfigValueType<?> configValueType = getConfigValueType(mappedField.getGenericTypes());

		// Assume that ConfigValueType is compatible with the value
		return toTypedConfigValue(configValueType, value);
	}

	@SuppressWarnings("unchecked")
	default <T> ConfigValue toTypedConfigValue(ConfigValueType<T> configValueType, Object value) throws ConverterCastException {
		try {
			return ConfigValue.of(configValueType, (T) value);
		} catch (ClassCastException e) {
			throw new ConverterCastException(ConfigErrors.converterFailed(value, this), e);
		}
	}

}
