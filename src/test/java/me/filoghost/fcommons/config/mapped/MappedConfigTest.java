package me.filoghost.fcommons.config.mapped;

import me.filoghost.fcommons.config.ConfigSection;
import me.filoghost.fcommons.config.exception.ConfigException;
import me.filoghost.fcommons.config.exception.ConfigLoadException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class MappedConfigTest {

	@Test
	public void testGenerateConfig(@TempDir Path tempDir) throws ConfigException, IOException {
		MappedConfigLoader<SimpleMappedConfig> configLoader = newNonExistingConfig(tempDir, SimpleMappedConfig.class);
		configLoader.init();

		assertThat(configLoader.getFile()).exists();
		assertThat(Files.readAllLines(configLoader.getFile())).containsExactlyInAnyOrder(
				"integer: 3",
				"string: abc"
		);
	}

	@Test
	public void testReadConfig(@TempDir Path tempDir) throws ConfigException, IOException {
		MappedConfigLoader<SimpleMappedConfig> configLoader = newExistingConfig(
				tempDir,
				SimpleMappedConfig.class,
				"integer: 5",
				"string: def"
		);
		SimpleMappedConfig config = configLoader.init();

		assertThat(config.integer).isEqualTo(5);
		assertThat(config.string).isEqualTo("def");
	}

	@Test
	public void testWrongTypes(@TempDir Path tempDir) throws ConfigException, IOException {
		MappedConfigLoader<SimpleMappedConfig> configLoader = newExistingConfig(
				tempDir,
				SimpleMappedConfig.class,
				"integer: string"
		);
		SimpleMappedConfig config = configLoader.init();

		assertThat(config.integer).isEqualTo(3); // Should use default value
		assertThat(config.string).isEqualTo("abc");
	}

	@Test
	public void testConfigSection(@TempDir Path tempDir) throws ConfigException, IOException {
		MappedConfigLoader<ConfigSectionConfig> configLoader = newExistingConfig(
				tempDir,
				ConfigSectionConfig.class,
				"section:",
				"  key1: value1",
				"  key2: value2",
				"  nested-section:",
				"    nested1: value3"
		);
		ConfigSectionConfig config = configLoader.init();

		assertThat(config.section.getString("key1")).isEqualTo("value1");
		assertThat(config.section.getString("key2")).isEqualTo("value2");
		assertThat(config.section.getConfigSection("nested-section"))
				.isNotNull()
				.extracting(s -> s.getString("nested1")).isEqualTo("value3");
	}

	@Test
	public void testListOfConfigSections(@TempDir Path tempDir) throws ConfigException, IOException {
		MappedConfigLoader<ListOfSectionsConfig> configLoader = newExistingConfig(
				tempDir,
				ListOfSectionsConfig.class,
				"sections:",
				"- key1: value1",
				"  key2: value2",
				"- key1: value3",
				"  key2: value4"
		);
		ListOfSectionsConfig config = configLoader.init();

		assertThat(config.sections).hasSize(2);
		assertThat(config.sections.get(0).getString("key1")).isEqualTo("value1");
		assertThat(config.sections.get(0).getString("key2")).isEqualTo("value2");
		assertThat(config.sections.get(1).getString("key1")).isEqualTo("value3");
		assertThat(config.sections.get(1).getString("key2")).isEqualTo("value4");
	}

	@Test
	public void testWriteReadConfig(@TempDir Path tempDir) throws ConfigException {
		MappedConfigLoader<SimpleMappedConfig> configLoader = newNonExistingConfig(tempDir, SimpleMappedConfig.class);
		configLoader.init();

		// Read again after initialization
		SimpleMappedConfig config = configLoader.init();

		assertThat(config.integer).isEqualTo(3);
		assertThat(config.string).isEqualTo("abc");
	}

	@Test
	public void testNestedGenerics(@TempDir Path tempDir) throws ConfigException, IOException {
		MappedConfigLoader<NestedGenericsConfig> configLoader = newNonExistingConfig(tempDir, NestedGenericsConfig.class);
		configLoader.init();

		assertFileContents(configLoader.getFile(),
				"lists:",
				"- - 1",
				"  - 2",
				"- - 3",
				"  - 4"
		);
	}

	@Test
	public void testBadGenerics(@TempDir Path tempDir) {
		MappedConfigLoader<BadGenericsConfig> configLoaderBadGenerics = newNonExistingConfig(tempDir, BadGenericsConfig.class);
		MappedConfigLoader<NoGenericsConfig> configLoaderNoGenerics = newNonExistingConfig(tempDir, NoGenericsConfig.class);

		assertThatExceptionOfType(ConfigLoadException.class).isThrownBy(() -> {
			configLoaderBadGenerics.init();
		});

		assertThatExceptionOfType(ConfigLoadException.class).isThrownBy(() -> {
			configLoaderNoGenerics.init();
		});
	}

	@Test
	public void testWriteNulls(@TempDir Path tempDir) throws ConfigException, IOException {
		MappedConfigLoader<NullConfig> configLoader = newNonExistingConfig(tempDir, NullConfig.class);
		configLoader.init();

		assertFileContents(configLoader.getFile(),
				"primitiveInteger: 0",
				"listWithNull:",
				"- a",
				"- null",
				"- c",
				"sectionWithNull:",
				"  key1: value1"
		);
	}

	@Test
	public void testReadNulls(@TempDir Path tempDir) throws ConfigException, IOException {
		Path configPath = tempDir.resolve("mapped-config.yml");
		Files.createFile(configPath);

		MappedConfigLoader<NullConfig> configLoader = new MappedConfigLoader<>(tempDir, configPath, NullConfig.class);
		NullConfig config = configLoader.init();

		assertThat(config.primitiveInteger).isEqualTo(0);
		assertThat(config.boxedInteger).isNull();
		assertThat(config.string).isNull();
		assertThat(config.list).isNull();
		assertThat(config.section).isNull();
	}

	@Test
	public void testCustomObjects(@TempDir Path tempDir) throws ConfigException, IOException {
		MappedConfigLoader<CustomObjectConfig> configLoader = newExistingConfig(
				tempDir,
				CustomObjectConfig.class,
				"customObject:",
				"  string: xyz",
				"  integer: 99",
				"customObjectList:",
				"- string: fromConfig1",
				"  integer: 123",
				"- string: fromConfig2"
		);
		CustomObjectConfig config = configLoader.init();

		assertThat(config.customObject.getString()).isEqualTo("xyz");
		assertThat(config.customObject.getInteger()).isEqualTo(99);
		assertThat(config.customObjectList).hasSize(2);
		assertThat(config.customObjectList.get(0).getString()).isEqualTo("fromConfig1");
		assertThat(config.customObjectList.get(0).getInteger()).isEqualTo(123);
		assertThat(config.customObjectList.get(1).getString()).isEqualTo("fromConfig2");
		assertThat(config.customObjectList.get(1).getInteger()).isEqualTo(3); // Should use default

		// Check that missing fields have been added, but not missing keys in sections
		assertFileContents(configLoader.getFile(),
				"customObject:",
				"  string: xyz",
				"  integer: 99",
				"customObjectList:",
				"- string: fromConfig1",
				"  integer: 123",
				"- string: fromConfig2",
				"customObjectWithDefault:",
				"  string: abc",
				"  integer: 3",
				"customObjectListWithDefault:",
				"- string: element1",
				"  integer: 1",
				"- string: element2",
				"  integer: 2"
		);
	}


	private static <T extends MappedConfig> MappedConfigLoader<T> newNonExistingConfig(Path tempDir, Class<T> mappedConfigClass) {
		Path configPath = tempDir.resolve("temp-config.yml");
		return new MappedConfigLoader<>(tempDir, configPath, mappedConfigClass);
	}


	private static <T extends MappedConfig> MappedConfigLoader<T> newExistingConfig(Path tempDir, Class<T> mappedConfigClass, String... contents) throws IOException {
		Path configPath = tempDir.resolve("temp-config.yml");
		if (contents != null && contents.length > 0) {
			Files.write(configPath, Arrays.asList(contents));
		} else {
			Files.createFile(configPath);
		}

		return new MappedConfigLoader<>(tempDir, configPath, mappedConfigClass);
	}


	private void assertFileContents(Path file, String... contents) throws IOException {
		assertThat(file).exists();
		assertThat(Files.readAllLines(file)).containsExactly(contents);
	}


	private static class SimpleMappedConfig implements MappedConfig {

		private int integer = 3;
		private String string = "abc";

	}

	private static class ConfigSectionConfig implements MappedConfig {

		private ConfigSection section;

	}

	private static class ListOfSectionsConfig implements MappedConfig {

		private List<ConfigSection> sections;

	}

	private static class NestedGenericsConfig implements MappedConfig {

		private List<List<Integer>> lists = Arrays.asList(
				Arrays.asList(1, 2),
				Arrays.asList(3, 4)
		);

	}

	private static class BadGenericsConfig implements MappedConfig {

		private List<?> list = Arrays.asList(1, 2, 3);

	}

	private static class NoGenericsConfig implements MappedConfig {

		private List list = Arrays.asList(1, 2, 3);

	}

	private static class NullConfig implements MappedConfig {

		private int primitiveInteger;
		private Integer boxedInteger;
		private String string;
		private List<Integer> list;
		private ConfigSection section;
		private List<String> listWithNull = Arrays.asList("a", null, "c");
		private ConfigSection sectionWithNull;

		NullConfig() {
			sectionWithNull = new ConfigSection();
			sectionWithNull.setString("key1", "value1");
			sectionWithNull.setString("key2", null);
		}

	}

	private static class CustomObjectConfig implements MappedConfig {

		private CustomObject customObject;
		private CustomObject customObjectWithDefault = new CustomObject();

		private List<CustomObject> customObjectList;
		private List<CustomObject> customObjectListWithDefault = Arrays.asList(
				new CustomObject("element1", 1),
				new CustomObject("element2", 2)
		);

		private static class CustomObject implements MappedConfigSection {

			private String string = "abc";
			private int integer = 3;

			public CustomObject() {}

			public CustomObject(String string, int integer) {
				this.string = string;
				this.integer = integer;
			}

			public String getString() {
				return string;
			}

			public int getInteger() {
				return integer;
			}

		}

	}

}