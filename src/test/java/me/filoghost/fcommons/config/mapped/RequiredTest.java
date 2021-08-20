/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.mapped;

import me.filoghost.fcommons.config.ConfigSection;
import me.filoghost.fcommons.config.exception.ConfigException;
import me.filoghost.fcommons.config.exception.ConfigLoadException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;

import static org.assertj.core.api.Assertions.*;

class RequiredTest {

    @Test
    void testCorrectConfig(@TempDir java.nio.file.Path tempDir) throws ConfigException, IOException {
        MappedConfigLoader<ConfigWithPaths> configLoader = MappedTestCommons.newExistingConfig(tempDir, ConfigWithPaths.class,
                "integer: 5",
                "string: xyz",
                "section: {}"
        );
        ConfigWithPaths configWithPaths = configLoader.load();

        assertThat(configWithPaths.integer).isEqualTo(5);
        assertThat(configWithPaths.string).isEqualTo("xyz");
    }

    @Test
    void testMissingString(@TempDir java.nio.file.Path tempDir) throws IOException {
        MappedConfigLoader<ConfigWithPaths> configLoader = MappedTestCommons.newExistingConfig(tempDir, ConfigWithPaths.class,
                "integer: 5",
                "section: {}"
        );

        assertThatExceptionOfType(ConfigLoadException.class).isThrownBy(() -> {
            configLoader.load();
        }).withMessageContaining("string");
    }

    @Test
    void testMissingSection(@TempDir java.nio.file.Path tempDir) throws IOException {
        MappedConfigLoader<ConfigWithPaths> configLoader = MappedTestCommons.newExistingConfig(tempDir, ConfigWithPaths.class,
                "integer: 5",
                "string: xyz"
        );

        assertThatExceptionOfType(ConfigLoadException.class).isThrownBy(() -> {
            configLoader.load();
        }).withMessageContaining("section");
    }

    private static class ConfigWithPaths implements MappedConfig {

        @Required
        private int integer;

        @Required
        private String string;

        @Required
        private ConfigSection section;

    }

}
