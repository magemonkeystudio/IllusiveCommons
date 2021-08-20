/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.mapped;

import me.filoghost.fcommons.config.exception.ConfigException;
import me.filoghost.fcommons.config.exception.ConfigLoadException;
import me.filoghost.fcommons.config.exception.ConfigSaveException;
import me.filoghost.fcommons.config.exception.ConfigValidateException;
import me.filoghost.fcommons.config.exception.ConfigValueException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

class MappedConfigSectionTest {

    @Test
    void testAfterLoadWithLoad(@TempDir Path tempDir) throws ConfigLoadException, IOException, ConfigValueException {
        MappedConfigLoader<AfterLoadConfig> configLoader = MappedTestCommons.newExistingConfig(tempDir, AfterLoadConfig.class,
                "string: xyz"
        );

        AfterLoadConfig config = configLoader.load();

        assertThat(config.stringAfterLoad).isEqualTo("xyz");
    }

    @Test
    void testAfterLoadWithInit(@TempDir Path tempDir) throws ConfigLoadException, ConfigSaveException, ConfigValueException {
        MappedConfigLoader<AfterLoadConfig> configLoader = MappedTestCommons.newNonExistingConfig(tempDir, AfterLoadConfig.class);

        AfterLoadConfig config = configLoader.init();

        assertThat(config.stringAfterLoad).isEqualTo("abc");
    }

    @Test
    void testInitException(@TempDir Path tempDir) {
        MappedConfigLoader<AfterLoadExceptionConfig> configLoader = MappedTestCommons.newNonExistingConfig(tempDir, AfterLoadExceptionConfig.class);

        assertThatExceptionOfType(ConfigValidateException.class).isThrownBy(() -> {
            configLoader.init();
        });
    }

    @Test
    void testLoadException(@TempDir Path tempDir) throws IOException {
        MappedConfigLoader<AfterLoadExceptionConfig> configLoader = MappedTestCommons.newExistingConfig(tempDir, AfterLoadExceptionConfig.class,
                "string: abc"
        );

        assertThatExceptionOfType(ConfigValidateException.class).isThrownBy(() -> {
            configLoader.load();
        });
    }

    @Test
    void testSaveNotThrowException(@TempDir Path tempDir) throws IOException, ConfigException {
        MappedConfigLoader<AfterLoadExceptionConfig> configLoader = MappedTestCommons.newExistingConfig(tempDir, AfterLoadExceptionConfig.class,
                "string: abc"
        );

        // Save shouldn't invoke the afterLoad() callback
        configLoader.save(new AfterLoadExceptionConfig());
    }

    private static class AfterLoadConfig implements MappedConfig {

        private String string = "abc";

        private transient String stringAfterLoad;

        @Override
        public void afterLoad() {
            stringAfterLoad = string;
        }

    }

    private static class AfterLoadExceptionConfig implements MappedConfig {

        @Override
        public void afterLoad() throws ConfigValidateException {
            throw new ConfigValidateException("test");
        }

    }

}
