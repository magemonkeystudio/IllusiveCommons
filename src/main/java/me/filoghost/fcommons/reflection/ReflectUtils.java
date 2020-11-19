/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.reflection;

public class ReflectUtils {

    public static boolean isClassLoaded(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<T> boxPrimitiveClass(Class<T> clazz) {
        if (clazz.isPrimitive()) {
            if (clazz == double.class) {
                return (Class<T>) Double.class;
            } else if (clazz == float.class) {
                return (Class<T>) Float.class;
            } else if (clazz == long.class) {
                return (Class<T>) Long.class;
            } else if (clazz == int.class) {
                return (Class<T>) Integer.class;
            } else if (clazz == short.class) {
                return (Class<T>) Short.class;
            } else if (clazz == byte.class) {
                return (Class<T>) Byte.class;
            } else if (clazz == char.class) {
                return (Class<T>) Character.class;
            } else if (clazz == boolean.class) {
                return (Class<T>) Boolean.class;
            } else if (clazz == void.class) {
                return (Class<T>) Void.class;
            }
        }

        return clazz;
    }

}
