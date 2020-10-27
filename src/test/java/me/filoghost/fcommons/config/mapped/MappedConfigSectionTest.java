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
    void testPostLoad(@TempDir Path tempDir) throws ConfigLoadException, ConfigSaveException {
        MappedConfigLoader<PostLoadConfig> configLoader = MappedTestCommons.newNonExistingConfig(tempDir, PostLoadConfig.class);

        PostLoadConfig config = configLoader.init("abc");

        assertThat(config.postLoadCount).isEqualTo(1);
        assertThat(config.postLoadContextCount).isEqualTo(1);
        assertThat(config.postLoadContext).isEqualTo("abc");
    }

    @Test
    void testPostLoadWrongTypedContext(@TempDir Path tempDir) {
        MappedConfigLoader<PostLoadConfig> configLoader = MappedTestCommons.newNonExistingConfig(tempDir, PostLoadConfig.class);

        assertThatExceptionOfType(ClassCastException.class).isThrownBy(() -> {
            configLoader.init(123);
        });
    }

    @Test
    void testPostLoadNullContext(@TempDir Path tempDir) throws ConfigLoadException, ConfigSaveException {
        MappedConfigLoader<PostLoadConfig> configLoader = MappedTestCommons.newNonExistingConfig(tempDir, PostLoadConfig.class);

        configLoader.init(null);
    }

    @Test
    void testInitException(@TempDir Path tempDir) {
        MappedConfigLoader<PostLoadExceptionConfig> configLoader = MappedTestCommons.newNonExistingConfig(tempDir, PostLoadExceptionConfig.class);

        assertThatExceptionOfType(ConfigPostLoadException.class).isThrownBy(() -> {
            configLoader.init();
        });
    }

    @Test
    void testLoadException(@TempDir Path tempDir) throws IOException {
        MappedConfigLoader<PostLoadExceptionConfig> configLoader = MappedTestCommons.newExistingConfig(tempDir, PostLoadExceptionConfig.class,
                "string: abc"
        );

        assertThatExceptionOfType(ConfigPostLoadException.class).isThrownBy(() -> {
            configLoader.load();
        });
    }

    @Test
    void testSaveNotThrowException(@TempDir Path tempDir) throws IOException, ConfigException {
        MappedConfigLoader<PostLoadExceptionConfig> configLoader = MappedTestCommons.newExistingConfig(tempDir, PostLoadExceptionConfig.class,
                "string: abc"
        );

        // Save shouldn't invoke the postLoad() callback
        configLoader.save(new PostLoadExceptionConfig());
    }

    private static class PostLoadConfig implements MappedConfig, PostLoadCallback, ContextualPostLoadCallback<String> {

        private String string = "abc";

        private transient int postLoadCount;
        private transient int postLoadContextCount;
        private transient String postLoadContext;

        @Override
        public void postLoad() {
            postLoadCount++;
        }

        @Override
        public void postLoad(String context) {
            postLoadContext = context;
            postLoadContextCount++;
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