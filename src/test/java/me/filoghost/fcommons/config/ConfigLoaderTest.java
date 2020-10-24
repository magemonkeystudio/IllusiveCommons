package me.filoghost.fcommons.config;

import com.google.common.base.Strings;
import me.filoghost.fcommons.config.exception.ConfigLoadException;
import me.filoghost.fcommons.config.exception.ConfigSaveException;
import me.filoghost.fcommons.test.AssertExtra;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class ConfigLoaderTest {

	@Test
	void testSerialization(@TempDir Path tempDir) throws ConfigSaveException, IOException {
		ConfigLoader configLoader = newNonExistingConfig(tempDir);
		Config config = new Config();

		config.setString("string", "abc");
		config.setInt("integer", 3);
		config.setStringList("list", Arrays.asList("a", "b", "c"));
		config.setString("nested.NestedKey", "value");

		ConfigSection section = new ConfigSection();
		section.setString("sectionKey", "value");
		config.setConfigSection("section", section);

		List<ConfigValue> listOfLists = new ArrayList<>();
		listOfLists.add(ConfigValue.of(ConfigValueType.STRING_LIST, Arrays.asList("a", "b", "c")));
		listOfLists.add(ConfigValue.of(ConfigValueType.INTEGER_LIST, Arrays.asList(1, 2, 3)));
		listOfLists.add(ConfigValue.of(ConfigValueType.SECTION, section));
		config.set("list-of-lists", ConfigValueType.LIST, listOfLists);

		configLoader.save(config);

		AssertExtra.fileContentMatches(configLoader.getFile(),
				"string: abc",
				"integer: 3",
				"list:",
				"- a",
				"- b",
				"- c",
				"nested:",
				"  NestedKey: value",
				"section:",
				"  sectionKey: value",
				"list-of-lists:",
				"- - a",
				"  - b",
				"  - c",
				"- - 1",
				"  - 2",
				"  - 3",
				"- sectionKey: value"
		);
	}

	@Test
	void testHeader(@TempDir Path tempDir) throws IOException, ConfigLoadException {
		ConfigLoader configLoader = newExistingConfig(tempDir,
				"# a",
				"# b",
				"key: value",
				"# other comment"
		);
		Config config = configLoader.load();
		assertThat(config.getHeader()).containsExactly("a", "b");
	}

	@Test
	void testHeaderWhitespaces(@TempDir Path tempDir) throws IOException, ConfigLoadException {
		ConfigLoader configLoader = newExistingConfig(tempDir,
				"#a",
				"# ",
				"# b",
				"#    c",
				"#",
				"# d   ",
				"#    ",
				"key: value",
				"#other comment"
		);
		Config config = configLoader.load();
		assertThat(config.getHeader()).containsExactly("a", "", "b", "   c", "", "d   ", "   ");
	}

	@Test
	void testHeaderEmptyLineBetween(@TempDir Path tempDir) throws IOException, ConfigLoadException {
		ConfigLoader configLoader = newExistingConfig(tempDir,
				"# a",
				"",
				"# b",
				"key: value"
		);
		Config config = configLoader.load();
		assertThat(config.getHeader()).containsExactly("a");
	}

	@Test
	void testHeaderEmptyLineBefore(@TempDir Path tempDir) throws IOException, ConfigLoadException {
		ConfigLoader configLoader = newExistingConfig(tempDir,
				"",
				"# a",
				"# b",
				"key: value"
		);
		Config config = configLoader.load();
		assertThat(config.getHeader()).containsExactly("a", "b");
	}

	@Test
	void testHeaderEmptyLineAfter(@TempDir Path tempDir) throws IOException, ConfigLoadException {
		ConfigLoader configLoader = newExistingConfig(tempDir,
				"# a",
				"# b",
				"",
				"key: value"
		);
		Config config = configLoader.load();
		assertThat(config.getHeader()).containsExactly("a", "b");
	}

	@Test
	void testLongStringsDontWrap(@TempDir Path tempDir) throws IOException, ConfigSaveException {
		ConfigLoader configLoader = newNonExistingConfig(tempDir);
		Config config = new Config();

		String longString = Strings.repeat("a 1 ", 100);
		config.setString("key", longString);
		configLoader.save(config);

		AssertExtra.fileContentMatches(configLoader.getFile(),
				"key: '" + longString + "'"
		);
	}

	static ConfigLoader newExistingConfig(Path tempDir, String... contents) throws IOException {
		Path configPath = tempDir.resolve("temp-config.yml");

		if (contents != null && contents.length > 0) {
			Files.write(configPath, Arrays.asList(contents));
		} else {
			Files.createFile(configPath);
		}

		return new ConfigLoader(tempDir, configPath);
	}

	static ConfigLoader newNonExistingConfig(Path tempDir) {
		Path configPath = tempDir.resolve("temp-config.yml");
		return new ConfigLoader(tempDir, configPath);
	}

}