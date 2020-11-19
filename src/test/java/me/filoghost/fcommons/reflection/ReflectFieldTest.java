/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.reflection;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ReflectFieldTest {

    @Test
    void notExisting() {
        ReflectField<Object> field = ReflectField.lookup(Object.class, ClassWithFields.class, "notExisting");
        
        assertThatExceptionOfType(NoSuchFieldException.class).isThrownBy(() -> {
            assertThat(field.get(new ClassWithFields()));
        });
    }

    @Test
    void getPrivate() throws ReflectiveOperationException {
        ReflectField<Object> field = ReflectField.lookup(Object.class, ClassWithFields.class, "privateObject");

        ClassWithFields instance = new ClassWithFields();
        instance.privateObject = "string";

        assertThat(field.get(instance)).isEqualTo("string");
    }

    @Test
    void setPrivate() throws ReflectiveOperationException {
        ReflectField<Object> field = ReflectField.lookup(Object.class, ClassWithFields.class, "privateObject");

        ClassWithFields instance = new ClassWithFields();
        field.set(instance, true);
        
        assertThat(instance.privateObject).isEqualTo(true);
    }

    @Test
    void getStatic() throws ReflectiveOperationException {
        ReflectField<Object> field = ReflectField.lookup(Object.class, ClassWithFields.class, "staticObject");

        ClassWithFields.staticObject = "string";

        assertThat(field.get(null)).isEqualTo("string");
        assertThat(field.getStatic()).isEqualTo("string");
    }

    @Test
    void setStatic() throws ReflectiveOperationException {
        ReflectField<Object> field = ReflectField.lookup(Object.class, ClassWithFields.class, "staticObject");
        
        field.set(null, 1);
        assertThat(ClassWithFields.staticObject).isEqualTo(1);
        
        field.setStatic(2);
        assertThat(ClassWithFields.staticObject).isEqualTo(2);
    }

    @Test
    void getWrongStatic() {
        ReflectField<String> field = ReflectField.lookup(String.class, ClassWithFields.class, "string");

        assertThatExceptionOfType(InvalidInstanceException.class).isThrownBy(() -> {
            field.getStatic();
        });
    }

    @Test
    void setWrongStatic() {
        ReflectField<String> field = ReflectField.lookup(String.class, ClassWithFields.class, "string");

        assertThatExceptionOfType(InvalidInstanceException.class).isThrownBy(() -> {
            field.setStatic("abc");
        });
    }

    @Test
    void getNull() throws ReflectiveOperationException {
        ReflectField<String> field = ReflectField.lookup(String.class, ClassWithFields.class, "string");

        ClassWithFields instance = new ClassWithFields();
        instance.string = null;

        assertThat(field.get(instance)).isNull();
    }

    @Test
    void setNull() throws ReflectiveOperationException {
        ReflectField<String> field = ReflectField.lookup(String.class, ClassWithFields.class, "string");

        ClassWithFields instance = new ClassWithFields();
        field.set(instance, null);

        assertThat(instance.string).isNull();
    }

    @Test
    void getPrimitive() throws ReflectiveOperationException {
        ReflectField<Integer> field = ReflectField.lookup(int.class, ClassWithFields.class, "primitiveInt");

        ClassWithFields instance = new ClassWithFields();
        instance.primitiveInt = 1;
        
        assertThat(field.get(instance)).isEqualTo(1);
    }

    @Test
    void setPrimitive() throws ReflectiveOperationException {
        ReflectField<Integer> field = ReflectField.lookup(int.class, ClassWithFields.class, "primitiveInt");

        ClassWithFields instance = new ClassWithFields();
        field.set(instance, 1);

        assertThat(instance.primitiveInt).isEqualTo(1);
    }

    @Test
    void getUsingSuperClass() throws ReflectiveOperationException {
        ReflectField<Object> field = ReflectField.lookup(Object.class, ClassWithFields.class, "number");

        ClassWithFields instance = new ClassWithFields();
        instance.number = 1;
        
        assertThat(field.get(instance)).isEqualTo(1);
    }

    @Test
    void setCorrectUnsafeTypeUsingSuperClass() {
        ReflectField<Object> field = ReflectField.lookup(Object.class, ClassWithFields.class, "number");

        assertThatExceptionOfType(TypeNotCompatibleException.class).isThrownBy(() -> {
            // Cannot set Object where field type is Number (even if actual value is Double)
            field.set(new ClassWithFields(), 1.23);
        });
    }

    @Test
    void setWrongTypeUsingSuperClass() {
        ReflectField<Object> field = ReflectField.lookup(Object.class, ClassWithFields.class, "number");

        assertThatExceptionOfType(TypeNotCompatibleException.class).isThrownBy(() -> {
            // Cannot set Object where field type is Number (and actual value is Object)
            field.set(new ClassWithFields(), new Object());
        });
    }

    @Test
    void getCorrectUnsafeTypeUsingSubClass() {
        ReflectField<Integer> field = ReflectField.lookup(Integer.class, ClassWithFields.class, "number");

        ClassWithFields instance = new ClassWithFields();
        instance.number = 1;

        assertThatExceptionOfType(TypeNotCompatibleException.class).isThrownBy(() -> {
            // Cannot get Integer where field type is Number (even if actual value is Integer)
            field.get(instance);
        });
    }

    @Test
    void getWrongTypeUsingSubClass() {
        ReflectField<Double> field = ReflectField.lookup(Double.class, ClassWithFields.class, "number");

        ClassWithFields instance = new ClassWithFields();
        instance.number = 1;  
        
        assertThatExceptionOfType(TypeNotCompatibleException.class).isThrownBy(() -> {
            // Cannot get Double where field type is Number (and actual value is Integer)
            field.get(instance);
        });
    }

    @Test
    void setUsingSubClass() throws ReflectiveOperationException {
        ReflectField<Double> field = ReflectField.lookup(Double.class, ClassWithFields.class, "number");

        ClassWithFields instance = new ClassWithFields();
        field.set(instance, 1.23);

        assertThat(instance.number).isEqualTo(1.23);
    }

    @Test
    void setPrimitiveAsBoxed() throws ReflectiveOperationException {
        ReflectField<Integer> field = ReflectField.lookup(Integer.class, ClassWithFields.class, "primitiveInt");

        ClassWithFields instance = new ClassWithFields();
        field.set(instance, 1);

        assertThat(instance.primitiveInt).isEqualTo(1);
    }

    @Test
    void setPrimitiveAsBoxedNull() {
        ReflectField<Integer> field = ReflectField.lookup(Integer.class, ClassWithFields.class, "primitiveInt");
        
        assertThatExceptionOfType(ReflectiveOperationException.class).isThrownBy(() -> {
            field.set(new ClassWithFields(), null);
        });
    }

    @Test
    void getPrimitiveAsBoxed() throws ReflectiveOperationException {
        ReflectField<Integer> field = ReflectField.lookup(Integer.class, ClassWithFields.class, "primitiveInt");

        ClassWithFields instance = new ClassWithFields();
        instance.primitiveInt = 1;
        
        assertThat(field.get(instance)).isEqualTo(1);
    }

    @Test
    void getPrimitiveAsObject() throws ReflectiveOperationException {
        ReflectField<Object> field = ReflectField.lookup(Object.class, ClassWithFields.class, "primitiveInt");

        ClassWithFields instance = new ClassWithFields();
        instance.primitiveInt = 1;

        assertThat(field.get(instance)).isEqualTo(1);
    }

    @Test
    void setBoxedAsPrimitive() throws ReflectiveOperationException {
        ReflectField<Integer> field = ReflectField.lookup(int.class, ClassWithFields.class, "boxedInt");

        ClassWithFields instance = new ClassWithFields();
        field.set(instance, 1);

        assertThat(instance.boxedInt).isEqualTo(1);
    }

    @Test
    void getBoxedAsPrimitive() throws ReflectiveOperationException {
        ReflectField<Integer> field = ReflectField.lookup(int.class, ClassWithFields.class, "boxedInt");

        ClassWithFields instance = new ClassWithFields();
        instance.boxedInt = 1;
        
        assertThat(field.get(instance)).isEqualTo(1);
    }

    @Test
    void getWrongType() {
        ReflectField<Boolean> field = ReflectField.lookup(Boolean.class, ClassWithFields.class, "string");

        assertThatExceptionOfType(TypeNotCompatibleException.class).isThrownBy(() -> {
            field.get(new ClassWithFields());
        });
    }

    @Test
    void setWrongType() {
        ReflectField<Boolean> field = ReflectField.lookup(Boolean.class, ClassWithFields.class, "string");

        assertThatExceptionOfType(TypeNotCompatibleException.class).isThrownBy(() -> {
            field.set(new ClassWithFields(), true);
        });
    }

    @Test
    void getMismatchingDeclarationType() throws ReflectiveOperationException {
        ReflectField<Boolean> field = ReflectField.lookup(Boolean.class, ClassWithFields.class, "string");
        
        field.getDeclarationType(); // No exception here
    }

    @Test
    void getCheckedMismatchingDeclarationType() {
        ReflectField<Boolean> field = ReflectField.lookup(Boolean.class, ClassWithFields.class, "string");

        assertThatExceptionOfType(TypeNotCompatibleException.class).isThrownBy(() -> {
            field.getCheckedDeclarationType();
        });
    }
    
    
    private static class ClassWithFields {

        private Object privateObject;
        private static Object staticObject;
        public int primitiveInt;
        public Integer boxedInt;
        public String string;
        public Number number;
        
    }

}
