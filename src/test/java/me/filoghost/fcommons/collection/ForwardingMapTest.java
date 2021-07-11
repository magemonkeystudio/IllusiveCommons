/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.collection;

import me.filoghost.fcommons.test.TestUtils;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class ForwardingMapTest {

    @Test
    void directlyImplementsAllMapMethods() {
        List<Method> methods = TestUtils.getInterfaceHierarchyMethods(Map.class);

        for (Method method : methods) {
            assertThatCode(() -> ForwardingMap.class.getDeclaredMethod(method.getName(), method.getParameterTypes()))
                    .withFailMessage("Does not override method " + method)
                    .doesNotThrowAnyException();
        }
    }

    @Test
    void testEqualsAndHashCode() {
        ForwardingMap<String, Integer> forwardingMap = new ForwardingMap<>(new HashMap<>());
        Map<String, Integer> map = new LinkedHashMap<>();

        for (char c : "ecdab".toCharArray()) {
            forwardingMap.put(String.valueOf(c), (int) c);
            map.put(String.valueOf(c), (int) c);
        }

        assertThat(forwardingMap).isEqualTo(map);
        assertThat(map).isEqualTo(forwardingMap);
        assertThat(forwardingMap).hasSameHashCodeAs(map);
    }

}
