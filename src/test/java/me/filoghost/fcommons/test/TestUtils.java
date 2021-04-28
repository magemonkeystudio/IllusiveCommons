/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestUtils {

    public static List<Method> getInterfaceHierarchyMethods(Class<?> interfaceClass) {
        List<Method> methods = new ArrayList<>();
        addInterfaceMethodsRecursively(methods, interfaceClass);
        return methods;
    }

    public static void addInterfaceMethodsRecursively(List<Method> collector, Class<?> interfaceClass) {
        Collections.addAll(collector, interfaceClass.getDeclaredMethods());
        for (Class<?> superInterface : interfaceClass.getInterfaces()) {
            addInterfaceMethodsRecursively(collector, superInterface);
        }
    }

}
