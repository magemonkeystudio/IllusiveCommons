package me.filoghost.fcommons.config.mapped;

import me.filoghost.fcommons.config.ConfigErrors;
import me.filoghost.fcommons.config.exception.ConfigMappingException;
import me.filoghost.fcommons.reflection.ReflectionUtils;
import me.filoghost.fcommons.reflection.TypeInfo;

import java.lang.reflect.Type;

public class MappingUtils {

    public static <T> T createInstance(TypeInfo<T> typeInfo) throws ConfigMappingException {
        Class<T> clazz = typeInfo.getTypeClass();
        try {
            return ReflectionUtils.newInstance(clazz);
        } catch (NoSuchMethodException e) {
            throw new ConfigMappingException(ConfigErrors.noEmptyConstructor(clazz));
        } catch (ReflectiveOperationException e) {
            throw new ConfigMappingException(ConfigErrors.cannotCreateInstance(clazz), e);
        }
    }

    public static TypeInfo<?> getSingleGenericType(TypeInfo<?> typeInfo) throws ConfigMappingException {
        Type[] typeArguments = typeInfo.getTypeArguments();

        if (typeArguments == null || typeArguments.length == 0) {
            throw new ConfigMappingException("declaration omits generic type");
        }
        if (typeArguments.length != 1) {
            throw new ConfigMappingException("declaration has more than 1 generic type");
        }

        try {
            return TypeInfo.of(typeArguments[0]);
        } catch (ReflectiveOperationException e) {
            throw new ConfigMappingException("error while getting type info of " + typeArguments[0], e);
        }
    }

}
