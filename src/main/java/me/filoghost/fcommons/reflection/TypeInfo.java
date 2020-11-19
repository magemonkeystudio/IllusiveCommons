/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.reflection;

import me.filoghost.fcommons.Preconditions;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.stream.Collectors;

public class TypeInfo<T> {

    private final Class<T> typeClass;
    private final Type[] typeArguments;

    private TypeInfo(Class<T> typeClass, Type... typeArguments) {
        this.typeClass = typeClass;
        this.typeArguments = typeArguments;
    }

    public Class<T> getTypeClass() {
        return typeClass;
    }

    public Type[] getTypeArguments() {
        return typeArguments;
    }

    public T cast(Object object) {
        return typeClass.cast(object);
    }

    public ReflectField<?>[] getDeclaredFields() throws ReflectiveOperationException {
        Field[] declaredFields;
        try {
            declaredFields = typeClass.getDeclaredFields();
        } catch (Throwable t) {
            throw new ReflectiveOperationException(t);
        }
        
        ReflectField<?>[] output = new ReflectField[declaredFields.length];
        for (int i = 0; i < declaredFields.length; i++) {
            output[i] = ReflectField.wrap(declaredFields[i]);
        }
        return output;
    }

    public T newInstance() throws ReflectiveOperationException {
        try {
            Constructor<T> constructor = typeClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw e;
        } catch (Throwable t) {
            throw new ReflectiveOperationException(t);
        }
    }

    public static TypeInfo<?> of(Field field) throws ReflectiveOperationException {
        Preconditions.notNull(field, "field");

        Type genericType;
        try {
            genericType = field.getGenericType();
        } catch (Throwable t) {
            throw new ReflectiveOperationException(t);
        }
        return of(genericType);
    }

    public static <T> TypeInfo<T> of(Class<T> clazz) {
        return new TypeInfo<>(clazz);
    }

    public static TypeInfo<?> of(Type type) throws ReflectiveOperationException {
        Preconditions.notNull(type, "type");

        try {
            Class<?> typeClass;
            Type[] typeArguments;

            if (type instanceof Class) {
                typeClass = (Class<?>) type;
                typeArguments = null;
            } else if (type instanceof ParameterizedType) {
                typeClass = (Class<?>) ((ParameterizedType) type).getRawType();
                typeArguments = ((ParameterizedType) type).getActualTypeArguments();
            } else {
                throw new ReflectiveOperationException("type is not a Class or ParameterizedType");
            }

            return new TypeInfo<>(typeClass, typeArguments);

        } catch (Throwable t) {
            throw new ReflectiveOperationException(t);
        }
    }

    @Override
    public String toString() {
        String output = typeClass.toString();

        if (typeArguments != null && typeArguments.length > 0) {
            output += Arrays.stream(typeArguments)
                    .map(Object::toString)
                    .collect(Collectors.joining(", ", "<", ">"));
        }

        return output;
    }

}
