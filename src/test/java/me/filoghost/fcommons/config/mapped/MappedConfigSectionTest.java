package me.filoghost.fcommons.config.mapped;

import me.filoghost.fcommons.config.exception.ConfigException;
import me.filoghost.fcommons.config.exception.ConfigLoadException;
import me.filoghost.fcommons.config.exception.ConfigPostLoadException;
import me.filoghost.fcommons.config.exception.ConfigSaveException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;

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

}