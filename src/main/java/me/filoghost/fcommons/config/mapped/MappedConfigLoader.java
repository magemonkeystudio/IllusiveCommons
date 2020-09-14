/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.mapped;

import me.filoghost.fcommons.config.Config;
import me.filoghost.fcommons.config.ConfigLoader;
import me.filoghost.fcommons.config.ConfigSection;
import me.filoghost.fcommons.config.ConfigValue;
import me.filoghost.fcommons.config.exception.ConfigLoadException;
import me.filoghost.fcommons.config.exception.ConfigMappingException;
import me.filoghost.fcommons.config.exception.ConfigSaveException;

import java.nio.file.Path;
import java.util.Map;

public class MappedConfigLoader<T extends MappedConfig> extends BaseMappedConfigLoader<T> {

	private final ConfigLoader configLoader;

	private Map<String, ConfigValue> defaultValues;

	public MappedConfigLoader(Path rootDataFolder, Path configPath, Class<T> mappedConfigClass) {
		super(mappedConfigClass);
		this.configLoader = new ConfigLoader(rootDataFolder, configPath);
	}

	public T load() throws ConfigLoadException {
		Config config = configLoader.load();
		return super.loadFromConfig(config);
	}

	public T init() throws ConfigLoadException, ConfigSaveException {
		Config config = configLoader.init();

		try {
			T mappedObject = getMapper().newMappedObjectInstance();

			if (defaultValues == null) {
				defaultValues = getMapper().getFieldsAsConfigValues(mappedObject);
			}

			boolean modified = addMissingDefaultValues(config);
			if (modified) {
				config.setHeader(mappedObject.getHeader());
				configLoader.save(config);
			}

			getMapper().setFieldsFromConfig(mappedObject, config);
			return mappedObject;

		} catch (ConfigMappingException e) {
			throw new ConfigLoadException(e.getMessage(), e.getCause());
		}
	}

	private boolean addMissingDefaultValues(ConfigSection config) {
		boolean modified = false;

		for (Map.Entry<String, ConfigValue> entry : defaultValues.entrySet()) {
			if (!config.contains(entry.getKey())) {
				config.set(entry.getKey(), entry.getValue());
				modified = true;
			}
		}

		return modified;
	}

	public void save(T mappedObject) throws ConfigSaveException {
		Config config = new Config();

		saveWithHeader(config, mappedObject);
	}

	public boolean saveIfDifferent(T newMappedObject) throws ConfigLoadException, ConfigSaveException {
		Config config;
		if (configLoader.fileExists()) {
			config = configLoader.load();
		} else {
			config = new Config();
		}

		T currentMappedObject = loadFromConfig(config);

		try {
			if (getMapper().equals(newMappedObject, currentMappedObject)) {
				return false;
			}
		} catch (ConfigMappingException e) {
			throw new ConfigLoadException(e.getMessage(), e.getCause());
		}

		saveWithHeader(config, newMappedObject);
		return true;
	}

	private void saveWithHeader(Config config, T mappedObject) throws ConfigSaveException {
		super.saveToConfig(mappedObject, config);
		config.setHeader(mappedObject.getHeader());
		configLoader.save(config);
	}

	public Path getFile() {
		return configLoader.getFile();
	}

}
