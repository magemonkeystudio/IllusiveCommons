/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.command.multi;

import me.filoghost.fcommons.command.CommandException;
import me.filoghost.fcommons.command.annotation.Description;
import me.filoghost.fcommons.command.annotation.DisplayPriority;
import me.filoghost.fcommons.command.annotation.MinArgs;
import me.filoghost.fcommons.command.annotation.Name;
import me.filoghost.fcommons.command.annotation.Permission;
import me.filoghost.fcommons.command.annotation.PermissionMessage;
import me.filoghost.fcommons.command.annotation.UsageArgs;
import org.bukkit.command.CommandSender;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class MethodReflectionSubCommand extends SimpleSubCommand {

    private static final Map<Class<?>, Function<SubCommandSession, ?>> parameterProviders = new HashMap<>();
    static {
        parameterProviders.put(SubCommandSession.class, Function.identity());
        parameterProviders.put(CommandSender.class, SubCommandSession::getSender);
        parameterProviders.put(SubCommand.class, SubCommandSession::getSubCommand);
        parameterProviders.put(String[].class, SubCommandSession::getArgs);
    }

    private final Object instance;
    private final Method method;
    private final Class<?>[] methodParameterTypes;

    protected MethodReflectionSubCommand(Object instance, Method method) {
        super(getNameAnnotation(method).value());
        this.instance = instance;
        this.method = method;
        this.method.setAccessible(true);

        Class<?>[] params = method.getParameterTypes();
        for (Class<?> param : params) {
            if (!parameterProviders.containsKey(param)) {
                throw new IllegalArgumentException("Method " + method.getName()
                        + " contains unsupported parameter type: " + param.getSimpleName());
            }
        }
        this.methodParameterTypes = params;

        Permission permission = method.getAnnotation(Permission.class);
        if (permission != null) {
            setPermission(permission.value());
        }

        PermissionMessage permissionMessage = method.getAnnotation(PermissionMessage.class);
        if (permissionMessage != null) {
            setPermissionMessage(permissionMessage.value());
        }

        UsageArgs usage = method.getAnnotation(UsageArgs.class);
        if (usage != null) {
            setUsageArgs(usage.value());
        }

        MinArgs minArgs = method.getAnnotation(MinArgs.class);
        if (minArgs != null) {
            setMinArgs(minArgs.value());
        }

        DisplayPriority displayPriority = method.getAnnotation(DisplayPriority.class);
        if (displayPriority != null) {
            setDisplayPriority(displayPriority.value());
        }

        Description description = method.getAnnotation(Description.class);
        if (description != null) {
            setDescription(description.value());
        }
    }

    private static Name getNameAnnotation(Method method) {
        Name name = method.getAnnotation(Name.class);
        if (name == null) {
            throw new IllegalArgumentException("Missing @Name annotation");
        }
        return name;
    }

    @Override
    public void execute(SubCommandSession subCommandSession) throws CommandException {
        Object[] methodParameters = new Object[methodParameterTypes.length];
        for (int i = 0; i < methodParameters.length; i++) {
            methodParameters[i] = parameterProviders.get(methodParameterTypes[i]).apply(subCommandSession);
        }

        try {
            method.invoke(instance, methodParameters);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof CommandException) {
                throw (CommandException) e.getCause();
            } else {
                throw new RuntimeException(e.getCause());
            }
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

}
