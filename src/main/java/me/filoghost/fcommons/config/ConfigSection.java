/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config;

import me.filoghost.fcommons.Preconditions;
import me.filoghost.fcommons.config.exception.InvalidConfigValueException;
import me.filoghost.fcommons.config.exception.MissingConfigValueException;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ConfigSection {

	private LinkedHashMap<String, Object> values;

	public ConfigSection() {
		setInternalValues(new LinkedHashMap<>());
	}

	protected ConfigSection(LinkedHashMap<String, Object> values) {
		setInternalValues(values);
	}

	protected void setInternalValues(LinkedHashMap<String, Object> values) {
		Preconditions.notNull(values, "values");
		this.values = values;
	}

	protected LinkedHashMap<String, Object> getInternalValues() {
		return values;
	}

	public ConfigValue get(String path) {
		return ConfigValue.ofRawConfigValue(path, getRawValue(path));
	}

	public <T> T get(String path, ConfigValueType<T> configValueType) {
		return getOrDefault(path, configValueType, null);
	}

	public <T> T getRequired(String path, ConfigValueType<T> configValueType) throws MissingConfigValueException, InvalidConfigValueException {
		return configValueType.fromConfigValueRequired(path, getRawValue(path));
	}

	public <T> T getOrDefault(String path, ConfigValueType<T> configValueType, T defaultValue) {
		return configValueType.fromConfigValueOrDefault(getRawValue(path), defaultValue);
	}

	public <T> void set(String path, ConfigValue configValue) {
		setRawValue(path, configValue.getRawConfigValue());
	}

	public <T> void set(String path, ConfigValueType<T> configValueType, T value) {
		setRawValue(path, configValueType.toConfigValueOrNull(value));
	}

	public boolean contains(String path) {
		return getRawValue(path) != null;
	}

	public void remove(String path) {
		setRawValue(path, null);
	}

	public ConfigSection getOrCreateSection(String path) {
		ConfigSection section = getConfigSection(path);
		if (section == null) {
			section =  new ConfigSection();
			setConfigSection(path, section);
		}
		return section;
	}

	public Set<String> getKeys() {
		return new LinkedHashSet<>(values.keySet());
	}

	private Object getRawValue(String path) {
		Preconditions.notEmpty(path, "path");

		int separatorIndex = path.indexOf('.');
		if (separatorIndex >= 0) {
			String firstPath = path.substring(0, separatorIndex);
			String remainingPath = path.substring(separatorIndex + 1);
			ConfigSection section = getConfigSection(firstPath);

			if (section != null) {
				return section.getRawValue(remainingPath);
			} else {
				return null;
			}
		} else {
			return values.get(path);
		}
	}

	private void setRawValue(String path, Object value) {
		Preconditions.notEmpty(path, "path");

		int separatorIndex = path.indexOf('.');
		if (separatorIndex >= 0) {
			String firstPath = path.substring(0, separatorIndex);
			String remainingPath = path.substring(separatorIndex + 1);
			ConfigSection section = getConfigSection(firstPath);

			if (section == null) {
				if (value == null) {
					return;
				} else {
					section = getOrCreateSection(firstPath);
				}
			}

			section.setRawValue(remainingPath, value);
		} else {
			if (value != null) {
				values.put(path, value);
			} else {
				values.remove(path);
			}
		}
	}

	/*
	 * Convenience getRequired{TYPE} alias methods
	 */

	public String getRequiredString(String path) throws MissingConfigValueException, InvalidConfigValueException {
		return getRequired(path, ConfigValueType.STRING);
	}

	public boolean getRequiredBoolean(String path) throws MissingConfigValueException, InvalidConfigValueException {
		return getRequired(path, ConfigValueType.BOOLEAN);
	}

	public int getRequiredInt(String path) throws MissingConfigValueException, InvalidConfigValueException {
		return getRequired(path, ConfigValueType.INTEGER);
	}

	public double getRequiredDouble(String path) throws MissingConfigValueException, InvalidConfigValueException {
		return getRequired(path, ConfigValueType.DOUBLE);
	}

	public List<String> getRequiredStringList(String path) throws MissingConfigValueException, InvalidConfigValueException {
		return getRequired(path, ConfigValueType.STRING_LIST);
	}

	public ConfigSection getRequiredConfigSection(String path) throws MissingConfigValueException, InvalidConfigValueException {
		return getRequired(path, ConfigValueType.SECTION);
	}

	/*
	 * Convenience get{TYPE} (without defaults) alias methods
	 */
	public String getString(String path) {
		return getOrDefault(path, ConfigValueType.STRING, null);
	}

	public boolean getBoolean(String path) {
		return getOrDefault(path, ConfigValueType.BOOLEAN, false);
	}

	public int getInt(String path) {
		return getOrDefault(path, ConfigValueType.INTEGER, 0);
	}

	public double getDouble(String path) {
		return getOrDefault(path, ConfigValueType.DOUBLE, 0.0);
	}

	public List<String> getStringList(String path) {
		return getOrDefault(path, ConfigValueType.STRING_LIST, null);
	}

	public ConfigSection getConfigSection(String path) {
		return getOrDefault(path, ConfigValueType.SECTION, null);
	}

	/*
	 * Convenience get{TYPE} (with defaults) alias methods
	 */
	public String getString(String path, String defaultValue) {
		return getOrDefault(path, ConfigValueType.STRING, defaultValue);
	}

	public boolean getBoolean(String path, boolean defaultValue) {
		return getOrDefault(path, ConfigValueType.BOOLEAN, defaultValue);
	}

	public int getInt(String path, int defaultValue) {
		return getOrDefault(path, ConfigValueType.INTEGER, defaultValue);
	}

	public double getDouble(String path, double defaultValue) {
		return getOrDefault(path, ConfigValueType.DOUBLE, defaultValue);
	}

	public List<String> getStringList(String path, List<String> defaultValue) {
		return getOrDefault(path, ConfigValueType.STRING_LIST, defaultValue);
	}

	public ConfigSection getConfigSection(String path, ConfigSection defaultValue) {
		return getOrDefault(path, ConfigValueType.SECTION, defaultValue);
	}


	/*
	 * Convenience set{TYPE} alias methods
	 */
	public void setString(String path, String value) {
		set(path, ConfigValueType.STRING, value);
	}

	public void setBoolean(String path, boolean value) {
		set(path, ConfigValueType.BOOLEAN, value);
	}

	public void setInt(String path, int value) {
		set(path, ConfigValueType.INTEGER, value);
	}

	public void setDouble(String path, double value) {
		set(path, ConfigValueType.DOUBLE, value);
	}

	public void setStringList(String path, List<String> value) {
		set(path, ConfigValueType.STRING_LIST, value);
	}

	public void setConfigSection(String path, ConfigSection value) {
		set(path, ConfigValueType.SECTION, value);
	}

	@Override
	public final boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ConfigSection)) {
			return false;
		}
		ConfigSection other = (ConfigSection) obj;
		return this.values.equals(other.values);
	}

	@Override
	public final int hashCode() {
		return values.hashCode();
	}

	@Override
	public String toString() {
		return "ConfigSection{" + values + "}";
	}
}
