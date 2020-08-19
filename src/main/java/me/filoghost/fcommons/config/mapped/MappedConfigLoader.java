/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.mapped;

import me.filoghost.fcommons.config.Config;
import me.filoghost.fcommons.config.ConfigErrors;
import me.filoghost.fcommons.config.ConfigLoader;
import me.filoghost.fcommons.config.ConfigValue;
import me.filoghost.fcommons.config.exception.ConfigLoadException;
import me.filoghost.fcommons.config.exception.ConfigSaveException;

import java.nio.file.Path;
import java.util.Map;
import java.util.function.Supplier;

public class MappedConfigLoader<T extends MappedConfig> {

	private final ConfigLoader configLoader;
	private final Supplier<T> mappedObjectConstructor;
	private Map<String, ConfigValue> defaultValues;

	public MappedConfigLoader(Path rootDataFolder, Path configPath, Supplier<T> mappedObjectConstructor) {
		this.configLoader = new ConfigLoader(rootDataFolder, configPath);
		this.mappedObjectConstructor = mappedObjectConstructor;
	}

	public T init() throws ConfigLoadException, ConfigSaveException {
		Config config = configLoader.init();
		T mappedObject = mappedObjectConstructor.get();

		ConfigMapper mapper;
		try {
			mapper = new ConfigMapper(mappedObject, config);
		} catch (ReflectiveOperationException e) {
			throw new ConfigLoadException(ConfigErrors.mapperInitError(mappedObject), e);
		}

		// Extract default values from fields
		if (defaultValues == null) {
			try {
				defaultValues = mapper.toConfigValues(mapper.getFieldValues());
			} catch (ReflectiveOperationException e) {
				throw new ConfigLoadException(ConfigErrors.fieldReadError(mappedObject), e);
			}
		}

		// Add missing values and save if necessary
		boolean modified = mapper.addMissingConfigValues(defaultValues);
		if (modified) {
			config.setHeader(mappedObject.getHeader());
			configLoader.save(config);
		}

		// Update the mapped object with the contents from the config
		try {
			mapper.injectObjectFields();
		} catch (ReflectiveOperationException e) {
			throw new ConfigLoadException(ConfigErrors.fieldInjectError(mappedObject), e);
		}
		mappedObject.postLoad();
		return mappedObject;
	}

	public Path getFile() {
		return configLoader.getFile();
	}

}
