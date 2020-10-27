package me.filoghost.fcommons.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ReflectionUtils {

    public static Object getFieldValue(Field field, Object instance) throws ReflectiveOperationException {
        try {
            field.setAccessible(true);
            return field.get(instance);
        } catch (ReflectiveOperationException e) {
            throw e;
        } catch (Throwable t) {
            throw new ReflectiveOperationException(t);
        }
    }

    public static void setFieldValue(Field field, Object instance, Object value) throws ReflectiveOperationException {
        try {
            field.setAccessible(true);
            field.set(instance, value);
        } catch (ReflectiveOperationException e) {
            throw e;
        } catch (Throwable t) {
            throw new ReflectiveOperationException(t);
        }
    }

    public static void setFinalFieldValue(Field field, Object instance, Object value) throws ReflectiveOperationException {
        try {
            field.setAccessible(true);

            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

            field.set(instance, value);
        } catch (ReflectiveOperationException e) {
            throw e;
        } catch (Throwable t) {
            throw new ReflectiveOperationException(t);
        }
    }

    public static boolean isClassLoaded(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    public static Field[] getDeclaredFields(Class<?> clazz) throws ReflectiveOperationException {
        try {
            return clazz.getDeclaredFields();
        } catch (Throwable t) {
            throw new ReflectiveOperationException(t);
        }
    }

    public static <T> T newInstance(Class<T> clazz) throws ReflectiveOperationException {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw e;
        } catch (Throwable t) {
            throw new ReflectiveOperationException(t);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<T> wrapPrimitiveClass(Class<T> clazz) {
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
