/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.collection;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

abstract class CaseInsensitiveMapTest {

    protected abstract CaseInsensitiveMap<Integer> createCaseInsensitiveMap();

    @Test
    void get() {
        CaseInsensitiveMap<Integer> map = createCaseInsensitiveMap();
        map.put("A", 1);

        assertThat(map.get("a")).isEqualTo(1);
    }

    @Test
    void remove() {
        CaseInsensitiveMap<Integer> map = createCaseInsensitiveMap();
        map.put("a", 1);
        map.remove("A");

        assertThat(map).isEmpty();
    }

    @Test
    void putOverwrite() {
        CaseInsensitiveMap<Integer> map = createCaseInsensitiveMap();
        map.put("a", 1);
        map.put("A", 2);

        assertThat(map.get("a")).isEqualTo(2);
    }

    @Test
    void putDoesNotChangeKeyCase() {
        CaseInsensitiveMap<Integer> map = createCaseInsensitiveMap();
        map.put("A", 1);
        map.put("a", 2);

        assertThat(getStringKeys(map)).containsExactly("A");
    }

    @Test
    void putAll() {
        CaseInsensitiveMap<Integer> map = createCaseInsensitiveMap();
        
        Map<String, Integer> stringMap = new HashMap<>();
        stringMap.put("A", 1);
        stringMap.put("b", 2);
        stringMap.put("C", 3);
        map.putAllString(stringMap);
        
        assertThat(getStringKeys(map)).containsExactly("A", "b", "C");
    }

    @Test
    void removeIf() {
        CaseInsensitiveMap<Integer> map = createCaseInsensitiveMap();
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
        CaseInsensitiveMap<Integer> map = createCaseInsensitiveMap();
        map.put("A", 1);
        map.put("B", 2);
        map.put("C", 3);
        map.removeAllIf((key, value) -> key.equalsIgnoreCase("a") || value == 3);

        assertThat(map.containsKey("b")).isTrue();
        assertThat(map.size()).isEqualTo(1);
    }

    private List<String> getStringKeys(CaseInsensitiveMap<?> map) {
        return map.keySet().stream().map(CaseInsensitiveString::getOriginalString).collect(Collectors.toList());
    }

}
