/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.collection;

import me.filoghost.fcommons.test.TestUtils;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class ForwardingSetTest {

    @Test
    void directlyImplementsAllSetMethods() {
        List<Method> methods = TestUtils.getInterfaceHierarchyMethods(Set.class);

        for (Method method : methods) {
            assertThatCode(() -> ForwardingSet.class.getDeclaredMethod(method.getName(), method.getParameterTypes()))
                    .withFailMessage("Does not override method " + method)
                    .doesNotThrowAnyException();
        }
    }

    @Test
    void testEqualsAndHashCode() {
        ForwardingSet<String> forwardingSet = new ForwardingSet<>(new HashSet<>());
        Set<String> set = new LinkedHashSet<>();
        
        for (char c : "ecdab".toCharArray()) {
            forwardingSet.add(String.valueOf(c));
            set.add(String.valueOf(c));
        }
        
        assertThat(forwardingSet).isEqualTo(set);
        assertThat(set).isEqualTo(forwardingSet);
        assertThat(forwardingSet).hasSameHashCodeAs(set);
    }

}
