package me.filoghost.fcommons.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

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

}
