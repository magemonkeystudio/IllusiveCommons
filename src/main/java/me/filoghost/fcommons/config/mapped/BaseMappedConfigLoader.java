/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.mapped;

import me.filoghost.fcommons.config.ConfigSection;
import me.filoghost.fcommons.config.exception.ConfigLoadException;
import me.filoghost.fcommons.config.exception.ConfigMappingException;
import me.filoghost.fcommons.config.exception.ConfigSaveException;

public class BaseMappedConfigLoader<T extends MappedConfig> {

	private final Class<T> mappedConfigClass;
	private ConfigMapper<T> configMapper;

	public BaseMappedConfigLoader(Class<T> mappedConfigClass) {
		this.mappedConfigClass = mappedConfigClass;
	}

	protected ConfigMapper<T> getMapper() throws ConfigMappingException {
		if (configMapper == null) {
			configMapper = new ConfigMapper<>(mappedConfigClass);
		}
		return configMapper;
	}

	public T loadFromConfig(ConfigSection configSection) throws ConfigLoadException {
		try {
			T mappedObject = getMapper().newMappedObjectInstance();
			getMapper().setFieldsFromConfig(mappedObject, configSection);
			return mappedObject;
		} catch (ConfigMappingException e) {
			throw new ConfigLoadException(e.getMessage(), e.getCause());
		}
	}

	public void saveToConfig(T mappedObject, ConfigSection configSection) throws ConfigSaveException {
		try {
			getMapper().setConfigFromFields(mappedObject, configSection);
		} catch (ConfigMappingException e) {
			throw new ConfigSaveException(e.getMessage(), e.getCause());
		}
	}

}
