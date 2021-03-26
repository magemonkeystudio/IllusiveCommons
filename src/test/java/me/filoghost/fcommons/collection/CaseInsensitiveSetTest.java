/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.collection;

import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

class CaseInsensitiveSetTest {

    @Test
    void add() {
        CaseInsensitiveSet set = CaseInsensitiveSet.create();
        set.add("A");

        assertThat(set.contains("a")).isTrue();
    }

    @Test
    void addNoDuplicates() {
        CaseInsensitiveSet set = CaseInsensitiveSet.create();
        set.add("a");
        set.add("A");

        assertThat(set.size()).isEqualTo(1);
    }

    @Test
    void remove() {
        CaseInsensitiveSet set = CaseInsensitiveSet.create();
        set.add("a");
        set.remove("A");

        assertThat(set.isEmpty()).isTrue();
    }

    @Test
    void addPreservesInitialCase() {
        CaseInsensitiveSet set = CaseInsensitiveSet.create();
        set.add("A");
        set.add("a");

        assertThat(getStringElements(set)).containsExactly("A");
    }

    @Test
    void removeIf() {
        CaseInsensitiveSet set = CaseInsensitiveSet.create();
        set.add("A");
        set.add("B");
        set.add("C");
        set.removeIf(element -> !element.equalsIgnoreCase("b"));

        assertThat(set.contains("b")).isTrue();
        assertThat(set.size()).isEqualTo(1);
    }

    @Test
    void editWithSetView() {
        CaseInsensitiveSet set = CaseInsensitiveSet.create();
        set.asSet().add(new CaseInsensitiveString("a"));
        
        assertThat(set.contains("a")).isTrue();
    }

    private Set<String> getStringElements(CaseInsensitiveSet set) {
        return set.asSet().stream().map(CaseInsensitiveString::getOriginalString).collect(Collectors.toSet());
    }

}
