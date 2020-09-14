package me.filoghost.fcommons.config.mapped;

import me.filoghost.fcommons.Colors;
import me.filoghost.fcommons.config.Config;
import me.filoghost.fcommons.config.ConfigSection;
import me.filoghost.fcommons.config.exception.ConfigException;
import me.filoghost.fcommons.config.mapped.modifier.ChatColors;
import me.filoghost.fcommons.test.AssertExtra;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class MappedConfigLoaderTest {


	@Test
	public void testInitNew(@TempDir Path tempDir) throws ConfigException, IOException {
		MappedConfigLoader<TestConfig> configLoader = MappedTestCommons.newNonExistingConfig(tempDir, TestConfig.class);
		configLoader.init();

		AssertExtra.fileContentMatches(configLoader.getFile(),
				"normalPresent: 10",
				"primitivePresent: 10",
				"primitiveMissing: 0",
				"listPresent:",
				"- 1",
				"- 2",
				"- 3",
				"sectionPresent: {}",
				"objectPresent:",
				"  normalPresent: 10"
		);
	}

	@Test
	public void testInitExisting(@TempDir Path tempDir) throws ConfigException, IOException {
		MappedConfigLoader<TestConfig> configLoader = MappedTestCommons.newExistingConfig(tempDir, TestConfig.class,
				"normalMissing: 5",
				"listPresent: not a list"
		);
		configLoader.init();

		AssertExtra.fileContentMatches(configLoader.getFile(),
				"normalMissing: 5",
				"listPresent: not a list",
				"normalPresent: 10",
				"primitivePresent: 10",
				"primitiveMissing: 0",
				"sectionPresent: {}",
				"objectPresent:",
				"  normalPresent: 10"
		);
	}

	@Test
	public void testLoad(@TempDir Path tempDir) throws ConfigException, IOException {
		MappedConfigLoader<TestConfig> configLoader = MappedTestCommons.newExistingConfig(tempDir, TestConfig.class,
				"normalMissing: 5"
		);
		TestConfig config = configLoader.load();

		assertThat(config.normalMissing).isEqualTo(5);
		AssertExtra.fileContentMatches(configLoader.getFile(),
				"normalMissing: 5"
		);
	}

	@Test
	public void testLoadFromConfig() throws ConfigException {
		BaseMappedConfigLoader<TestConfig> configLoader = new BaseMappedConfigLoader<>(TestConfig.class);
		Config config = new Config();
		config.setInt("normalMissing", 5);
		TestConfig mappedConfig = configLoader.loadFromConfig(config);

		assertThat(mappedConfig.normalMissing).isEqualTo(5);
	}

	@Test
	public void testSave(@TempDir Path tempDir) throws ConfigException, IOException {
		MappedConfigLoader<TestConfig> configLoader = MappedTestCommons.newExistingConfig(tempDir, TestConfig.class,
				"normalPresent: 5",
				"listPresent: not a list"
		);

		TestConfig config = new TestConfig();
		config.objectMissing = new TestConfigSection();
		config.objectMissing.normalMissing = 99;

		boolean changed = configLoader.saveIfDifferent(config);

		assertThat(changed).isTrue();
		AssertExtra.fileContentMatches(configLoader.getFile(),
				"normalPresent: 10",
				"listPresent:",
				"- 1",
				"- 2",
				"- 3",
				"primitivePresent: 10", // New values are added after
				"primitiveMissing: 0",
				"sectionPresent: {}",
				"objectPresent:",
				"  normalPresent: 10",
				"objectMissing:",
				"  normalPresent: 10",
				"  normalMissing: 99"
		);
	}

	@Test
	public void testSaveNotRemoveExtra(@TempDir Path tempDir) throws ConfigException, IOException {
		MappedConfigLoader<TestSingleInnerObject> configLoader = MappedTestCommons.newExistingConfig(tempDir, TestSingleInnerObject.class,
				"object:",
				"  normalPresent: 10",
				"extra: abc"
		);
		boolean changed = configLoader.saveIfDifferent(new TestSingleInnerObject());

		assertThat(changed).isFalse();
		AssertExtra.fileContentMatches(configLoader.getFile(),
				"object:",
				"  normalPresent: 10",
				"extra: abc"
		);
	}

	@Test
	public void testSaveSectionDifference(@TempDir Path tempDir) throws ConfigException, IOException {
		MappedConfigLoader<TestSingleInnerSection> configLoader = MappedTestCommons.newExistingConfig(tempDir, TestSingleInnerSection.class,
				"section:",
				"  string: xyz",
				"  extra: 1"
		);
		boolean changed = configLoader.saveIfDifferent(new TestSingleInnerSection());

		assertThat(changed).isTrue();
		AssertExtra.fileContentMatches(configLoader.getFile(),
				"section:",
				"  string: abc"
		);
	}

	@Test
	public void testNoSaveBecauseEqual(@TempDir Path tempDir) throws ConfigException {
		MappedConfigLoader<TestConfig> configLoader = MappedTestCommons.newNonExistingConfig(tempDir, TestConfig.class);
		configLoader.init();

		// Save the default value again
		boolean changed = configLoader.saveIfDifferent(new TestConfig());
		assertThat(changed).isFalse();
	}

	@Test
	public void testSaveObjectDifference(@TempDir Path tempDir) throws ConfigException, IOException {
		MappedConfigLoader<TestSingleInnerObject> configLoader = MappedTestCommons.newExistingConfig(tempDir, TestSingleInnerObject.class,
				"object:",
				"  normalPresent: 9"
		);
		boolean changed = configLoader.saveIfDifferent(new TestSingleInnerObject());

		assertThat(changed).isTrue();
		AssertExtra.fileContentMatches(configLoader.getFile(),
				"object:",
				"  normalPresent: 10"
		);
	}

	@Test
	public void testSaveListOfObjectsDifference(@TempDir Path tempDir) throws ConfigException, IOException {
		MappedConfigLoader<TestListOfObject> configLoader = MappedTestCommons.newExistingConfig(tempDir, TestListOfObject.class,
				"list:",
				"- normalPresent: 5",
				"  normalMissing: 6"
		);
		boolean changed = configLoader.saveIfDifferent(new TestListOfObject());

		assertThat(changed).isTrue();
		AssertExtra.fileContentMatches(configLoader.getFile(),
				"list:",
				"- normalPresent: 10",
				"- normalPresent: 1",
				"  normalMissing: 2"
		);
	}

	@Test
	public void testColorsExisting(@TempDir Path tempDir) throws ConfigException, IOException {
		String modifiedMessage = "&cModified";
		MappedConfigLoader<TestColors> configLoader = MappedTestCommons.newExistingConfig(tempDir, TestColors.class,
				"message: '" + modifiedMessage + "'"
		);
		TestColors testColors = configLoader.load();

		assertThat(testColors.message).isEqualTo(Colors.addColors(modifiedMessage));
	}

	@Test
	public void testColorsDefault(@TempDir Path tempDir) throws ConfigException, IOException {
		MappedConfigLoader<TestColors> configLoader = MappedTestCommons.newNonExistingConfig(tempDir, TestColors.class);
		TestColors testColors = configLoader.init();

		assertThat(testColors.message).isEqualTo(Colors.addColors(new TestColors().message));
		AssertExtra.fileContentMatches(configLoader.getFile(),
				"message: '" + new TestColors().message + "'"
		);
	}

	@Test
	public void testSaveConfigWithNullByDefault(@TempDir Path tempDir) throws ConfigException, IOException {
		MappedConfigLoader<TestSingleInnerObject> configLoader = MappedTestCommons.newExistingConfig(tempDir, TestSingleInnerObject.class,
				"object:",
				"  normalPresent: 10",
				"  normalMissing: 0"
		);
		boolean changed = configLoader.saveIfDifferent(new TestSingleInnerObject());

		assertThat(changed).isTrue();
		AssertExtra.fileContentMatches(configLoader.getFile(),
				"object:",
				"  normalPresent: 10"
		);
	}


	private static class TestConfig implements MappedConfig {


		private Integer normalPresent = 10;
		private Integer normalMissing;
		private int primitivePresent = 10;
		private int primitiveMissing;
		private List<Integer> listPresent = Arrays.asList(1, 2, 3);
		private List<Integer> listMissing;
		private ConfigSection sectionPresent = new ConfigSection();
		private ConfigSection sectionMissing;
		private TestConfigSection objectPresent = new TestConfigSection();
		private TestConfigSection objectMissing;

	}

	private static class TestConfigSection implements MappedConfigSection {

		private Integer normalPresent = 10;
		private Integer normalMissing;

		public TestConfigSection() {}

		public TestConfigSection(Integer normalPresent, Integer normalMissing) {
			this.normalPresent = normalPresent;
			this.normalMissing = normalMissing;
		}

	}

	private static class TestSingleInnerSection implements MappedConfig {

		private ConfigSection section;

		private TestSingleInnerSection() {
			section = new ConfigSection();
			section.setString("string", "abc");
		}

	}

	private static class TestSingleInnerObject implements MappedConfig {

		private TestConfigSection object = new TestConfigSection();

	}

	private static class TestListOfObject implements MappedConfig {

		private List<TestConfigSection> list = Arrays.asList(
				new TestConfigSection(),
				new TestConfigSection(1, 2)
		);

	}

	@ChatColors
	private static class TestColors implements MappedConfig {

		private String message = "&aHello";

	}

}