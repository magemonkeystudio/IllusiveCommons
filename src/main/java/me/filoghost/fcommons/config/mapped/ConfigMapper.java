/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.mapped;

import com.google.common.collect.ImmutableList;
import me.filoghost.fcommons.config.ConfigErrors;
import me.filoghost.fcommons.config.ConfigSection;
import me.filoghost.fcommons.config.ConfigValue;
import me.filoghost.fcommons.config.exception.ConfigMappingException;
import me.filoghost.fcommons.config.exception.ConfigPostLoadException;
import me.filoghost.fcommons.config.mapped.converter.Converter;
import me.filoghost.fcommons.config.mapped.modifier.ChatColorsModifier;
import me.filoghost.fcommons.config.mapped.modifier.ValueModifier;
import me.filoghost.fcommons.reflection.ReflectionUtils;
import me.filoghost.fcommons.reflection.TypeInfo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ConfigMapper<T> {

	private static final List<ValueModifier<?, ?>> VALUE_MODIFIERS = ImmutableList.of(
			new ChatColorsModifier()
	);

	private final TypeInfo<T> mappedTypeInfo;
	private final List<MappedField<?>> mappedFields;

	public ConfigMapper(Class<T> mappedClass) throws ConfigMappingException {
		try {
			this.mappedTypeInfo = TypeInfo.of(mappedClass);
			ImmutableList.Builder<MappedField<?>> mappedFieldsBuilder = ImmutableList.builder();
			for (Field field : ReflectionUtils.getDeclaredFields(mappedClass)) {
				if (isMappable(field)) {
					mappedFieldsBuilder.add(MappedField.of(field));
				}
			}
			this.mappedFields = mappedFieldsBuilder.build();
		} catch (ReflectiveOperationException e) {
			throw new ConfigMappingException(ConfigErrors.mapperReflectionException(mappedClass), e);
		}
	}

	public T newMappedObjectInstance() throws ConfigMappingException {
		return MappingUtils.createInstance(mappedTypeInfo);
	}

	public Map<String, ConfigValue> getFieldsAsConfigValues(T mappedObject) throws ConfigMappingException {
		Map<String, ConfigValue> configValues = new LinkedHashMap<>();

		for (MappedField<?> mappedField : mappedFields) {
			try {
				ConfigValue configValue = getFieldAsConfigValue(mappedObject, mappedField);
				configValues.put(mappedField.getConfigPath(), configValue);
			} catch (ConfigMappingException e) {
				// Display field information in exception
				throw new ConfigMappingException(ConfigErrors.conversionFailed(mappedField));
			}
		}

		return configValues;
	}

	private <F> ConfigValue getFieldAsConfigValue(T mappedObject, MappedField<F> mappedField) throws ConfigMappingException {
		TypeInfo<F> fieldTypeInfo = mappedField.getTypeInfo();
		F fieldValue = mappedField.readFromObject(mappedObject);
		Converter<F, ?> converter = ConverterRegistry.find(fieldTypeInfo);

		if (fieldValue != null) {
			return converter.toConfigValue(fieldTypeInfo, fieldValue);
		} else {
			return ConfigValue.NULL;
		}
	}

	public void setConfigFromFields(T mappedObject, ConfigSection config) throws ConfigMappingException {
		Map<String, ConfigValue> configValues = getFieldsAsConfigValues(mappedObject);

		for (Entry<String, ConfigValue> entry : configValues.entrySet()) {
			config.set(entry.getKey(), entry.getValue());
		}
	}

	@SuppressWarnings("unchecked")
	public void setFieldsFromConfig(T mappedObject, ConfigSection config, Object context) throws ConfigMappingException, ConfigPostLoadException {
		for (MappedField<?> mappedField : mappedFields) {
			try {
				setFieldFromConfig(mappedObject, mappedField, config, context);
			} catch (ConfigMappingException e) {
				// Display field information in exception
				throw new ConfigMappingException(ConfigErrors.conversionFailed(mappedField), e);
			}
		}

		// After injecting fields, make sure the config object is alerted
		if (mappedObject instanceof PostLoadCallback) {
			((PostLoadCallback) mappedObject).postLoad();
		}
		if (mappedObject instanceof ContextualPostLoadCallback) {
			((ContextualPostLoadCallback<Object>) mappedObject).postLoad(context);
		}
	}

	private <F> void setFieldFromConfig(T mappedObject, MappedField<F> mappedField, ConfigSection config, Object context) throws ConfigMappingException, ConfigPostLoadException {
		Converter<F, ?> converter = ConverterRegistry.find(mappedField.getTypeInfo());
		ConfigValue configValue = config.get(mappedField.getConfigPath());
		if (configValue == null) {
			return;
		}

		F fieldValue = converter.toFieldValue(mappedField.getTypeInfo(), configValue, context);
		if (fieldValue == null) {
			return;
		}

		for (Annotation annotation : mappedField.getAnnotations()) {
			fieldValue = applyValueModifiers(fieldValue, annotation);
		}

		// Field is written only if new value is not null (default field value is kept)
		mappedField.writeToObject(mappedObject, fieldValue);
	}

	@SuppressWarnings("unchecked")
	private <F, A extends Annotation> F applyValueModifiers(F fieldValue, A annotation) {
		for (ValueModifier<?, ?> modifier : VALUE_MODIFIERS) {
			if (modifier.isApplicable(annotation, fieldValue)) {
				fieldValue = ((ValueModifier<F, A>) modifier).transform(annotation, fieldValue);
			}
		}
		return fieldValue;
	}

	private boolean isMappable(Field field) {
		int modifiers = field.getModifiers();
		boolean includeStatic = field.isAnnotationPresent(IncludeStatic.class)
				|| field.getDeclaringClass().isAnnotationPresent(IncludeStatic.class);

		return (!Modifier.isStatic(modifiers) || includeStatic)
				|| !Modifier.isTransient(modifiers)
				|| !Modifier.isFinal(modifiers);
	}

	public boolean equalsConfig(T mappedObject, ConfigSection config) throws ConfigMappingException {
		for (MappedField<?> mappedField : mappedFields) {
			if (!fieldEqualsConfigValue(mappedField, mappedObject, config)) {
				return false;
			}
		}

		return true;
	}

	private <F> boolean fieldEqualsConfigValue(MappedField<F> mappedField, T mappedObject, ConfigSection config) throws ConfigMappingException {
		F fieldValue = mappedField.readFromObject(mappedObject);
		ConfigValue configValue = config.get(mappedField.getConfigPath());

		Converter<F, ?> converter = ConverterRegistry.find(mappedField.getTypeInfo());
		return converter.equalsConfig(mappedField.getTypeInfo(), fieldValue, configValue);
	}

}
