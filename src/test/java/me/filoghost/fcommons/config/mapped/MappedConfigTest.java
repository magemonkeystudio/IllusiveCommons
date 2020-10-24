package me.filoghost.fcommons.config.mapped;

import me.filoghost.fcommons.test.AssertExtra;
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
	void testGenerateConfig(@TempDir Path tempDir) throws ConfigException, IOException {
		MappedConfigLoader<SimpleMappedConfig> configLoader = MappedTestCommons.newNonExistingConfig(tempDir, SimpleMappedConfig.class);
		configLoader.init();

		assertThat(configLoader.getFile()).exists();
		assertThat(Files.readAllLines(configLoader.getFile())).containsExactlyInAnyOrder(
				"integer: 3",
				"string: abc"
		);
	}

	@Test
	void testReadConfig(@TempDir Path tempDir) throws ConfigException, IOException {
		MappedConfigLoader<SimpleMappedConfig> configLoader = MappedTestCommons.newExistingConfig(tempDir, SimpleMappedConfig.class,
				"integer: 5",
				"string: def"
		);
		SimpleMappedConfig config = configLoader.init();

		assertThat(config.integer).isEqualTo(5);
		assertThat(config.string).isEqualTo("def");
	}

	@Test
	void testWrongTypes(@TempDir Path tempDir) throws ConfigException, IOException {
		MappedConfigLoader<SimpleMappedConfig> configLoader = MappedTestCommons.newExistingConfig(tempDir, SimpleMappedConfig.class,
				"integer: string"
		);
		SimpleMappedConfig config = configLoader.init();

		assertThat(config.integer).isEqualTo(3); // Should use default value
		assertThat(config.string).isEqualTo("abc");
	}

	@Test
	void testHeaderExisting(@TempDir Path tempDir) throws ConfigException, IOException {
		MappedConfigLoader<HeaderConfig> configLoader = MappedTestCommons.newExistingConfig(tempDir, HeaderConfig.class,
				"integer: 5"
		);
		configLoader.init();

		AssertExtra.fileContentMatches(configLoader.getFile(),
				"integer: 5"
		);
	}

	@Test
	void testHeaderMissingValue(@TempDir Path tempDir) throws ConfigException, IOException {
		MappedConfigLoader<HeaderConfig> configLoader = MappedTestCommons.newExistingConfig(tempDir, HeaderConfig.class,
				""
		);
		configLoader.init();

		AssertExtra.fileContentMatches(configLoader.getFile(),
				"# Header line",
				"",
				"integer: 1"
		);
	}

	@Test
	void testHeaderNewFile(@TempDir Path tempDir) throws ConfigException, IOException {
		MappedConfigLoader<HeaderConfig> configLoader = MappedTestCommons.newNonExistingConfig(tempDir, HeaderConfig.class);
		configLoader.init();

		AssertExtra.fileContentMatches(configLoader.getFile(),
				"# Header line",
				"",
				"integer: 1"
		);
	}

	@Test
	void testConfigSection(@TempDir Path tempDir) throws ConfigException, IOException {
		MappedConfigLoader<ConfigSectionConfig> configLoader = MappedTestCommons.newExistingConfig(tempDir, ConfigSectionConfig.class,
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
	void testListOfConfigSections(@TempDir Path tempDir) throws ConfigException, IOException {
		MappedConfigLoader<ListOfSectionsConfig> configLoader = MappedTestCommons.newExistingConfig(tempDir, ListOfSectionsConfig.class,
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
	void testWriteReadConfig(@TempDir Path tempDir) throws ConfigException {
		MappedConfigLoader<SimpleMappedConfig> configLoader = MappedTestCommons.newNonExistingConfig(tempDir, SimpleMappedConfig.class);
		configLoader.init();

		// Read again after initialization
		SimpleMappedConfig config = configLoader.init();

		assertThat(config.integer).isEqualTo(3);
		assertThat(config.string).isEqualTo("abc");
	}

	@Test
	void testNestedGenerics(@TempDir Path tempDir) throws ConfigException, IOException {
		MappedConfigLoader<NestedGenericsConfig> configLoader = MappedTestCommons.newNonExistingConfig(tempDir, NestedGenericsConfig.class);
		configLoader.init();

		AssertExtra.fileContentMatches(configLoader.getFile(),
				"lists:",
				"- - 1",
				"  - 2",
				"- - 3",
				"  - 4"
		);
	}

	@Test
	void testBadGenerics(@TempDir Path tempDir) {
		MappedConfigLoader<BadGenericsConfig> configLoaderBadGenerics = MappedTestCommons.newNonExistingConfig(tempDir, BadGenericsConfig.class);
		MappedConfigLoader<NoGenericsConfig> configLoaderNoGenerics = MappedTestCommons.newNonExistingConfig(tempDir, NoGenericsConfig.class);

		assertThatExceptionOfType(ConfigLoadException.class).isThrownBy(() -> {
			configLoaderBadGenerics.init();
		});

		assertThatExceptionOfType(ConfigLoadException.class).isThrownBy(() -> {
			configLoaderNoGenerics.init();
		});
	}

	@Test
	void testWriteNulls(@TempDir Path tempDir) throws ConfigException, IOException {
		MappedConfigLoader<NullConfig> configLoader = MappedTestCommons.newNonExistingConfig(tempDir, NullConfig.class);
		configLoader.init();

		AssertExtra.fileContentMatches(configLoader.getFile(),
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
	void testReadNulls(@TempDir Path tempDir) throws ConfigException, IOException {
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
	void testCustomObjects(@TempDir Path tempDir) throws ConfigException, IOException {
		MappedConfigLoader<CustomObjectConfig> configLoader = MappedTestCommons.newExistingConfig(tempDir, CustomObjectConfig.class,
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
		AssertExtra.fileContentMatches(configLoader.getFile(),
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


	private static class SimpleMappedConfig implements MappedConfig {

		private int integer = 3;
		private String string = "abc";

	}

	private static class HeaderConfig implements MappedConfig {

		private int integer = 1;

		@Override
		public List<String> getHeader() {
			return Arrays.asList("Header line");
		}

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