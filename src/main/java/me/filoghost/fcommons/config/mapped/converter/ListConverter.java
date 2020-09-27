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
import java.util.stream.Collectors;

public class ListConverter<E> implements Converter<List<E>,List<ConfigValue>> {

	@Override
	public ConfigValueType<List<ConfigValue>> getRequiredConfigValueType() {
		return ConfigValueType.LIST;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ConfigValue> toConfigValue0(TypeInfo<List<E>> fieldTypeInfo, List<E> fieldValue) throws ConfigMappingException {
		TypeInfo<E> elementTypeInfo = (TypeInfo<E>) MappingUtils.getSingleGenericType(fieldTypeInfo);
		Converter<E, ?> elementConverter = ConverterRegistry.find(elementTypeInfo);

		List<ConfigValue> configList = new ArrayList<>();
		for (E fieldElement : fieldValue) {
			configList.add(elementConverter.toConfigValue(elementTypeInfo, fieldElement));
		}

		return configList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<E> toFieldValue0(TypeInfo<List<E>> fieldTypeInfo, List<ConfigValue> configList, Object context) throws ConfigMappingException, ConfigPostLoadException {
		TypeInfo<E> elementTypeInfo = (TypeInfo<E>) MappingUtils.getSingleGenericType(fieldTypeInfo);
		Converter<E, ?> elementConverter = ConverterRegistry.find(elementTypeInfo);

		List<E> fieldList = new ArrayList<>();
		for (ConfigValue configElement : configList) {
			E fieldValue = elementConverter.toFieldValue(elementTypeInfo, configElement, context);
			if (fieldValue != null) {
				fieldList.add(fieldValue);
			}
		}

		return fieldList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equalsConfig0(TypeInfo<List<E>> fieldTypeInfo, List<E> fieldList, List<ConfigValue> configList) throws ConfigMappingException {
		if (fieldList == null && configList == null) {
			return true;
		} else if (fieldList == null || configList == null) {
			return false;
		}

		TypeInfo<E> elementTypeInfo = (TypeInfo<E>) MappingUtils.getSingleGenericType(fieldTypeInfo);
		Converter<E, ?> elementConverter = ConverterRegistry.find(elementTypeInfo);

		// Skip elements that would be skipped during read
		List<ConfigValue> filteredConfigList = configList.stream()
				.filter(configElement -> configElement.isPresentAs(elementConverter.getRequiredConfigValueType()))
				.collect(Collectors.toList());

		if (filteredConfigList.size() != fieldList.size()) {
			return false;
		}

		for (int i = 0; i < filteredConfigList.size(); i++) {
			ConfigValue configElement = filteredConfigList.get(i);
			E fieldElement = fieldList.get(i);

			if (!elementConverter.equalsConfig(elementTypeInfo, fieldElement, configElement)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean matches(Class<?> clazz) {
		return clazz == List.class;
	}

}
