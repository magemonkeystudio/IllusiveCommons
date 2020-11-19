/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.mapped.converter;

import me.filoghost.fcommons.config.ConfigValue;
import me.filoghost.fcommons.config.ConfigValueType;
import me.filoghost.fcommons.config.exception.ConfigMappingException;
import me.filoghost.fcommons.config.exception.ConfigPostLoadException;
import me.filoghost.fcommons.config.mapped.ConverterRegistry;
import me.filoghost.fcommons.config.mapped.MappingUtils;
import me.filoghost.fcommons.reflection.TypeInfo;

import java.util.ArrayList;
import java.util.List;

public class ListConverter<E> extends Converter<List<E>, List<ConfigValue>> {

    protected final TypeInfo<List<E>> fieldTypeInfo;
    private final Converter<E, ?> elementConverter;

    @SuppressWarnings("unchecked")
    public ListConverter(TypeInfo<List<E>> fieldTypeInfo) throws ConfigMappingException {
        super(ConfigValueType.LIST);
        this.fieldTypeInfo = fieldTypeInfo;
        TypeInfo<E> elementTypeInfo = (TypeInfo<E>) MappingUtils.getSingleGenericType(fieldTypeInfo);
        this.elementConverter = ConverterRegistry.fromObjectType(elementTypeInfo);
    }

    @Override
    protected List<ConfigValue> toConfigValue0(List<E> fieldValue) throws ConfigMappingException {
        List<ConfigValue> configList = new ArrayList<>();
        for (E fieldElement : fieldValue) {
            configList.add(elementConverter.toConfigValue(fieldElement));
        }

        return configList;
    }

    @Override
    protected List<E> toFieldValue0(List<ConfigValue> configList, Object context) throws ConfigMappingException, ConfigPostLoadException {
        List<E> fieldList = new ArrayList<>();
        for (ConfigValue configElement : configList) {
            if (isValidConfigListElement(configElement)) {
                E fieldValue = elementConverter.toFieldValue(configElement, context);
                fieldList.add(fieldValue);
            }
        }

        return fieldList;
    }

    @Override
    protected boolean equalsConfig0(List<E> fieldList, List<ConfigValue> configList) throws ConfigMappingException {
        if (fieldList == null && configList == null) {
            return true;
        } else if (fieldList == null || configList == null) {
            return false;
        }

        // Skip elements that would be skipped during read
        List<ConfigValue> filteredConfigList = new ArrayList<>();
        for (ConfigValue configValue : configList) {
            if (isValidConfigListElement(configValue)) {
                filteredConfigList.add(configValue);
            }
        }

        if (filteredConfigList.size() != fieldList.size()) {
            return false;
        }

        for (int i = 0; i < filteredConfigList.size(); i++) {
            ConfigValue configElement = filteredConfigList.get(i);
            E fieldElement = fieldList.get(i);

            if (!elementConverter.equalsConfig(fieldElement, configElement)) {
                return false;
            }
        }

        return true;
    }

    private boolean isValidConfigListElement(ConfigValue configElement) {
        return configElement.isPresentAs(elementConverter.configValueType);
    }

    public static boolean supports(Class<?> clazz) {
        return clazz == List.class;
    }

}
