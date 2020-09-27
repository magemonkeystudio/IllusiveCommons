package me.filoghost.fcommons.config.mapped.converter;

import me.filoghost.fcommons.config.ConfigSection;
import me.filoghost.fcommons.config.ConfigValueType;
import me.filoghost.fcommons.config.exception.ConfigMappingException;
import me.filoghost.fcommons.config.exception.ConfigPostLoadException;
import me.filoghost.fcommons.config.mapped.ConfigMapper;
import me.filoghost.fcommons.config.mapped.MappedConfigSection;
import me.filoghost.fcommons.reflection.TypeInfo;

public class MappedConfigSectionConverter implements Converter<MappedConfigSection, ConfigSection> {

	@Override
	public ConfigValueType<ConfigSection> getRequiredConfigValueType() {
		return ConfigValueType.SECTION;
	}

	@Override
	public ConfigSection toConfigValue0(TypeInfo<MappedConfigSection> fieldTypeInfo, MappedConfigSection mappedObject) throws ConfigMappingException {
		ConfigMapper<MappedConfigSection> configMapper = new ConfigMapper<>(fieldTypeInfo.getTypeClass());

		ConfigSection configSection = new ConfigSection();
		configMapper.setConfigFromFields(mappedObject, configSection);
		return configSection;
	}

	@Override
	public MappedConfigSection toFieldValue0(TypeInfo<MappedConfigSection> fieldTypeInfo, ConfigSection configSection, Object context) throws ConfigMappingException, ConfigPostLoadException {
		ConfigMapper<MappedConfigSection> configMapper = new ConfigMapper<>(fieldTypeInfo.getTypeClass());

		MappedConfigSection mappedObject = configMapper.newMappedObjectInstance();
		configMapper.setFieldsFromConfig(mappedObject, configSection, context);
		return mappedObject;
	}

	@Override
	public boolean equalsConfig0(TypeInfo<MappedConfigSection> fieldTypeInfo, MappedConfigSection fieldValue, ConfigSection configSection) throws ConfigMappingException {
		if (fieldValue == null && configSection == null) {
			return true;
		} else if (fieldValue == null || configSection == null) {
			return false;
		}

		ConfigMapper<MappedConfigSection> configMapper = new ConfigMapper<>(fieldTypeInfo.getTypeClass());
		return configMapper.equalsConfig(fieldValue, configSection);
	}

	@Override
	public boolean matches(Class<?> clazz) {
		return MappedConfigSection.class.isAssignableFrom(clazz);
	}

}
