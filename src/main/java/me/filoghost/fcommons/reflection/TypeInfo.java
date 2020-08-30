package me.filoghost.fcommons.reflection;

import me.filoghost.fcommons.Preconditions;
import me.filoghost.fcommons.config.mapped.MappedConfigSection;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class TypeInfo {

	private final Class<?> typeClass;
	private final Type[] typeArguments;

	public TypeInfo(Class<?> typeClass, Type[] typeArguments) {
		this.typeClass = typeClass;
		this.typeArguments = typeArguments;
	}

	public Class<?> getTypeClass() {
		return typeClass;
	}

	@SuppressWarnings("unchecked")
	public <T> Class<T> getTypeClassAs(Class<T> clazz) {
		return (Class<T>) typeClass.asSubclass(clazz);
	}

	public Type[] getTypeArguments() {
		return typeArguments;
	}

	public static TypeInfo of(Field field) throws ReflectiveOperationException {
		Preconditions.notNull(field, "field");

		Type genericType;
		try {
			genericType = field.getGenericType();
		} catch (Throwable t) {
			throw new ReflectiveOperationException(t);
		}
		return of(genericType);
	}

	public static TypeInfo of(Type type) throws ReflectiveOperationException {
		Preconditions.notNull(type, "type");

		try {
			Type[] typeArguments = null;
			if (type instanceof ParameterizedType) {
				typeArguments = ((ParameterizedType) type).getActualTypeArguments();
			}

			Class<?> typeClass;
			if (type instanceof Class) {
				typeClass = (Class<?>) type;
			} else if (type instanceof ParameterizedType) {
				typeClass = (Class<?>) ((ParameterizedType) type).getRawType();
			} else {
				throw new ReflectiveOperationException("type is not a Class or ParameterizedType");
			}

			return new TypeInfo(typeClass, typeArguments);

		} catch (Throwable t) {
			throw new ReflectiveOperationException(t);
		}
	}

}
