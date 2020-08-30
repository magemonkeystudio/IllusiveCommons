/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config;

import me.filoghost.fcommons.Preconditions;
import me.filoghost.fcommons.config.exception.InvalidConfigValueException;
import me.filoghost.fcommons.config.exception.MissingConfigValueException;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ConfigSection {

	private final ConfigurationSection yamlSection;

	public ConfigSection() {
		this.yamlSection = new MemoryConfiguration();
	}

	public ConfigSection(Map<String, Object> map) {
		Preconditions.notNull(map, "map");
		yamlSection = new MemoryConfiguration();
		for (Entry<String, Object> entry : map.entrySet()) {
			yamlSection.set(entry.getKey(), entry.getValue());
		}
	}

	public ConfigSection(ConfigurationSection yamlSection) {
		Preconditions.notNull(yamlSection, "yamlSection");
		this.yamlSection = yamlSection;
	}

	public ConfigValue get(String path) {
		return ConfigValue.fromRawConfigValue(path, getRawValue(path));
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
		setRawValue(path, configValueType.toConfigValueUnchecked(value));
	}

	public boolean contains(String path) {
		return getRawValue(path) != null;
	}

	public void remove(String path) {
		setRawValue(path, null);
	}

	public ConfigSection createSection(String path) {
		return new ConfigSection(yamlSection.createSection(path));
	}

	public Set<String> getKeys() {
		return yamlSection.getKeys(false);
	}

	private Object getRawValue(String path) {
		Preconditions.notEmpty(path, "path");
		return yamlSection.get(path, null);
	}

	private void setRawValue(String path, Object value) {
		Preconditions.notEmpty(path, "path");
		yamlSection.set(path, value);
	}

	protected ConfigurationSection getInternalYamlSection() {
		return yamlSection;
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
	public String toString() {
		return "ConfigSection{" +
				"yamlSection=" + yamlSection +
				"}";
	}
}
