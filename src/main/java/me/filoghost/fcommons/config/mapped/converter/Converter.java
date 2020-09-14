/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.mapped.converter;

import me.filoghost.fcommons.config.ConfigValue;
import me.filoghost.fcommons.config.exception.ConfigMappingException;
import me.filoghost.fcommons.config.exception.ConfigPostLoadException;
import me.filoghost.fcommons.reflection.TypeInfo;

public interface Converter<T> {

	ConfigValue toConfigValue(TypeInfo<T> fieldTypeInfo, T fieldValue) throws ConfigMappingException;

	T toFieldValue(TypeInfo<T> fieldTypeInfo, ConfigValue configValue) throws ConfigMappingException, ConfigPostLoadException;

	boolean equals(TypeInfo<T> fieldTypeInfo, T o1, T o2) throws ConfigMappingException;

	boolean matches(Class<?> clazz);

}
