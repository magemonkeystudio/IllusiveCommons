/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.mapped;

import me.filoghost.fcommons.config.exception.ConfigException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.*;

class PathTest {

    @Test
    void testSaveConfig(@TempDir java.nio.file.Path tempDir) throws ConfigException, IOException {
        MappedConfigLoader<ConfigWithPaths> configLoader = MappedTestCommons.newNonExistingConfig(tempDir, ConfigWithPaths.class);
        configLoader.init();

        assertThat(configLoader.getFile()).exists();
        assertThat(Files.readAllLines(configLoader.getFile())).containsExactlyInAnyOrder(
                "integer-alt: 3",
                "string-alt: abc"
        );
    }

    @Test
    void testLoadConfig(@TempDir java.nio.file.Path tempDir) throws ConfigException, IOException {
        MappedConfigLoader<ConfigWithPaths> configLoader = MappedTestCommons.newExistingConfig(tempDir, ConfigWithPaths.class,
                "integer-alt: 5",
                "string-alt: xyz"
        );
        ConfigWithPaths configWithPaths = configLoader.load();

        assertThat(configWithPaths.integer).isEqualTo(5);
        assertThat(configWithPaths.string).isEqualTo("xyz");
    }

    private static class ConfigWithPaths implements MappedConfig {

        @Path("integer-alt")
        private int integer = 3;

        @Path("string-alt")
        private String string = "abc";

    }

}
