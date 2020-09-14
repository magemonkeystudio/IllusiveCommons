package me.filoghost.fcommons.config.mapped;

import me.filoghost.fcommons.config.ConfigSection;
import me.filoghost.fcommons.config.exception.ConfigException;
import me.filoghost.fcommons.config.exception.ConfigLoadException;
import me.filoghost.fcommons.config.exception.ConfigPostLoadException;
import me.filoghost.fcommons.config.exception.ConfigSaveException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class MappedConfigSectionTest {

	@Test
	public void testPostLoad(@TempDir Path tempDir) throws ConfigLoadException, ConfigSaveException {
		MappedConfigLoader<PostLoadConfig> configLoader = MappedTestCommons.newNonExistingConfig(tempDir, PostLoadConfig.class);

		PostLoadConfig config = configLoader.init(3);

		assertThat(config.postLoadCalled).isTrue();
		assertThat(config.postLoadContext).isEqualTo(3);
	}

	@Test
	public void testPostLoadWrongTypedContext(@TempDir Path tempDir) {
		MappedConfigLoader<PostLoadConfig> configLoader = MappedTestCommons.newNonExistingConfig(tempDir, PostLoadConfig.class);

		assertThatExceptionOfType(ClassCastException.class).isThrownBy(() -> {
			configLoader.init("bad context");
		});
	}

	@Test
	public void testPostLoadNullContext(@TempDir Path tempDir) throws ConfigLoadException, ConfigSaveException {
		MappedConfigLoader<PostLoadConfig> configLoader = MappedTestCommons.newNonExistingConfig(tempDir, PostLoadConfig.class);

		configLoader.init(null);
	}

	@Test
	public void testInitException(@TempDir Path tempDir) {
		MappedConfigLoader<PostLoadExceptionConfig> configLoader = MappedTestCommons.newNonExistingConfig(tempDir, PostLoadExceptionConfig.class);

		assertThatExceptionOfType(ConfigPostLoadException.class).isThrownBy(() -> {
			configLoader.init();
		});
	}

	@Test
	public void testLoadException(@TempDir Path tempDir) throws IOException {
		MappedConfigLoader<PostLoadExceptionConfig> configLoader = MappedTestCommons.newExistingConfig(tempDir, PostLoadExceptionConfig.class,
				"string: abc"
		);

		assertThatExceptionOfType(ConfigPostLoadException.class).isThrownBy(() -> {
			configLoader.load();
		});
	}

	@Test
	public void testSaveNotThrowException(@TempDir Path tempDir) throws IOException, ConfigException {
		MappedConfigLoader<PostLoadExceptionConfig> configLoader = MappedTestCommons.newExistingConfig(tempDir, PostLoadExceptionConfig.class,
				"string: abc"
		);

		// Save shouldn't invoke the postLoad() callback
		configLoader.save(new PostLoadExceptionConfig());
	}

	@Test
	public void testSaveNotDifferent(@TempDir Path tempDir) throws IOException, ConfigException {
		MappedConfigLoader<ComplexConfig> configLoader = MappedTestCommons.newExistingConfig(tempDir, ComplexConfig.class,
				"section:",
				"  key2: value2", // Order of keys shouldn't matter in sections (the same is true for the root section)
				"  key1: value1",
				"  key3: value3",
				"  key5: value5",
				"  key4: value4",
				"list:",
				"- 1",
				"- 2",
				"- 3",
				"nestedSection:",
				"  nested1:",
				"    nested2:",
				"      key: value"
		);

		boolean changed = configLoader.saveIfDifferent(new ComplexConfig());
		assertThat(changed).isFalse();
	}

	@Test
	public void testSaveDifferent(@TempDir Path tempDir) throws IOException, ConfigException {
		MappedConfigLoader<ComplexConfig> configLoader = MappedTestCommons.newExistingConfig(tempDir, ComplexConfig.class,
				"list:",
				"- 1",
				"- 2",
				"- string",
				"section:",
				"  key1: value1",
				"  key2: value2",
				"  key3: value3",
				"  key4: value4",
				"  key5: value5",
				"nestedSection:",
				"  nested1:",
				"    nested2:",
				"      key: value"
		);

		boolean changed = configLoader.saveIfDifferent(new ComplexConfig());
		assertThat(changed).isTrue();
	}

	private static class PostLoadConfig implements MappedConfig, PostLoadCallback, ContextualPostLoadCallback<Integer> {

		private String string = "abc";

		private transient boolean postLoadCalled;
		private transient Integer postLoadContext;

		@Override
		public void postLoad() {
			postLoadCalled = true;
		}

		@Override
		public void postLoad(Integer context) {
			postLoadContext = context;
		}

	}

	private static class PostLoadExceptionConfig implements MappedConfig, PostLoadCallback {

		private String string = "abc";

		@Override
		public void postLoad() throws ConfigPostLoadException {
			throw new ConfigPostLoadException("test");
		}

	}

	private static class ComplexConfig implements MappedConfig {

		private List<Integer> list = Arrays.asList(1, 2, 3);
		private ConfigSection section;
		private ConfigSection nestedSection;

		private ComplexConfig() {
			section = new ConfigSection();
			section.setString("key1", "value1");
			section.setString("key2", "value2");
			section.setString("key3", "value3");
			section.setString("key4", "value4");
			section.setString("key5", "value5");

			nestedSection = new ConfigSection();
			nestedSection.getOrCreateSection("nested1").getOrCreateSection("nested2").setString("key", "value");
		}

	}

}