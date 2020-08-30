package me.filoghost.fcommons.config.mapped.converter;

import me.filoghost.fcommons.config.ConfigSection;
import me.filoghost.fcommons.config.ConfigValue;
import me.filoghost.fcommons.config.ConfigValueType;
import me.filoghost.fcommons.config.exception.ConfigLoadException;
import me.filoghost.fcommons.config.mapped.ConfigMapper;
import me.filoghost.fcommons.config.mapped.MappedConfigSection;
import me.filoghost.fcommons.reflection.TypeInfo;

public class MappedConfigSectionConverter implements Converter<MappedConfigSection> {

	@Override
	public ConfigValue toConfigValue(TypeInfo fieldTypeInfo, MappedConfigSection fieldValue) throws ConfigLoadException {
		ConfigMapper<MappedConfigSection> configMapper = new ConfigMapper<>(fieldTypeInfo.getTypeClassAs(MappedConfigSection.class));

		ConfigSection configSection = new ConfigSection();
		configMapper.setConfigFromFields(fieldValue, configSection);
		return ConfigValue.of(ConfigValueType.SECTION, configSection);
	}

	@Override
	public MappedConfigSection toFieldValue(TypeInfo fieldTypeInfo, ConfigValue configValue) throws ConfigLoadException {
		if (!configValue.isPresentAs(ConfigValueType.SECTION)) {
			return null;
		}

		ConfigMapper<MappedConfigSection> configMapper = new ConfigMapper<>(fieldTypeInfo.getTypeClassAs(MappedConfigSection.class));

		ConfigSection configSection = configValue.as(ConfigValueType.SECTION);
		MappedConfigSection mappedObject = configMapper.newMappedObjectInstance();
		configMapper.setFieldsFromConfig(mappedObject, configSection);
		mappedObject.postLoad();

		return mappedObject;
	}

	@Override
	public boolean matches(Class<?> clazz) {
		return MappedConfigSection.class.isAssignableFrom(clazz);
	}

}
