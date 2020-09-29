/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config;

import me.filoghost.fcommons.config.exception.InvalidConfigValueException;
import me.filoghost.fcommons.config.exception.MissingConfigValueException;
import me.filoghost.fcommons.config.valuetype.*;

import java.util.List;

public abstract class ConfigValueType<T> {

	public static final ConfigValueType<String> STRING = new StringConfigValueType("STRING");
	public static final ConfigValueType<Boolean> BOOLEAN = new BooleanConfigValueType("BOOLEAN");
	public static final ConfigValueType<Long> LONG = new NumberConfigValueType<>("LONG", Number::longValue);
	public static final ConfigValueType<Integer> INTEGER = new NumberConfigValueType<>("INTEGER", Number::intValue);
	public static final ConfigValueType<Short> SHORT = new NumberConfigValueType<>("SHORT", Number::shortValue);
	public static final ConfigValueType<Byte> BYTE = new NumberConfigValueType<>("BYTE", Number::byteValue);
	public static final ConfigValueType<Double> DOUBLE = new NumberConfigValueType<>("DOUBLE", Number::doubleValue);
	public static final ConfigValueType<Float> FLOAT = new NumberConfigValueType<>("FLOAT", Number::floatValue);

	public static final ConfigValueType<ConfigSection> SECTION = new SectionConfigValueType("SECTION");

	public static final ConfigValueType<List<ConfigValue>> LIST = new WrappedListConfigValueType("LIST");
	public static final ConfigValueType<List<String>> STRING_LIST = new ListConfigValueType<>("STRING_LIST", STRING);
	public static final ConfigValueType<List<Integer>> INTEGER_LIST = new ListConfigValueType<>("INTEGER_LIST", INTEGER);
	public static final ConfigValueType<List<ConfigSection>> SECTION_LIST = new ListConfigValueType<>("SECTION_LIST", SECTION);


	private final String name;
	protected final String notConvertibleErrorMessage;

	protected ConfigValueType(String name, String notConvertibleErrorMessage) {
		this.name = name;
		this.notConvertibleErrorMessage = notConvertibleErrorMessage;
	}

	@Override
	public String toString() {
		return name;
	}

	protected T fromConfigValueRequired(String path, Object rawConfigValue) throws MissingConfigValueException, InvalidConfigValueException {
		if (isValidNonNullConfigValue(rawConfigValue)) {
			return fromConfigValue(rawConfigValue);
		} else {
			if (rawConfigValue != null) {
				throw new InvalidConfigValueException(path, notConvertibleErrorMessage);
			} else {
				throw new MissingConfigValueException(path, ConfigErrors.valueNotSet);
			}
		}
	}

	protected T fromConfigValueOrNull(Object rawConfigValue) {
		return fromConfigValueOrDefault(rawConfigValue, null);
	}

	protected T fromConfigValueOrDefault(Object rawConfigValue, T defaultValue) {
		if (isValidNonNullConfigValue(rawConfigValue)) {
			return fromConfigValue(rawConfigValue);
		} else {
			return defaultValue;
		}
	}

	protected boolean isValidNonNullConfigValue(Object rawConfigValue) {
		return rawConfigValue != null && isValidConfigValue(rawConfigValue);
	}

	protected Object toConfigValueOrNull(T value) {
		if (value != null) {
			return toConfigValue(value);
		} else {
			return null;
		}
	}

	protected ConfigValue wrapRawValue(Object rawConfigValue) {
		return ConfigValue.ofRawConfigValue(null, rawConfigValue);
	}

	protected Object unwrapRawValue(ConfigValue configValue) {
		return configValue.getRawConfigValue();
	}

	protected static <T> T fromConfigValueOrNull(ConfigValueType<T> type, Object rawConfigValue) {
		return type.fromConfigValueOrNull(rawConfigValue);
	}

	protected abstract boolean isValidConfigValue(Object value);

	protected abstract T fromConfigValue(Object value);

	protected abstract Object toConfigValue(T value);

}
