package me.filoghost.fcommons.config.mapped.converter;

import me.filoghost.fcommons.config.ConfigValue;
import me.filoghost.fcommons.config.exception.ConfigMappingException;
import me.filoghost.fcommons.config.exception.ConfigPostLoadException;
import me.filoghost.fcommons.config.exception.ConfigValueException;
import me.filoghost.fcommons.config.mapped.ConverterRegistry;
import me.filoghost.fcommons.config.mapped.ConfigSerializable;
import me.filoghost.fcommons.config.mapped.MappingUtils;
import me.filoghost.fcommons.reflection.TypeInfo;

import java.util.Objects;

public class ConfigSerializableConverter<E> implements Converter<ConfigSerializable<E>> {

	@SuppressWarnings("unchecked")
	@Override
	public ConfigValue toConfigValue(TypeInfo<ConfigSerializable<E>> fieldTypeInfo, ConfigSerializable<E> fieldValue) throws ConfigMappingException {
		TypeInfo<E> serializedParameterTypeInfo = (TypeInfo<E>) MappingUtils.getSingleGenericType(fieldTypeInfo);
		Converter<E> serializedParameterConverter = ConverterRegistry.find(serializedParameterTypeInfo);

		E serializedValue = fieldValue.serialize();
		return serializedParameterConverter.toConfigValue(serializedParameterTypeInfo, serializedValue);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ConfigSerializable<E> toFieldValue(TypeInfo<ConfigSerializable<E>> fieldTypeInfo, ConfigValue configValue) throws ConfigMappingException, ConfigPostLoadException {
		TypeInfo<E> serializedParameterTypeInfo = (TypeInfo<E>) MappingUtils.getSingleGenericType(fieldTypeInfo);
		Converter<E> serializedParameterConverter = ConverterRegistry.find(serializedParameterTypeInfo);

		E fieldValue = serializedParameterConverter.toFieldValue(serializedParameterTypeInfo, configValue);
		ConfigSerializable<E> configSerializable = MappingUtils.createInstance(fieldTypeInfo);

		try {
			configSerializable.deserialize(fieldValue);
		} catch (ConfigValueException e) {
			throw new ConfigMappingException(e.getMessage(), e.getCause());
		}
		return configSerializable;
	}

	@Override
	public boolean equals(TypeInfo<ConfigSerializable<E>> fieldTypeInfo, ConfigSerializable<E> o1, ConfigSerializable<E> o2) {
		if (o1 == o2) {
			return true;
		} else if (o1 == null || o2 == null) {
			return false;
		}

		return Objects.equals(o1.serialize(), o2.serialize());
	}

	@Override
	public boolean matches(Class<?> clazz) {
		return ConfigSerializable.class.isAssignableFrom(clazz);
	}

}
