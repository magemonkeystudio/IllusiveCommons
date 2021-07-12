/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.mapped.modifier;

import me.filoghost.fcommons.Colors;
import me.filoghost.fcommons.config.exception.ConfigException;
import me.filoghost.fcommons.config.mapped.MappedConfig;
import me.filoghost.fcommons.config.mapped.MappedConfigLoader;
import me.filoghost.fcommons.config.mapped.MappedTestCommons;
import me.filoghost.fcommons.test.AssertExtra;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

class ChatColorsTest {

    @Test
    void testLoad(@TempDir Path tempDir) throws ConfigException, IOException {
        String modifiedMessage = "&cModified";
        MappedConfigLoader<TestColors> configLoader = MappedTestCommons.newExistingConfig(tempDir, TestColors.class,
                "message: '" + modifiedMessage + "'"
        );
        TestColors testColors = configLoader.load();

        assertThat(testColors.message).isEqualTo(Colors.colorize(modifiedMessage));
    }

    @Test
    void testSave(@TempDir Path tempDir) throws ConfigException, IOException {
        MappedConfigLoader<TestColors> configLoader = MappedTestCommons.newNonExistingConfig(tempDir, TestColors.class);
        TestColors testColors = configLoader.init();

        assertThat(testColors.message).isEqualTo(Colors.colorize(new TestColors().message));
        AssertExtra.fileContentMatches(configLoader.getFile(),
                "message: '" + new TestColors().message + "'"
        );
    }

    @ChatColors
    private static class TestColors implements MappedConfig {

        private String message = "&aHello";

    }

}
