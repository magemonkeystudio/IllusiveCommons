/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config;

import me.filoghost.fcommons.Preconditions;
import me.filoghost.fcommons.config.exception.InvalidConfigValueException;
import me.filoghost.fcommons.config.exception.MissingConfigValueException;

public class ConfigValue {

	public static final ConfigValue NULL = new ConfigValue(null, null);

	private final String path;
	private final Object rawConfigValue;

	public static <T> ConfigValue of(ConfigValueType<T> valueType, T value) {
		Preconditions.notNull(valueType, "valueType");
		Preconditions.notNull(value, "value");
		return new ConfigValue(null, valueType.toConfigValueOrNull(value));
	}

	protected static ConfigValue ofRawConfigValue(String path, Object rawConfigValue) {
		return new ConfigValue(path, rawConfigValue);
	}

	private ConfigValue(String path, Object rawConfigValue) {
		this.path = path;
		this.rawConfigValue = rawConfigValue;
	}

	protected Object getRawConfigValue() {
		return rawConfigValue;
	}

	public final <T> T as(ConfigValueType<T> valueType) {
		return valueType.fromConfigValueOrDefault(rawConfigValue, null);
	}

	public final <T> T asRequired(ConfigValueType<T> valueType) throws MissingConfigValueException, InvalidConfigValueException {
		return valueType.fromConfigValueRequired(path, rawConfigValue);
	}

	public final <T> T asOrDefault(ConfigValueType<T> valueType, T defaultValue) {
		return valueType.fromConfigValueOrDefault(rawConfigValue, defaultValue);
	}

	public boolean isPresentAs(ConfigValueType<?> configValueType) {
		return configValueType.isValidNonNullConfigValue(rawConfigValue);
	}

	@Override
	public String toString() {
		return "ConfigValue{"
				+ "path=" + path
				+ ", rawValueType=" + (rawConfigValue != null ? rawConfigValue.getClass().getSimpleName() : "null")
				+ ", rawValue=" + rawConfigValue
				+ "}";
	}
}
