/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.collection;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.assertj.core.api.Assertions.*;

class CaseInsensitiveStringTest {

    @Test
    void originalString() {
        assertThat(new CaseInsensitiveString("Abc").getOriginalString()).isEqualTo("Abc");
    }

    @Test
    void testToString() {
        assertThat(new CaseInsensitiveString("Abc").getOriginalString()).isEqualTo("Abc");
    }

    @Test
    void stringEquals() {
        assertThat(new CaseInsensitiveString("Abc").equalsIgnoreCase("abC")).isTrue();
    }

    @Test
    void testHashCode() {
        assertThat(new CaseInsensitiveString("Abc")).hasSameHashCodeAs(new CaseInsensitiveString("abC"));
    }

    @Test
    void testEquals() {
        assertThat(new CaseInsensitiveString("Abc")).isEqualTo(new CaseInsensitiveString("abC"));
    }

    @Test
    void turkeyLocale() {
        Locale systemLocale = Locale.getDefault();
        try {
            // Turkey locale has two types of lowercase "i" characters and two types of uppercase "I" characters,
            // which may cause issues with case insensitive comparisons.
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));

            assertThat(new CaseInsensitiveString("i")).isEqualTo(new CaseInsensitiveString("I"));
        } finally {
            Locale.setDefault(systemLocale);
        }
    }

}
