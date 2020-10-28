/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.collection;

import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class CaseInsensitiveMapTest {

    @Test
    void get() {
        Map<String, Integer> map = new CaseInsensitiveMap<>();
        map.put("A", 1);

        assertThat(map.get("a")).isEqualTo(1);
    }

    @Test
    void putOverwrite() {
        Map<String, Integer> map = new CaseInsensitiveMap<>();
        map.put("A", 1);
        map.put("a", 2);

        assertThat(map.get("A")).isEqualTo(2);
        assertThat(map).hasSize(1);
    }

    @Test
    void remove() {
        Map<String, Integer> map = new CaseInsensitiveMap<>();
        map.put("A", 1);
        map.remove("a");

        assertThat(map.get("A")).isNull();
        assertThat(map).isEmpty();
    }

    @Test
    void turkeyLocale() {
        Locale systemLocale = Locale.getDefault();
        try {
            // Turkey locale has two types of lowercase "i" characters and two types of uppercase "I" characters,
            // which may cause issues with case insensitive comparisons.
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));

            Map<String, Integer> map = new CaseInsensitiveMap<>();
            map.put("i", 1);
            map.put("II", 2);

            assertThat(map.get("I")).isEqualTo(1);
            assertThat(map.get("ii")).isEqualTo(2);

        } finally {
            Locale.setDefault(systemLocale);
        }
    }

}