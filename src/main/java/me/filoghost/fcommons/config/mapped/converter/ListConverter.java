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
import me.filoghost.fcommons.config.mapped.ConverterRegistry;
import me.filoghost.fcommons.config.mapped.MappingUtils;
import me.filoghost.fcommons.reflection.TypeInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListConverter<E> implements Converter<List<E>> {

	@SuppressWarnings("unchecked")
	@Override
	public ConfigValue toConfigValue(TypeInfo<List<E>> fieldTypeInfo, List<E> fieldValue) throws ConfigMappingException {
		TypeInfo<E> elementTypeInfo = (TypeInfo<E>) MappingUtils.getSingleGenericType(fieldTypeInfo);
		Converter<E> elementConverter = ConverterRegistry.find(elementTypeInfo);

		List<ConfigValue> result = new ArrayList<>();
		for (E fieldElement : fieldValue) {
			result.add(elementConverter.toConfigValue(elementTypeInfo, fieldElement));
		}

		return ConfigValue.of(ConfigValueType.LIST, result);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<E> toFieldValue(TypeInfo<List<E>> fieldTypeInfo, ConfigValue configValue, Object context) throws ConfigMappingException, ConfigPostLoadException {
		if (!configValue.isPresentAs(ConfigValueType.LIST)) {
			return null;
		}

		TypeInfo<E> elementTypeInfo = (TypeInfo<E>) MappingUtils.getSingleGenericType(fieldTypeInfo);
		Converter<E> elementConverter = ConverterRegistry.find(elementTypeInfo);

		List<E> result = new ArrayList<>();
		for (ConfigValue configElement : configValue.as(ConfigValueType.LIST)) {
			E fieldValue = elementConverter.toFieldValue(elementTypeInfo, configElement, context);
			if (fieldValue != null) {
				result.add(fieldValue);
			}
		}

		return result;
	}

	@Override
	public boolean equals(TypeInfo<List<E>> fieldTypeInfo, List<E> o1, List<E> o2) {
		return Objects.equals(o1, o2);
	}

	@Override
	public boolean matches(Class<?> clazz) {
		return clazz == List.class;
	}

}
