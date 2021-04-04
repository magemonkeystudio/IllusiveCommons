/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.collection;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class CaseInsensitiveMapTest {

    @Test
    void get() {
        CaseInsensitiveMap<Integer> map = CaseInsensitiveMap.create();
        map.put("A", 1);

        assertThat(map.get("a")).isEqualTo(1);
    }

    @Test
    void putOverwrite() {
        CaseInsensitiveMap<Integer> map = CaseInsensitiveMap.create();
        map.put("a", 1);
        map.put("A", 2);

        assertThat(map.get("a")).isEqualTo(2);
    }

    @Test
    void remove() {
        CaseInsensitiveMap<Integer> map = CaseInsensitiveMap.create();
        map.put("a", 1);
        map.remove("A");

        assertThat(map.isEmpty()).isTrue();
    }

    @Test
    void putPreservesInitialCase() {
        CaseInsensitiveMap<Integer> map = CaseInsensitiveMap.create();
        map.put("A", 1);
        map.put("a", 2);

        assertThat(getStringKeys(map)).containsExactly("A");
    }

    @Test
    void entriesPreserveCase() {
        CaseInsensitiveMap<Integer> map = CaseInsensitiveMap.create();
        map.put("A", 1);
        map.put("b", 2);
        
        assertThat(getStringKeys(map)).containsExactly("A", "b");
    }

    @Test
    void removeIf() {
        CaseInsensitiveMap<Integer> map = CaseInsensitiveMap.create();
        map.put("A", 1);
        map.put("B", 2);
        map.put("C", 3);
        boolean removedA = map.removeIf("a", value -> value == 10); // Should NOT be removed
        boolean removedB = map.removeIf("b", value -> value == 2); // Should be removed
        boolean removedNonExisting = map.removeIf("d", value -> true);
        
        assertThat(removedA).isFalse();
        assertThat(removedB).isTrue();
        assertThat(removedNonExisting).isFalse();
        assertThat(map.containsKey("a")).isTrue();
        assertThat(map.containsKey("c")).isTrue();
        assertThat(map.size()).isEqualTo(2);
    }

    @Test
    void removeAllIf() {
        CaseInsensitiveMap<Integer> map = CaseInsensitiveMap.create();
        map.put("A", 1);
        map.put("B", 2);
        map.put("C", 3);
        map.removeAllIf((key, value) -> key.equalsIgnoreCase("a") || value == 3);

        assertThat(map.containsKey("b")).isTrue();
        assertThat(map.size()).isEqualTo(1);
    }

    @Test
    void removeKeysIf() {
        CaseInsensitiveMap<Integer> map = CaseInsensitiveMap.create();
        map.put("A", 1);
        map.put("B", 2);
        map.put("C", 3);
        map.removeKeysIf(key -> !key.equalsIgnoreCase("b"));

        assertThat(map.containsKey("b")).isTrue();
        assertThat(map.size()).isEqualTo(1);
    }

    @Test
    void removeValuesIf() {
        CaseInsensitiveMap<Integer> map = CaseInsensitiveMap.create();
        map.put("A", 1);
        map.put("B", 2);
        map.put("C", 3);
        map.removeValuesIf(value -> value != 2);

        assertThat(map.containsKey("b")).isTrue();
        assertThat(map.size()).isEqualTo(1);
    }
    
    private List<String> getStringKeys(CaseInsensitiveMap<?> map) {
        List<String> stringKeys = new ArrayList<>();
        map.forEachKey(key -> stringKeys.add(key.getOriginalString()));
        return stringKeys;
    }

}
