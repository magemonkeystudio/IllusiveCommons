/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config;

import me.filoghost.fcommons.config.exception.ConfigValueException;
import me.filoghost.fcommons.config.exception.MissingConfigValueException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ConfigSectionTest {

    @Test
    void testGet() {
        ConfigSection section = new ConfigSection();
        section.setString("string", "abc");

        assertThat(section.getString("string")).isEqualTo("abc");
        assertThat(section.getString("string2")).isNull();
    }

    @Test
    void testGetWithDefault() {
        ConfigSection section = new ConfigSection();
        section.setString("string", "abc");

        assertThat(section.getString("string", "xyz")).isEqualTo("abc");
        assertThat(section.getString("string2", "xyz")).isEqualTo("xyz");
    }

    @Test
    void testGetRequired() throws ConfigValueException {
        ConfigSection section = new ConfigSection();
        section.setString("string", "abc");

        section.getRequiredString("string");
        assertThatExceptionOfType(MissingConfigValueException.class).isThrownBy(() -> {
            section.getRequiredString("string2");
        });
    }

    @Test
    void testGetNestedPath() {
        ConfigSection section = new ConfigSection();
        section.getOrCreateSection("s1").getOrCreateSection("s2").getOrCreateSection("s3").setInt("key", 3);
        int value = section.getInt("s1.s2.s3.key");

        assertThat(value).isEqualTo(3);
    }

    @Test
    void testSetNestedPath() throws ConfigValueException {
        ConfigSection section = new ConfigSection();
        section.setInt("s1.s2.s3.key", 3);
        int value = section.getRequiredConfigSection("s1").getRequiredConfigSection("s2").getRequiredConfigSection("s3").getInt("key");
        int valueAlt = section.getRequiredConfigSection("s1.s2").getRequiredConfigSection("s3").getInt("key");

        assertThat(value).isEqualTo(3);
        assertThat(valueAlt).isEqualTo(3);
    }

    @Test
    void testRemoveNestedPath() {
        ConfigSection section = new ConfigSection();
        section.setInt("s1.s2.s3.key", 3);
        section.setConfigSection("s1.s2.s3", null);

        assertThat(section.getInt("s1.s2.s3.key")).isEqualTo(0);
        assertThat(section.getConfigSection("s1.s2.s3")).isNull();
        assertThat(section.getConfigSection("s1.s2")).isNotNull();
    }

}