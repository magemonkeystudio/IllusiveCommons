/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.mapped;

import me.filoghost.fcommons.reflection.ReflectionUtils;
import me.filoghost.fcommons.reflection.TypeInfo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MappedField {

	private final Field field;
	private final String configPath;
	private final TypeInfo typeInfo;
	private final List<Annotation> annotations;


	public MappedField(Field field) throws ReflectiveOperationException {
		this.field = field;
		this.configPath = field.getName().replace("__", ".").replace("_", "-");
		this.typeInfo = TypeInfo.of(field);
		this.annotations = Stream.concat(
				Arrays.stream(field.getDeclaredAnnotations()),
				Arrays.stream(field.getDeclaringClass().getDeclaredAnnotations()))
				.collect(Collectors.toList());
	}

	public Object readFromObject(Object mappedObject) throws ReflectiveOperationException {
		return ReflectionUtils.getFieldValue(field, mappedObject);
	}

	public void writeToObject(Object mappedObject, Object fieldValue) throws ReflectiveOperationException {
		ReflectionUtils.setFieldValue(field, mappedObject, fieldValue);
	}

	public TypeInfo getTypeInfo() {
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
