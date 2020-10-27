package me.filoghost.fcommons.config;

import me.filoghost.fcommons.config.exception.ConfigException;
import me.filoghost.fcommons.config.exception.ConfigLoadException;
import me.filoghost.fcommons.config.exception.InvalidConfigValueException;
import me.filoghost.fcommons.config.exception.MissingConfigValueException;
import me.filoghost.fcommons.test.TestResources;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class ConfigTest {

    private static Config config;

    @BeforeAll
    static void setUp() throws ConfigLoadException {
        ConfigLoader configLoader = new ConfigLoader(TestResources.FOLDER, TestResources.get("test.yml"));
        config = configLoader.load();
    }

    @Test
    void testNonExistingPath() {
        assertThat(config.contains("non-existing-path")).isFalse();
    }

    @Test
    void testExplicitNulls() {
        assertThat(config.getString("null-value")).isNull();
        assertThat(config.getString("quoted-null-value")).isEqualTo("null");
    }

    @Test
    void testRequiredButMissing() {
        assertThatExceptionOfType(MissingConfigValueException.class).isThrownBy(() -> {
            config.getRequiredInt("missing-int");
        });
    }

    @Test
    void testRequiredButInvalid() {
        assertThatExceptionOfType(InvalidConfigValueException.class).isThrownBy(() -> {
            config.getRequiredInt("string");
        });
    }

    @Test
    void testSection() throws ConfigException {
        ConfigSection section = config.getRequired("section", ConfigValueType.SECTION);
        assertThat(section.getString("key1")).isEqualTo("value1");
        assertThat(section.getString("key2")).isEqualTo("value2");
    }

    @Test
    void testNestedSections() {
        List<ConfigSection> sections = config.get("nested-sections-list", ConfigValueType.SECTION_LIST);

        assertThat(sections).hasSize(2);
        assertThat(sections.get(0).get("nested1")).satisfies(v -> v.isPresentAs(ConfigValueType.SECTION));
        assertThat(sections.get(0).get("nested2")).satisfies(v -> v.isPresentAs(ConfigValueType.SECTION));
        assertThat(sections.get(1).get("nested1")).satisfies(v -> v.isPresentAs(ConfigValueType.SECTION));
        assertThat(sections.get(1).get("nested2")).satisfies(v -> v.isPresentAs(ConfigValueType.SECTION));

        assertThat(sections.get(0).get("nested1", ConfigValueType.SECTION).getString("key1")).isEqualTo("value1");
        assertThat(sections.get(0).get("nested2", ConfigValueType.SECTION).getString("key2")).isEqualTo("value2");
        assertThat(sections.get(1).get("nested1", ConfigValueType.SECTION).getString("key1")).isEqualTo("value3");
        assertThat(sections.get(1).get("nested2", ConfigValueType.SECTION).getString("key2")).isEqualTo("value4");
    }

    @Test
    void testList() throws ConfigException {
        assertThat(config.getRequired("list", ConfigValueType.STRING_LIST)).containsExactly(
                "one",
                "two",
                "three"
        );
    }

    @Test
    void testNestedList() throws ConfigException {
        List<String> innerList0 = config
                .get("nested-list").asRequired(ConfigValueType.LIST)
                .get(0).asRequired(ConfigValueType.STRING_LIST);

        assertThat(innerList0).containsExactly(
                "1",
                "2",
                "3"
        );
    }

    @Test
    void testSectionsList() {
        List<ConfigSection> sectionsList = config.get("sections-list", ConfigValueType.SECTION_LIST);
        assertThat(sectionsList).hasSize(2);
        assertThat(sectionsList.get(0).getString("key1")).isEqualTo("value1");
        assertThat(sectionsList.get(0).getString("key2")).isEqualTo("value2");
        assertThat(sectionsList.get(1).getString("key1")).isEqualTo("value3");
        assertThat(sectionsList.get(1).getString("key2")).isEqualTo("value4");
    }

    @Test
    void testListWithNull() {
        // Null values are skipped
        assertThat(config.get("list-with-null", ConfigValueType.STRING_LIST)).containsExactly(
                "one",
                "three"
        );
    }

    @Test
    void testWrappedListWithNull() throws ConfigException {
        // Null values are NOT skipped with wrapped values
        assertThat(config.getRequired("list-with-null", ConfigValueType.LIST)).containsExactly(
                ConfigValue.of(ConfigValueType.STRING, "one"),
                ConfigValue.NULL,
                ConfigValue.of(ConfigValueType.STRING, "three")
        );
    }

    @Test
    void testListWithInvalidElement() {
        // Invalid values are skipped
        assertThat(config.get("list-with-invalid-int", ConfigValueType.INTEGER_LIST)).containsExactly(
                1,
                3
        );
    }

}