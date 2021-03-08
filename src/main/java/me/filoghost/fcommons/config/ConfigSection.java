/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config;

import me.filoghost.fcommons.Preconditions;
import me.filoghost.fcommons.config.exception.InvalidConfigValueException;
import me.filoghost.fcommons.config.exception.MissingConfigValueException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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

    public <T> T get(ConfigPath configPath, ConfigValueType<T> configValueType) {
        return getOrDefault(configPath, configValueType, null);
    }

    public <T> T getRequired(String path, ConfigValueType<T> configValueType)
            throws MissingConfigValueException, InvalidConfigValueException {
        return configValueType.fromConfigValueRequired(path, getRawValue(path));
    }

    public <T> T getOrDefault(String path, ConfigValueType<T> configValueType, T defaultValue) {
        return getOrDefault(ConfigPath.tokenDelimited(path), configValueType, defaultValue);
    }

    public <T> T getOrDefault(ConfigPath configPath, ConfigValueType<T> configValueType, T defaultValue) {
        return configValueType.fromConfigValueOrDefault(getRawValue(configPath), defaultValue);
    }

    public <T> void set(String path, ConfigValue configValue) {
        setRawValue(path, configValue.getRawConfigValue());
    }

    public <T> void set(String path, ConfigValueType<T> configValueType, T value) {
        set(ConfigPath.tokenDelimited(path), configValueType, value);
    }

    public <T> void set(ConfigPath configPath, ConfigValueType<T> configValueType, T value) {
        setRawValue(configPath, configValueType.toConfigValueOrNull(value));
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
            section = new ConfigSection();
            setConfigSection(path, section);
        }
        return section;
    }
    
    public Map<ConfigPath, ConfigValue> toMap() {
        Map<ConfigPath, ConfigValue> map = new LinkedHashMap<>();

        for (Entry<String, Object> entry : values.entrySet()) {
            ConfigPath path = ConfigPath.literal(entry.getKey());
            ConfigValue value = ConfigValue.ofRawConfigValue(entry.getKey(), entry.getValue());
            map.put(path, value);
        }

        return map;
    }

    private Object getRawValue(String path) {
        return getRawValue(ConfigPath.tokenDelimited(path));
    }
    
    private Object getRawValue(ConfigPath configPath) {
        Preconditions.notNull(configPath, "configPath");
        
        if (configPath.getPartsLength() == 1) {
            return values.get(configPath.getLastPart());
        }

        ConfigSection targetSection = getSectionForPath(configPath);
        if (targetSection == null) {
            return null;
        }
        
        return targetSection.values.get(configPath.getLastPart());
    }

    private void setRawValue(String path, Object value) {
        setRawValue(ConfigPath.tokenDelimited(path), value);
    }
    
    private void setRawValue(ConfigPath configPath, Object value) {
        Preconditions.notNull(configPath, "configPath");
        
        if (value != null) {
            getOrCreateSectionForPath(configPath).values.put(configPath.getLastPart(), value);
        } else {
            ConfigSection section = getSectionForPath(configPath);
            if (section != null) {
                section.values.remove(configPath.getLastPart());
            }
        }
    }
    
    @Nullable
    private ConfigSection getSectionForPath(ConfigPath configPath) {
        return getSectionForPath(configPath, false);
    }

    @NotNull
    private ConfigSection getOrCreateSectionForPath(ConfigPath configPath) {
        return getSectionForPath(configPath, true);
    }

    private ConfigSection getSectionForPath(ConfigPath configPath, boolean createIfNotExisting) {
        ConfigSection currentSection = this;
        
        for (int i = 0; i < configPath.getPartsLength() - 1; i++) { // Exclude the last part of the path
            String pathPart = configPath.getPart(i);
            Object rawValue = currentSection.values.get(pathPart);
            
            if (createIfNotExisting && rawValue == null) {
                ConfigSection innerSection = new ConfigSection();
                currentSection.values.put(pathPart, ConfigValueType.SECTION.toConfigValue(innerSection));
                currentSection = innerSection;
            } else {
                currentSection = ConfigValueType.SECTION.fromConfigValueOrNull(rawValue);
                if (currentSection == null) {
                    return null;
                }
            }
        }
        
        return currentSection;
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
        return values.toString();
    }
}
