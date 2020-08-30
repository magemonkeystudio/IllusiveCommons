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
import me.filoghost.fcommons.config.exception.ConfigLoadException;
import me.filoghost.fcommons.config.exception.ConverterException;
import me.filoghost.fcommons.config.mapped.converter.Converter;
import me.filoghost.fcommons.config.mapped.modifier.ChatColorsModifier;
import me.filoghost.fcommons.config.mapped.modifier.ValueModifier;
import me.filoghost.fcommons.reflection.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class ConfigMapper<T> {

	private static final List<ValueModifier<?, ?>> VALUE_MODIFIERS = ImmutableList.of(
			new ChatColorsModifier()
	);

	private final Class<T> mappedClass;
	private List<MappedField> mappedFields;

	public ConfigMapper(Class<T> mappedClass) {
		this.mappedClass = mappedClass;
	}

	public T newMappedObjectInstance() throws ConfigLoadException {
		try {
			return ReflectionUtils.newInstance(mappedClass);
		} catch (NoSuchMethodException e) {
			throw new ConfigLoadException(ConfigErrors.noEmptyConstructor(mappedClass));
		} catch (ReflectiveOperationException e) {
			throw new ConfigLoadException(ConfigErrors.cannotCreateInstance(mappedClass), e);
		}
	}

	public List<PathAndConfigValue> getFieldsAsConfigValues(T mappedObject) throws ConfigLoadException {
		List<PathAndConfigValue> defaultValues = new ArrayList<>();

		for (MappedField mappedField : getMappedFields()) {
			try {
				Object fieldValue = mappedField.readFromObject(mappedObject);
				Converter<?> converter = ConverterRegistry.find(mappedField.getTypeInfo().getTypeClass());

				ConfigValue configValue;
				if (fieldValue != null) {
					configValue = converter.toConfigValueUnchecked(mappedField.getTypeInfo(), fieldValue);
				} else {
					configValue = ConfigValue.NULL;
				}

				defaultValues.add(new PathAndConfigValue(mappedField.getConfigPath(), configValue));

			} catch (ReflectiveOperationException e) {
				throw new ConfigLoadException(ConfigErrors.fieldReadError(mappedField), e);

			} catch (ConverterException e) {
				throw new ConfigLoadException(ConfigErrors.conversionFailed(mappedField), e);
			}
		}

		return defaultValues;
	}

	public void setConfigFromFields(T mappedObject, ConfigSection config) throws ConfigLoadException {
		List<PathAndConfigValue> configValues = getFieldsAsConfigValues(mappedObject);

		for (PathAndConfigValue configValue : configValues) {
			config.set(configValue.getPath(), configValue.getConfigValue());
		}
	}

	public boolean addMissingValuesToConfig(ConfigSection config, List<PathAndConfigValue> defaultValues) {
		boolean modified = false;

		for (PathAndConfigValue defaultValue : defaultValues) {
			if (!config.contains(defaultValue.getPath())) {
				config.set(defaultValue.getPath(), defaultValue.getConfigValue());
				modified = true;
			}
		}

		return modified;
	}

	public void setFieldsFromConfig(T mappedObject, ConfigSection config) throws ConfigLoadException {
		for (MappedField mappedField : getMappedFields()) {
			try {
				setFieldFromConfig(mappedObject, mappedField, config);

			} catch (ConverterException e) {
				throw new ConfigLoadException(ConfigErrors.conversionFailed(mappedField), e);

			} catch (ReflectiveOperationException e) {
				throw new ConfigLoadException(ConfigErrors.fieldWriteError(mappedField), e);
			}
		}
	}

	private void setFieldFromConfig(T mappedObject, MappedField mappedField, ConfigSection config)
			throws ReflectiveOperationException, ConverterException, ConfigLoadException {
		Converter<?> converter = ConverterRegistry.find(mappedField.getTypeInfo().getTypeClass());

		ConfigValue configValue = config.get(mappedField.getConfigPath());
		if (configValue == null) {
			return;
		}

		Object fieldValue = converter.toFieldValue(mappedField.getTypeInfo(), configValue);
		if (fieldValue == null) {
			return;
		}

		for (Annotation annotation : mappedField.getAnnotations()) {
			fieldValue = applyValueModifiers(fieldValue, annotation);
		}

		mappedField.writeToObject(mappedObject, fieldValue);
	}

	private Object applyValueModifiers(Object fieldValue, Annotation annotation) {
		for (ValueModifier<?, ?> modifier : VALUE_MODIFIERS) {
			if (modifier.isApplicable(annotation, fieldValue)) {
				fieldValue = modifier.transformUnchecked(annotation, fieldValue);
			}
		}
		return fieldValue;
	}

	private List<MappedField> getMappedFields() throws ConfigLoadException {
		if (mappedFields == null) {
			try {
				mappedFields = new ArrayList<>();
				for (Field field : ReflectionUtils.getDeclaredFields(mappedClass)) {
					if (isMappable(field)) {
						mappedFields.add(new MappedField(field));
					}
				}
			} catch (ReflectiveOperationException e) {
				throw new ConfigLoadException(ConfigErrors.mapperInitError(mappedClass), e);
			}
		}

		return mappedFields;
	}

	private boolean isMappable(Field field) {
		int modifiers = field.getModifiers();
		boolean includeStatic = field.isAnnotationPresent(IncludeStatic.class)
				|| field.getDeclaringClass().isAnnotationPresent(IncludeStatic.class);

		return (!Modifier.isStatic(modifiers) || includeStatic)
				|| !Modifier.isTransient(modifiers)
				|| !Modifier.isFinal(modifiers);
	}


}
