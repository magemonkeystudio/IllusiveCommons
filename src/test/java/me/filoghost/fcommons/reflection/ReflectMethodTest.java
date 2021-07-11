/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.reflection;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ReflectMethodTest {

    @Test
    void notExisting() {
        ReflectMethod<Object> field = ReflectMethod.lookup(Object.class, ClassWithMethods.class, "notExisting");

        assertThatExceptionOfType(NoSuchMethodException.class).isThrownBy(() -> {
            field.invoke(new ClassWithMethods());
        });
    }

    @Test
    void testReturn() throws ReflectiveOperationException {
        ReflectMethod<Integer> field = ReflectMethod.lookup(Integer.class, ClassWithMethods.class, "boxedInteger", Integer.class);

        assertThat(field.invoke(new ClassWithMethods(), 1)).isEqualTo(1);
    }

    @Test
    void testPrivate() throws ReflectiveOperationException {
        ReflectMethod<Void> field = ReflectMethod.lookup(Void.class, ClassWithMethods.class, "privateMethod");

        field.invoke(new ClassWithMethods());
    }

    @Test
    void testStatic() throws ReflectiveOperationException {
        ReflectMethod<Void> field = ReflectMethod.lookup(Void.class, ClassWithMethods.class, "staticMethod");

        field.invoke(null);
        field.invokeStatic();
    }

    @Test
    void testVoid() throws ReflectiveOperationException {
        ReflectMethod<Void> field = ReflectMethod.lookup(Void.class, ClassWithMethods.class, "voidMethod");

        assertThat(field.invoke(new ClassWithMethods())).isNull();
    }

    @Test
    void primitiveAsBoxed() throws ReflectiveOperationException {
        ReflectMethod<Integer> field = ReflectMethod.lookup(Integer.class, ClassWithMethods.class, "primitiveInteger", int.class);

        assertThat(field.invoke(new ClassWithMethods(), 1)).isEqualTo(1);
    }

    @Test
    void boxedAsPrimitive() throws ReflectiveOperationException {
        ReflectMethod<Integer> field = ReflectMethod.lookup(int.class, ClassWithMethods.class, "boxedInteger", Integer.class);

        assertThat(field.invoke(new ClassWithMethods(), 1)).isEqualTo(1);
    }

    @Test
    void wrongReturnTypeSuperClass() throws ReflectiveOperationException {
        ReflectMethod<Object> field = ReflectMethod.lookup(Object.class, ClassWithMethods.class, "number", Number.class);

        field.invoke(new ClassWithMethods(), 1); // Allowed
    }

    @Test
    void wrongReturnTypeSubClass() throws ReflectiveOperationException {
        ReflectMethod<Integer> field = ReflectMethod.lookup(Integer.class, ClassWithMethods.class, "number", Number.class);

        // Should not throw exception, because actual returned value is Integer
        field.invoke(new ClassWithMethods(), 1);
    }

    @Test
    void wrongInvokeArgumentsCount() {
        ReflectMethod<Number> field = ReflectMethod.lookup(Number.class, ClassWithMethods.class, "number", Number.class);

        assertThatExceptionOfType(ReflectiveOperationException.class).isThrownBy(() -> {
            field.invoke(new ClassWithMethods(), 1, 2, 3);
        });
    }

    @Test
    void wrongInvokeArgumentType() {
        ReflectMethod<Number> field = ReflectMethod.lookup(Number.class, ClassWithMethods.class, "number", Number.class);

        assertThatExceptionOfType(ReflectiveOperationException.class).isThrownBy(() -> {
            field.invoke(new ClassWithMethods(), new Object());
        });
    }

    @Test
    void wrongLookupArgumentTypeSubClass() {
        ReflectMethod<Number> field = ReflectMethod.lookup(Number.class, ClassWithMethods.class, "number", Integer.class);

        assertThatExceptionOfType(NoSuchMethodException.class).isThrownBy(() -> {
            field.invoke(new ClassWithMethods(), 1);
        });
    }

    @Test
    void wrongLookupArgumentTypeSuperClass() {
        ReflectMethod<Number> field = ReflectMethod.lookup(Number.class, ClassWithMethods.class, "number", Object.class);

        assertThatExceptionOfType(NoSuchMethodException.class).isThrownBy(() -> {
            field.invoke(new ClassWithMethods(), 1);
        });
    }


    private static class ClassWithMethods {

        private void privateMethod() {}

        public static void staticMethod() {}

        public void voidMethod() {}

        public Number number(Number number) {
            return number;
        }

        public Integer boxedInteger(Integer integer) {
            return integer;
        }

        public int primitiveInteger(int i) {
            return i;
        }

    }

}
