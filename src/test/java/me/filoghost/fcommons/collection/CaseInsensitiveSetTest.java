/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.collection;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

abstract class CaseInsensitiveSetTest {

    protected abstract CaseInsensitiveSet createCaseInsensitiveSet();

    @Test
    void contains() {
        CaseInsensitiveSet set = createCaseInsensitiveSet();
        set.add("A");

        assertThat(set.contains("a")).isTrue();
    }

    @Test
    void add() {
        CaseInsensitiveSet set = createCaseInsensitiveSet();
        set.add("A");
        set.add("a");

        assertThat(getStringElements(set)).containsExactly("A"); // The first value is not overridden
    }

    @Test
    void remove() {
        CaseInsensitiveSet set = createCaseInsensitiveSet();
        set.add("a");
        set.remove("A");

        assertThat(set).isEmpty();
    }

    @Test
    void addAll() {
        CaseInsensitiveSet set = createCaseInsensitiveSet();
        set.addAll("A", "b", "C");

        assertThat(getStringElements(set)).containsExactly("A", "b", "C");
    }

    private Set<String> getStringElements(CaseInsensitiveSet set) {
        Set<String> stringElements = new HashSet<>();
        set.forEach(element -> stringElements.add(element.getOriginalString()));
        return stringElements;
    }

}
