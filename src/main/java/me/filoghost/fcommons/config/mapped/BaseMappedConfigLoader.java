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
import me.filoghost.fcommons.reflection.TypeInfo;

public class BaseMappedConfigLoader<T extends MappedConfig> {

    private final TypeInfo<T> mappedTypeInfo;
    private ConfigMapper<T> configMapper;

    public BaseMappedConfigLoader(Class<T> mappedClass) {
        this.mappedTypeInfo = TypeInfo.of(mappedClass);
    }

    protected ConfigMapper<T> getMapper() throws ConfigMappingException {
        if (configMapper == null) {
            configMapper = new ConfigMapper<>(mappedTypeInfo);
        }
        return configMapper;
    }

    public T loadFromConfig(ConfigSection configSection) throws ConfigLoadException {
        return loadFromConfig(configSection, null);
    }

    public T loadFromConfig(ConfigSection configSection, Object context) throws ConfigLoadException {
        try {
            T mappedObject = getMapper().newMappedObjectInstance();
            getMapper().setFieldsFromConfig(mappedObject, configSection, context);
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
