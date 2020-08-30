/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.mapped.converter;

import me.filoghost.fcommons.config.ConfigValue;
import me.filoghost.fcommons.config.ConfigValueType;
import me.filoghost.fcommons.config.exception.ConfigLoadException;
import me.filoghost.fcommons.config.exception.ConverterException;
import me.filoghost.fcommons.config.mapped.ConverterRegistry;
import me.filoghost.fcommons.reflection.TypeInfo;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ListConverter implements Converter<List<?>> {

	@Override
	public ConfigValue toConfigValue(TypeInfo fieldTypeInfo, List<?> fieldValue) throws ConverterException, ConfigLoadException {
		TypeInfo elementTypeInfo = getListElementTypeInfo(fieldTypeInfo);
		Converter<?> elementConverter = ConverterRegistry.find(elementTypeInfo.getTypeClass());

		List<ConfigValue> result = new ArrayList<>();
		for (Object fieldElement : fieldValue) {
			result.add(elementConverter.toConfigValueUnchecked(elementTypeInfo, fieldElement));
		}

		return ConfigValue.of(ConfigValueType.LIST, result);
	}

	@Override
	public List<?> toFieldValue(TypeInfo fieldTypeInfo, ConfigValue configValue) throws ConverterException, ConfigLoadException {
		if (!configValue.isPresentAs(ConfigValueType.LIST)) {
			return null;
		}

		TypeInfo elementTypeInfo = getListElementTypeInfo(fieldTypeInfo);
		Converter<?> elementConverter = ConverterRegistry.find(elementTypeInfo.getTypeClass());

		List<Object> result = new ArrayList<>();
		for (ConfigValue configElement : configValue.as(ConfigValueType.LIST)) {
			Object fieldValue = elementConverter.toFieldValue(elementTypeInfo, configElement);
			if (fieldValue != null) {
				result.add(fieldValue);
			}
		}

		return result;
	}

	private TypeInfo getListElementTypeInfo(TypeInfo typeInfo) throws ConverterException {
		Type[] typeArguments = typeInfo.getTypeArguments();

		if (typeArguments == null || typeArguments.length == 0) {
			throw new ConverterException("declaration omits generic type");
		}
		if (typeArguments.length != 1) {
			throw new ConverterException("declaration has more than 1 generic type");
		}

		try {
			return TypeInfo.of(typeArguments[0]);
		} catch (ReflectiveOperationException e) {
			throw new ConverterException("error while getting type info of " + typeArguments[0], e);
		}
	}

	@Override
	public boolean matches(Class<?> clazz) {
		return clazz == List.class;
	}

}
