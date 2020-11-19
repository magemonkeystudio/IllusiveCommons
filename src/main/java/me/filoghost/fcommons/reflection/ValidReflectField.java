/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.reflection;

import me.filoghost.fcommons.Preconditions;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

@SuppressWarnings("unchecked")
public class ValidReflectField<T> implements ReflectField<T> {
    
    private final Class<T> expectedFieldType;
    private final Class<T> boxedExpectedFieldType;
    private final Field field;

    private boolean initialized;
    private boolean canRead;
    private boolean canWrite;

    protected ValidReflectField(Class<T> expectedFieldType, Field field) {
        Preconditions.notNull(expectedFieldType, "fieldType");
        Preconditions.notNull(field, "field");
        this.expectedFieldType = expectedFieldType;
        this.boxedExpectedFieldType = ReflectUtils.boxPrimitiveClass(expectedFieldType);
        this.field = field;
    }
    
    private void init() throws ReflectiveOperationException {
        if (initialized) {
            return;
        }
        
        try {
            field.setAccessible(true);
        } catch (Throwable t) {
            throw new ReflectiveOperationException(t);
        }


        Class<?> boxedActualFieldType = ReflectUtils.boxPrimitiveClass(field.getType());
        canRead = boxedExpectedFieldType.isAssignableFrom(boxedActualFieldType);
        canWrite = boxedActualFieldType.isAssignableFrom(boxedExpectedFieldType);
        
        initialized = true;
    }
    
    @Override
    public TypeInfo<?> getDeclarationType() throws ReflectiveOperationException {
        return TypeInfo.of(field);
    }

    @Override
    public TypeInfo<T> getCheckedDeclarationType() throws ReflectiveOperationException {
        TypeInfo<?> typeInfo = TypeInfo.of(field);
        
        if (typeInfo.getTypeClass() == expectedFieldType) {
            return (TypeInfo<T>) typeInfo;
        } else {
            throw new TypeNotCompatibleException("expected type \"" + expectedFieldType + "\""
                    + " must be equal to actual field type \"" + field.getType() + "\"");
        }
    }

    @Override
    public T getStatic() throws ReflectiveOperationException {
        return get(null);
    }

    @Override
    public T get(Object instance) throws ReflectiveOperationException {
        init();
        checkInstance(instance);
        
        if (!canRead) {
            throw new TypeNotCompatibleException("cannot safely read type \"" + expectedFieldType + "\""
                    + " from field type \"" + field.getType() + "\"");
        }
        
        try {
            return boxedExpectedFieldType.cast(field.get(instance));
        } catch (ReflectiveOperationException e) {
            throw e;
        } catch (Throwable t) {
            throw new ReflectiveOperationException(t);
        }
    }

    @Override
    public void setStatic(T value) throws ReflectiveOperationException {
        set(null, value);
    }

    @Override
    public void set(Object instance, T value) throws ReflectiveOperationException {
        init();
        checkInstance(instance);
        
        if (!canWrite) {
            throw new TypeNotCompatibleException("cannot safely write type \"" + expectedFieldType + "\""
                    + " into field type \"" + field.getType() + "\"");
        }
        
        try {
            field.set(instance, boxedExpectedFieldType.cast(value));
        } catch (ReflectiveOperationException e) {
            throw e;
        } catch (Throwable t) {
            throw new ReflectiveOperationException(t);
        }
    }

    private void checkInstance(Object instance) throws InvalidInstanceException {
        if (!Modifier.isStatic(getModifiers()) && instance == null) {
            throw new InvalidInstanceException("instance cannot be null when field is not static");
        }
    }

    @Override
    public String getName() {
        return field.getName();
    }

    @Override
    public Class<?> getDeclaringClass() {
        return field.getDeclaringClass();
    }

    @Override
    public int getModifiers() {
        return field.getModifiers();
    }

    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
        return field.isAnnotationPresent(annotationClass);
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
        return field.getAnnotation(annotationClass);
    }

    @Override
    public <A extends Annotation> A[] getAnnotationsByType(Class<A> annotationClass) {
        return field.getAnnotationsByType(annotationClass);
    }

    @Override
    public Annotation[] getAnnotations() {
        return field.getAnnotations();
    }

}
