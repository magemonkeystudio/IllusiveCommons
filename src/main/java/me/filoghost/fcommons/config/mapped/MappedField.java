/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.mapped;

import me.filoghost.fcommons.config.ConfigErrors;
import me.filoghost.fcommons.config.exception.ConfigMappingException;
import me.filoghost.fcommons.reflection.ReflectionUtils;
import me.filoghost.fcommons.reflection.TypeInfo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MappedField<T> {

	private final TypeInfo<T> typeInfo;
	private final Field field;
	private final String configPath;
	private final List<Annotation> annotations;

	public static MappedField<?> of(Field field) throws ReflectiveOperationException {
		return new MappedField<>(TypeInfo.of(field), field);
	}

	private MappedField(TypeInfo<T> typeInfo, Field field) {
		this.typeInfo = typeInfo;
		this.field = field;
		this.configPath = field.getName().replace("__", ".").replace("_", "-");
		this.annotations = Stream.concat(
				Arrays.stream(field.getDeclaredAnnotations()),
				Arrays.stream(field.getDeclaringClass().getDeclaredAnnotations()))
				.collect(Collectors.toList());
	}

	public T readFromObject(Object mappedObject) throws ConfigMappingException {
		try {
			return typeInfo.cast(ReflectionUtils.getFieldValue(field, mappedObject));
		} catch (ReflectiveOperationException e) {
			throw new ConfigMappingException(ConfigErrors.fieldReadError(this), e);
		}
	}

	public void writeToObject(Object mappedObject, T fieldValue) throws ConfigMappingException {
		try {
			ReflectionUtils.setFieldValue(field, mappedObject, fieldValue);
		} catch (ReflectiveOperationException e) {
			throw new ConfigMappingException(ConfigErrors.fieldWriteError(this), e);
		}
	}

	public TypeInfo<T> getTypeInfo() {
		return typeInfo;
	}

	public List<Annotation> getAnnotations() {
		return annotations;
	}

	public String getFieldName() {
		return field.getName();
	}

	public Class<?> getDeclaringClass() {
		return field.getDeclaringClass();
	}

	public String getConfigPath() {
		return configPath;
	}

}
