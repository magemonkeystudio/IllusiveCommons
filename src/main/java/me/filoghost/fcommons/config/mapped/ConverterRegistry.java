package me.filoghost.fcommons.config.mapped;

import com.google.common.collect.Lists;
import me.filoghost.fcommons.config.ConfigSection;
import me.filoghost.fcommons.config.ConfigValueType;
import me.filoghost.fcommons.config.exception.ConfigMappingException;
import me.filoghost.fcommons.config.mapped.converter.ConfigValueTypeConverter;
import me.filoghost.fcommons.config.mapped.converter.Converter;
import me.filoghost.fcommons.config.mapped.converter.ListConverter;
import me.filoghost.fcommons.config.mapped.converter.MappedConfigSectionConverter;
import me.filoghost.fcommons.reflection.TypeInfo;

import java.util.List;

public class ConverterRegistry {

	private static final List<Converter<?, ?>> converters = Lists.newArrayList(
			new ConfigValueTypeConverter<>(ConfigValueType.DOUBLE, Double.class, double.class),
			new ConfigValueTypeConverter<>(ConfigValueType.FLOAT, Float.class, float.class),
			new ConfigValueTypeConverter<>(ConfigValueType.LONG, Long.class, long.class),
			new ConfigValueTypeConverter<>(ConfigValueType.INTEGER, Integer.class, int.class),
			new ConfigValueTypeConverter<>(ConfigValueType.SHORT, Short.class, short.class),
			new ConfigValueTypeConverter<>(ConfigValueType.BYTE, Byte.class, byte.class),
			new ConfigValueTypeConverter<>(ConfigValueType.BOOLEAN, Boolean.class, boolean.class),

			new ConfigValueTypeConverter<>(ConfigValueType.STRING, String.class),
			new ConfigValueTypeConverter<>(ConfigValueType.SECTION, ConfigSection.class),

			new ListConverter<>(),
			new MappedConfigSectionConverter()
	);

	@SuppressWarnings("unchecked")
	public static <T> Converter<T, ?> find(TypeInfo<T> typeInfo) throws ConfigMappingException {
		return (Converter<T, ?>) converters.stream()
				.filter(converter -> converter.matches(typeInfo.getTypeClass()))
				.findFirst()
				.orElseThrow(() -> new ConfigMappingException("cannot find suitable converter for class \"" + typeInfo + "\""));
	}

}
