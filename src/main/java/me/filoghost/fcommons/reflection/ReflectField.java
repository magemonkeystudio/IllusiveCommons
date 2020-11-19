/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public interface ReflectField<T> {

    static <T> ReflectField<T> lookup(Class<T> fieldType, Class<?> declaringClass, String fieldName) {
        try {
            Field field = declaringClass.getDeclaredField(fieldName);
            return new ValidReflectField<>(fieldType, field);
        } catch (Throwable t) {
            return new UnknownReflectField<>(declaringClass, fieldName, t);
        }
    }

    static <T> ReflectField<T> wrap(Class<T> fieldType, Field field) {
        return new ValidReflectField<>(fieldType, field);
    }

    static ReflectField<?> wrap(Field field) {
        return new ValidReflectField<>(field.getType(), field);
    }

    TypeInfo<?> getDeclarationType() throws ReflectiveOperationException;

    TypeInfo<T> getCheckedDeclarationType() throws ReflectiveOperationException;

    T get(Object instance) throws ReflectiveOperationException;

    T getStatic() throws ReflectiveOperationException;

    void set(Object instance, T value) throws ReflectiveOperationException;

    void setStatic(T value) throws ReflectiveOperationException;

    String getName();

    Class<?> getDeclaringClass();

    int getModifiers();

    boolean isAnnotationPresent(Class<? extends Annotation> annotationClass);

    <A extends Annotation> A getAnnotation(Class<A> annotationClass);

    <A extends Annotation> A[] getAnnotationsByType(Class<A> annotationClass);

    Annotation[] getAnnotations();

}
