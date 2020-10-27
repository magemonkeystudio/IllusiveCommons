/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.collection;

import me.filoghost.fcommons.Preconditions;
import me.filoghost.fcommons.Strings;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class Registry<T> {
	
	// Characters to ignore when searching enums by name
	private static final char[] KEY_IGNORE_CHARS = {'-', '_', ' '};
	
	private final Class<T> valuesType;
	private final Map<String, T> valuesMap;
	
	

	public static <T extends Enum<T>> Registry<T> fromEnumValues(Class<T> enumClass) {
		Registry<T> registry = new Registry<>(enumClass);
		registry.putAll(enumClass.getEnumConstants(), Enum::name);
		return registry;
	}
	
	
	public static <T> Registry<T> fromValues(T[] values, Function<T, String> toKeyFunction) {
		Registry<T> registry = new Registry<>(null);
		registry.putAll(values, toKeyFunction);
		return registry;
	}
	
	
	private Registry(Class<T> valuesType) {
		this.valuesType = valuesType;
		this.valuesMap = new CaseInsensitiveMap<>();
	}
	
	public Optional<T> find(String key) {
		if (key == null) {
			return Optional.empty();
		}
		return Optional.ofNullable(valuesMap.get(removeIgnoredChars(key)));
	}
	
	public void putIfEnumExists(String key, String enumValueName) {
		Preconditions.checkState(valuesType.isEnum(), "value type is not an enum");
		
		try {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			T enumValue = (T) Enum.valueOf((Class<Enum>) valuesType, enumValueName);
			put(key, enumValue);
		} catch (IllegalArgumentException e) {
			// Ignore, enum value doesn't exist
		}
	}
	
	private void putAll(T[] enumValues, Function<T, String> toKeyFunction) {
		for (T enumValue : enumValues) {
			valuesMap.put(removeIgnoredChars(toKeyFunction.apply(enumValue)), enumValue);
		}
	}
	
	public void put(String key, T enumValue) {
		valuesMap.put(removeIgnoredChars(key), enumValue);
	}
	
	private String removeIgnoredChars(String enumValueName) {
		return Strings.stripChars(enumValueName, KEY_IGNORE_CHARS);
	}

	@Override
	public String toString() {
		return "Registry [type=" + valuesType + ", values=" + valuesMap + "]";
	}

}
