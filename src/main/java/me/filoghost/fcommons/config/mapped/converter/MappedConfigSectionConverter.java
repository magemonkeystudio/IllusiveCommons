/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.mapped.converter;

import me.filoghost.fcommons.config.ConfigSection;
import me.filoghost.fcommons.config.ConfigValueType;
import me.filoghost.fcommons.config.exception.ConfigMappingException;
import me.filoghost.fcommons.config.exception.ConfigPostLoadException;
import me.filoghost.fcommons.config.mapped.ConfigMapper;
import me.filoghost.fcommons.config.mapped.MappedConfigSection;
import me.filoghost.fcommons.reflection.TypeInfo;

public class MappedConfigSectionConverter extends Converter<MappedConfigSection, ConfigSection> {

    private final ConfigMapper<MappedConfigSection> configMapper;

    public MappedConfigSectionConverter(TypeInfo<MappedConfigSection> fieldTypeInfo) throws ConfigMappingException {
        super(ConfigValueType.SECTION);
        this.configMapper = new ConfigMapper<>(fieldTypeInfo);
    }

    @Override
    protected ConfigSection toConfigValue0(MappedConfigSection mappedObject) throws ConfigMappingException {
        ConfigSection configSection = new ConfigSection();
        configMapper.setConfigFromFields(mappedObject, configSection);
        return configSection;
    }

    @Override
    protected MappedConfigSection toFieldValue0(ConfigSection configSection, Object context)
            throws ConfigMappingException, ConfigPostLoadException {
        MappedConfigSection mappedObject = configMapper.newMappedObjectInstance();
        configMapper.setFieldsFromConfig(mappedObject, configSection, context);
        return mappedObject;
    }

    @Override
    protected boolean equalsConfig0(MappedConfigSection fieldValue, ConfigSection configSection) throws ConfigMappingException {
        if (fieldValue == null && configSection == null) {
            return true;
        } else if (fieldValue == null || configSection == null) {
            return false;
        }

        return configMapper.equalsConfig(fieldValue, configSection);
    }

    public static boolean supports(Class<?> clazz) {
        return MappedConfigSection.class.isAssignableFrom(clazz);
    }

}
