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
import java.util.Map.Entry;

public class MappedConfigLoader<T extends MappedConfig> extends BaseMappedConfigLoader<T> {

    private final ConfigLoader configLoader;

    private Map<String, ConfigValue> defaultValues;

    public MappedConfigLoader(Path rootDataFolder, Path configPath, Class<T> mappedConfigClass) {
        super(mappedConfigClass);
        this.configLoader = new ConfigLoader(rootDataFolder, configPath);
    }

    public T load() throws ConfigLoadException {
        return load(null);
    }

    public T load(Object context) throws ConfigLoadException {
        Config config = configLoader.load();
        return super.loadFromConfig(config, context);
    }

    public T init() throws ConfigLoadException, ConfigSaveException {
        return init(null);
    }

    public T init(Object context) throws ConfigLoadException, ConfigSaveException {
        Config config = configLoader.init();

        try {
            T mappedObject = getMapper().newMappedObjectInstance();

            if (defaultValues == null) {
                defaultValues = getMapper().getFieldsAsConfigValues(mappedObject);
            }

            boolean modified = addMissingDefaultValues(config, defaultValues);
            if (modified) {
                config.setHeader(mappedObject.getHeader());
                configLoader.save(config);
            }

            getMapper().setFieldsFromConfig(mappedObject, config, context);
            return mappedObject;

        } catch (ConfigMappingException e) {
            throw new ConfigLoadException(e.getMessage(), e.getCause());
        }
    }

    private boolean addMissingDefaultValues(ConfigSection config, Map<String, ConfigValue> defaultValues) {
        boolean modified = false;

        for (Entry<String, ConfigValue> entry : defaultValues.entrySet()) {
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
        if (!configLoader.fileExists()) {
            saveWithHeader(new Config(), newMappedObject);
            return true;
        }

        Config config = configLoader.load();

        try {
            if (!getMapper().equalsConfig(newMappedObject, config)) {
                saveWithHeader(config, newMappedObject);
                return true;
            }
        } catch (ConfigMappingException e) {
            throw new ConfigLoadException(e.getMessage(), e.getCause());
        }

        return false;
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
