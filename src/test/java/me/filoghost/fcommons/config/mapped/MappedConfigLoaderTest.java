/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.mapped;

import me.filoghost.fcommons.config.ConfigSection;
import me.filoghost.fcommons.config.exception.ConfigException;
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
    void testInitNew(@TempDir Path tempDir) throws ConfigException, IOException {
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
    void testInitExisting(@TempDir Path tempDir) throws ConfigException, IOException {
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
    void testLoad(@TempDir Path tempDir) throws ConfigException, IOException {
        MappedConfigLoader<TestConfig> configLoader = MappedTestCommons.newExistingConfig(tempDir, TestConfig.class,
                "normalMissing: 5"
        );
        TestConfig config = configLoader.load();

        assertThat(config.normalMissing).isEqualTo(5);
        assertThat(config.normalPresent).isEqualTo(10);
        AssertExtra.fileContentMatches(configLoader.getFile(),
                "normalMissing: 5"
        );
    }

    @Test
    void testSave(@TempDir Path tempDir) throws ConfigException, IOException {
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
    void testSaveNotRemoveExtra(@TempDir Path tempDir) throws ConfigException, IOException {
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
    void testSaveSectionDifference(@TempDir Path tempDir) throws ConfigException, IOException {
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
    void testSaveExistingFileWithDefaultPrimitive(@TempDir Path tempDir) throws ConfigException, IOException {
        MappedConfigLoader<TestPrimitiveConfig> configLoader = MappedTestCommons.newExistingConfig(tempDir, TestPrimitiveConfig.class);
        boolean changed = configLoader.saveIfDifferent(new TestPrimitiveConfig());

        assertThat(changed).isTrue();
        AssertExtra.fileContentMatches(configLoader.getFile(),
                "integer: 0"
        );
    }

    @Test
    void testSaveExistingFileWithDefaults(@TempDir Path tempDir) throws ConfigException, IOException {
        MappedConfigLoader<TestSingleInnerSection> configLoader = MappedTestCommons.newExistingConfig(tempDir, TestSingleInnerSection.class);
        boolean changed = configLoader.saveIfDifferent(new TestSingleInnerSection());

        assertThat(changed).isTrue();
        AssertExtra.fileContentMatches(configLoader.getFile(),
                "section:",
                "  string: abc"
        );
    }

    @Test
    void testSaveNonExistingFileWithDefaults(@TempDir Path tempDir) throws ConfigException, IOException {
        MappedConfigLoader<TestSingleInnerSection> configLoader = MappedTestCommons.newNonExistingConfig(tempDir, TestSingleInnerSection.class);
        boolean changed = configLoader.saveIfDifferent(new TestSingleInnerSection());

        assertThat(changed).isTrue();
        AssertExtra.fileContentMatches(configLoader.getFile(),
                "section:",
                "  string: abc"
        );
    }

    @Test
    void testNoSaveBecauseEqual(@TempDir Path tempDir) throws ConfigException {
        MappedConfigLoader<TestConfig> configLoader = MappedTestCommons.newNonExistingConfig(tempDir, TestConfig.class);
        configLoader.init();

        // Save the default value again
        boolean changed = configLoader.saveIfDifferent(new TestConfig());
        assertThat(changed).isFalse();
    }

    @Test
    void testSaveObjectDifference(@TempDir Path tempDir) throws ConfigException, IOException {
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
    void testSaveListOfObjectsDifferentElement(@TempDir Path tempDir) throws ConfigException, IOException {
        MappedConfigLoader<TestListOfObject> configLoader = MappedTestCommons.newExistingConfig(tempDir, TestListOfObject.class,
                "list:",
                "- normalPresent: 9",
                "- normalPresent: 1",
                "  normalMissing: 2"
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
    void testSaveListOfObjectsMissingElement(@TempDir Path tempDir) throws ConfigException, IOException {
        MappedConfigLoader<TestListOfObject> configLoader = MappedTestCommons.newExistingConfig(tempDir, TestListOfObject.class,
                "list:",
                "- normalPresent: 10"
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
    void testSaveNotDifferent(@TempDir Path tempDir) throws IOException, ConfigException {
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
    void testSaveDifferent(@TempDir Path tempDir) throws IOException, ConfigException {
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

    @Test
    void testSaveConfigWithNullByDefault(@TempDir Path tempDir) throws ConfigException, IOException {
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

    private static class TestPrimitiveConfig implements MappedConfig {

        private int integer = 0;

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
