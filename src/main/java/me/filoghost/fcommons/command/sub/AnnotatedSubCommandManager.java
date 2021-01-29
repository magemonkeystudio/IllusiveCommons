/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.command.sub;

import me.filoghost.fcommons.Preconditions;
import me.filoghost.fcommons.command.annotation.Name;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.TreeSet;

public class AnnotatedSubCommandManager extends SubCommandManager {

    private final TreeSet<SubCommand> subCommands;

    public AnnotatedSubCommandManager() {
        this.subCommands = new TreeSet<>(Comparator
                .comparing(SubCommand::getDisplayPriority).reversed()
                .thenComparing(SubCommand::getName, String.CASE_INSENSITIVE_ORDER));
        scanSubCommands();
    }

    @Override
    protected final SubCommand getSubCommandByName(String name) {
        for (SubCommand subCommand : subCommands) {
            if (subCommand.getName().equalsIgnoreCase(name)) {
                return subCommand;
            }
        }
        return null;
    }

    @Override
    protected final Iterable<SubCommand> getSubCommands() {
        return subCommands;
    }

    private void scanSubCommands() {
        for (Method method : getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Name.class)) {
                MethodReflectionSubCommand subCommand = new MethodReflectionSubCommand(this, method);
                registerSubCommand(subCommand);
            }
        }
    }

    protected final void registerSubCommand(SubCommand subCommand) {
        Preconditions.notNull(subCommand, "subCommand");
        Preconditions.notNull(subCommand.getName(), "subCommand's name");
        subCommands.add(subCommand);
    }

}
