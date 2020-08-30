/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.mapped;

import me.filoghost.fcommons.config.Config;
import me.filoghost.fcommons.config.ConfigLoader;
import me.filoghost.fcommons.config.exception.ConfigLoadException;
import me.filoghost.fcommons.config.exception.ConfigSaveException;

import java.nio.file.Path;
import java.util.List;

public class MappedConfigLoader<T extends MappedConfig> {

	private final ConfigLoader configLoader;
	private final ConfigMapper<T> configMapper;
	private List<PathAndConfigValue> defaultValues;

	public MappedConfigLoader(Path rootDataFolder, Path configPath, Class<T> mappedConfigClass) {
		this.configLoader = new ConfigLoader(rootDataFolder, configPath);
		this.configMapper = new ConfigMapper<>(mappedConfigClass);
	}

	public T load() throws ConfigLoadException {
		Config config = configLoader.load();
		T mappedObject = configMapper.newMappedObjectInstance();

		configMapper.setFieldsFromConfig(mappedObject, config);
		mappedObject.postLoad();
		return mappedObject;
	}

	public T init() throws ConfigLoadException, ConfigSaveException {
		Config config = configLoader.init();
		T mappedObject = configMapper.newMappedObjectInstance();

		if (defaultValues == null) {
			defaultValues = configMapper.getFieldsAsConfigValues(mappedObject);
		}

		boolean modified = configMapper.addMissingValuesToConfig(config, defaultValues);
		if (modified) {
			config.setHeader(mappedObject.getHeader());
			configLoader.save(config);
		}

		configMapper.setFieldsFromConfig(mappedObject, config);
		mappedObject.postLoad();
		return mappedObject;
	}

	public Path getFile() {
		return configLoader.getFile();
	}

}
