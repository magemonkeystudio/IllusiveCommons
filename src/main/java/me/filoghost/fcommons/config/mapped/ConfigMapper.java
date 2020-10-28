/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.mapped;

import com.google.common.collect.ImmutableList;
import me.filoghost.fcommons.config.ConfigErrors;
import me.filoghost.fcommons.config.ConfigSection;
import me.filoghost.fcommons.config.ConfigValue;
import me.filoghost.fcommons.config.exception.ConfigMappingException;
import me.filoghost.fcommons.config.exception.ConfigPostLoadException;
import me.filoghost.fcommons.reflection.ReflectionUtils;
import me.filoghost.fcommons.reflection.TypeInfo;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ConfigMapper<T> {

    private final TypeInfo<T> mappedTypeInfo;
    private final List<MappedField<?>> mappedFields;

    public ConfigMapper(TypeInfo<T> typeInfo) throws ConfigMappingException {
        try {
            this.mappedTypeInfo = typeInfo;
            ImmutableList.Builder<MappedField<?>> mappedFieldsBuilder = ImmutableList.builder();
            for (Field field : ReflectionUtils.getDeclaredFields(typeInfo.getTypeClass())) {
                if (isMappable(field)) {
                    mappedFieldsBuilder.add(MappedField.of(field));
                }
            }
            this.mappedFields = mappedFieldsBuilder.build();
        } catch (ReflectiveOperationException e) {
            throw new ConfigMappingException(ConfigErrors.mapperReflectionException(typeInfo.getTypeClass()), e);
        }
    }

    public T newMappedObjectInstance() throws ConfigMappingException {
        return MappingUtils.createInstance(mappedTypeInfo);
    }

    public Map<String, ConfigValue> getFieldsAsConfigValues(T mappedObject) throws ConfigMappingException {
        Map<String, ConfigValue> configValues = new LinkedHashMap<>();

        for (MappedField<?> mappedField : mappedFields) {
            ConfigValue configValue = mappedField.readConfigValueFromObject(mappedObject);
            configValues.put(mappedField.getConfigPath(), configValue);
        }

        return configValues;
    }

    public void setConfigFromFields(T mappedObject, ConfigSection config) throws ConfigMappingException {
        for (MappedField<?> mappedField : mappedFields) {
            ConfigValue configValue = mappedField.readConfigValueFromObject(mappedObject);
            config.set(mappedField.getConfigPath(), configValue);
        }
    }

    @SuppressWarnings("unchecked")
    public void setFieldsFromConfig(T mappedObject, ConfigSection config, Object context)
            throws ConfigMappingException, ConfigPostLoadException {
        for (MappedField<?> mappedField : mappedFields) {
            mappedField.setFieldValueFromConfig(mappedObject, config, context);
        }

        // After injecting fields, make sure the mapped config object is alerted
        if (mappedObject instanceof PostLoadCallback) {
            ((PostLoadCallback) mappedObject).postLoad();
        }
        if (mappedObject instanceof ContextualPostLoadCallback) {
            ((ContextualPostLoadCallback<Object>) mappedObject).postLoad(context);
        }
    }

    private boolean isMappable(Field field) {
        int modifiers = field.getModifiers();
        boolean includeStatic = field.isAnnotationPresent(IncludeStatic.class)
                || field.getDeclaringClass().isAnnotationPresent(IncludeStatic.class);

        return (!Modifier.isStatic(modifiers) || includeStatic)
                || !Modifier.isTransient(modifiers)
                || !Modifier.isFinal(modifiers);
    }

    public boolean equalsConfig(T mappedObject, ConfigSection config) throws ConfigMappingException {
        for (MappedField<?> mappedField : mappedFields) {
            if (!mappedField.equalsConfigValue(mappedObject, config)) {
                return false;
            }
        }

        return true;
    }

}
