/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.reflection;

import java.lang.annotation.Annotation;

/**
 * Returned when the internal initialization of a ReflectField throws an exception.
 * Always throws the initial exception on any method calls.
 */
public class UnknownReflectField<T> implements ReflectField<T> {

    private static final Object EMPTY_ARRAY = new Object[0];

    private final Class<?> declaringClass;
    private final String fieldName;
    private final ReflectiveOperationException error;

    public UnknownReflectField(Class<?> declaringClass, String fieldName, Throwable error) {
        this.declaringClass = declaringClass;
        this.fieldName = fieldName;
        if (error instanceof ReflectiveOperationException) {
            this.error = (ReflectiveOperationException) error;
        } else {
            this.error = new ReflectiveOperationException(error);
        }
    }

    @Override
    public TypeInfo<?> getDeclarationType() throws ReflectiveOperationException {
        throw error;
    }

    @Override
    public TypeInfo<T> getCheckedDeclarationType() throws ReflectiveOperationException {
        throw error;
    }

    @Override
    public T get(Object instance) throws ReflectiveOperationException {
        throw error;
    }

    @Override
    public T getStatic() throws ReflectiveOperationException {
        throw error;
    }

    @Override
    public void set(Object instance, T value) throws ReflectiveOperationException {
        throw error;
    }

    @Override
    public void setStatic(T value) throws ReflectiveOperationException {
        throw error;
    }

    @Override
    public String getName() {
        return fieldName;
    }

    @Override
    public Class<?> getDeclaringClass() {
        return declaringClass;
    }

    @Override
    public int getModifiers() {
        return 0;
    }

    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
        return false;
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <A extends Annotation> A[] getAnnotationsByType(Class<A> annotationClass) {
        return (A[]) EMPTY_ARRAY;
    }

    @Override
    public Annotation[] getAnnotations() {
        return (Annotation[]) EMPTY_ARRAY;
    }

}
