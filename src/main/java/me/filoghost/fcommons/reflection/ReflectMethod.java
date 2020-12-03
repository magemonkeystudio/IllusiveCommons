/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.reflection;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class ReflectMethod<T> {

    private final Class<T> boxedExpectedReturnClass;
    private final Class<?> declaringClass;
    private final String name;
    private final Class<?>[] parameterTypes;

    private Method method;

    public static <T> ReflectMethod<T> lookup(ClassToken<T> expectedReturnClassToken, Class<?> declaringClass, String name, Class<?>... parameterTypes) {
        return new ReflectMethod<>(expectedReturnClassToken.asClass(), declaringClass, name, parameterTypes);
    }
    
    public static <T> ReflectMethod<T> lookup(Class<T> expectedReturnClass, Class<?> declaringClass, String name, Class<?>... parameterTypes) {
        return new ReflectMethod<>(expectedReturnClass, declaringClass, name, parameterTypes);
    }

    private ReflectMethod(Class<T> expectedReturnClass, Class<?> declaringClass, String name, Class<?>[] parameterTypes) {
        this.boxedExpectedReturnClass = ReflectUtils.boxPrimitiveClass(expectedReturnClass);
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
        }
    }

    public T invoke(Object instance, Object... args) throws ReflectiveOperationException {
        init();

        if (!Modifier.isStatic(method.getModifiers()) && instance == null) {
            throw new InvalidInstanceException("instance cannot be null when method is not static");
        }
        
        try {
            return boxedExpectedReturnClass.cast(method.invoke(instance, args));
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
