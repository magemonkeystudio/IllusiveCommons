package me.filoghost.fcommons.config.mapped.converter;

import me.filoghost.fcommons.config.ConfigSection;
import me.filoghost.fcommons.config.ConfigValue;
import me.filoghost.fcommons.config.ConfigValueType;
import me.filoghost.fcommons.config.exception.ConfigMappingException;
import me.filoghost.fcommons.config.exception.ConfigPostLoadException;
import me.filoghost.fcommons.config.mapped.ConfigMapper;
import me.filoghost.fcommons.config.mapped.MappedConfigSection;
import me.filoghost.fcommons.reflection.TypeInfo;

public class MappedConfigSectionConverter implements Converter<MappedConfigSection> {

	@Override
	public ConfigValue toConfigValue(TypeInfo<MappedConfigSection> fieldTypeInfo, MappedConfigSection fieldValue) throws ConfigMappingException {
		ConfigMapper<MappedConfigSection> configMapper = new ConfigMapper<>(fieldTypeInfo.getTypeClass());

		ConfigSection configSection = new ConfigSection();
		configMapper.setConfigFromFields(fieldValue, configSection);
		return ConfigValue.of(ConfigValueType.SECTION, configSection);
	}

	@Override
	public MappedConfigSection toFieldValue(TypeInfo<MappedConfigSection> fieldTypeInfo, ConfigValue configValue, Object context) throws ConfigMappingException, ConfigPostLoadException {
		if (!configValue.isPresentAs(ConfigValueType.SECTION)) {
			return null;
		}

		ConfigMapper<MappedConfigSection> configMapper = new ConfigMapper<>(fieldTypeInfo.getTypeClass());

		ConfigSection configSection = configValue.as(ConfigValueType.SECTION);
		MappedConfigSection mappedObject = configMapper.newMappedObjectInstance();
		configMapper.setFieldsFromConfig(mappedObject, configSection, context);
		return mappedObject;
	}

	@Override
	public boolean equals(TypeInfo<MappedConfigSection> fieldTypeInfo, MappedConfigSection o1, MappedConfigSection o2) throws ConfigMappingException {
		if (o1 == o2) {
			return true;
		} else if (o1 == null || o2 == null) {
			return false;
		}

		ConfigMapper<MappedConfigSection> configMapper = new ConfigMapper<>(fieldTypeInfo.getTypeClass());
		return configMapper.equals(o1, o2);
	}

	@Override
	public boolean matches(Class<?> clazz) {
		return MappedConfigSection.class.isAssignableFrom(clazz);
	}

}
