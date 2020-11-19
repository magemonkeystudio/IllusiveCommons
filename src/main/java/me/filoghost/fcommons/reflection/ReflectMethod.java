/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.fcommons.reflection;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

@SuppressWarnings("unchecked")
public class ReflectMethod<T> {

    private final Class<T> expectedReturnType;
    private final Class<T> boxedExpectedReturnType;
    private final Class<?> declaringClass;
    private final String name;
    private final Class<?>[] parameterTypes;

    private Method method;
    private boolean canCastReturnType;

    public static <T> ReflectMethod<T> lookup(Class<T> returnType, Class<?> declaringClass, String name, Class<?>... parameterTypes) {
        return new ReflectMethod<>(returnType, declaringClass, name, parameterTypes);
    }

    private ReflectMethod(Class<T> expectedReturnType, Class<?> declaringClass, String name, Class<?>[] parameterTypes) {
        this.expectedReturnType = expectedReturnType;
        this.boxedExpectedReturnType = ReflectUtils.boxPrimitiveClass(expectedReturnType);
        this.declaringClass = declaringClass;
        this.name = name;
        this.parameterTypes = parameterTypes;
    }

    private void init() throws ReflectiveOperationException {
        if (method == null) {
            try {
                method = declaringClass.getDeclaredMethod(name, parameterTypes);
                method.setAccessible(true);
            } catch (ReflectiveOperationException e) {
                throw e;
            } catch (Throwable t) {
                throw new ReflectiveOperationException(t);
            }

            Class<?> boxedActualReturnType = ReflectUtils.boxPrimitiveClass(method.getReturnType());
            canCastReturnType = boxedExpectedReturnType.isAssignableFrom(boxedActualReturnType);
        }
    }

    public T invoke(Object instance, Object... args) throws ReflectiveOperationException {
        init();

        if (!Modifier.isStatic(method.getModifiers()) && instance == null) {
            throw new InvalidInstanceException("instance cannot be null when method is not static");
        }

        if (!canCastReturnType) {
            throw new TypeNotCompatibleException("cannot safely cast type \"" + expectedReturnType + "\""
                    + " from method return type \"" + method.getReturnType() + "\"");
        }
        
        try {
            return boxedExpectedReturnType.cast(method.invoke(instance, args));
        } catch (ReflectiveOperationException e) {
            throw e;
        } catch (Throwable t) {
            throw new ReflectiveOperationException(t);
        }
    }

    public T invokeStatic(Object... args) throws ReflectiveOperationException {
        return invoke(null, args);
    }

    public Class<?> getDeclaringClass() {
        return declaringClass;
    }

    public String getName() {
        return name;
    }

}
