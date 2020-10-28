/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.command.multi;

import me.filoghost.fcommons.Preconditions;
import me.filoghost.fcommons.Strings;
import me.filoghost.fcommons.command.CommandException;
import me.filoghost.fcommons.command.CommandManager;
import me.filoghost.fcommons.command.annotation.Name;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permissible;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public abstract class MultiCommandManager extends CommandManager {

    private final Set<SubCommand> subCommands;

    public MultiCommandManager(String label) {
        super(label);
        this.subCommands = new TreeSet<>(Comparator
                .comparing(SubCommand::getDisplayPriority).reversed()
                .thenComparing(SubCommand::getName, String.CASE_INSENSITIVE_ORDER));

        scanSubCommands();
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

    @Override
    public final void execute(CommandSender sender, String rootLabelUsed, String[] args) throws CommandException {
        if (args.length == 0) {
            sendNoArgsMessage(sender, rootLabelUsed);
            return;
        }

        String subLabelUsed = args[0];
        SubCommand subCommand = getSubCommand(subLabelUsed);
        String[] subCommandArgs = Arrays.copyOfRange(args, 1, args.length);
        SubCommandSession subCommandSession = new SubCommandSession(sender, subCommand, rootLabelUsed, subLabelUsed, subCommandArgs);

        if (subCommand == null) {
            sendUnknownSubCommandMessage(subCommandSession);
            return;
        }

        String permission = getSubCommandDefaultPermission(subCommand);
        if (!Strings.isEmpty(subCommand.getPermission())) {
            permission = subCommand.getPermission();
        }

        if (!Strings.isEmpty(permission) && !sender.hasPermission(permission)) {
            if (subCommand.getPermissionMessage() != null) {
                sender.sendMessage(subCommand.getPermissionMessage());
            } else {
                sendSubCommandDefaultPermissionMessage(subCommandSession);
            }
            return;
        }


        if (subCommandArgs.length < subCommand.getMinArgs()) {
            sendSubCommandUsage(subCommandSession);
            return;
        }

        subCommand.execute(subCommandSession);
    }

    private SubCommand getSubCommand(String label) {
        for (SubCommand subCommand : subCommands) {
            if (subCommand.getName().equalsIgnoreCase(label)) {
                return subCommand;
            }
        }
        return null;
    }

    protected final List<SubCommand> getAllSubCommands() {
        return new ArrayList<>(subCommands);
    }

    protected final List<SubCommand> getAccessibleSubCommands(Permissible sender) {
        List<SubCommand> list = new ArrayList<>();
        for (SubCommand subCommand : subCommands) {
            if (subCommand.getPermission() == null || sender.hasPermission(subCommand.getPermission())) {
                list.add(subCommand);
            }
        }
        return list;
    }

    protected String getSubCommandDefaultPermission(SubCommand subCommand) {
        return null;
    }

    protected void sendSubCommandUsage(SubCommandSession session) {
        String usageText = getUsageText(session.getRootLabelUsed(), session.getSubCommand());
        session.getSender().sendMessage(ChatColor.RED + "Command usage: " + usageText);
    }

    protected void sendSubCommandDefaultPermissionMessage(SubCommandSession subCommandSession) {
        subCommandSession.getSender().sendMessage(ChatColor.RED + "You don't have permission for this sub-command.");
    }

    protected void sendUnknownSubCommandMessage(SubCommandSession session) {
        session.getSender().sendMessage(ChatColor.RED + "Unknown sub-command \"" + session.getSubLabelUsed() + "\". "
                + "Use /" + session.getRootLabelUsed() + " to see available commands.");
    }

    protected void sendNoArgsMessage(CommandSender sender, String rootLabelUsed) {
        sender.sendMessage("/" + rootLabelUsed + " commands:");
        for (SubCommand subCommand : getAllSubCommands()) {
            sender.sendMessage(ChatColor.GRAY + getUsageText(rootLabelUsed, subCommand));
        }
    }

    protected final String getUsageText(String rootCommandName, SubCommand subCommand) {
        return getUsageText(rootCommandName, subCommand.getName(), subCommand.getUsageArgs());
    }

    protected String getUsageText(String rootCommandName, String subCommandName, String usageArgs) {
        return "/" + rootCommandName + " " + subCommandName + (usageArgs != null ? " " + usageArgs : "");
    }

}
