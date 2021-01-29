/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.command.sub;

import me.filoghost.fcommons.Strings;
import me.filoghost.fcommons.command.CommandException;
import me.filoghost.fcommons.command.CommandManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permissible;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class SubCommandManager extends CommandManager {
    
    @Override
    public final void execute(CommandSender sender, String rootLabel, String[] args) throws CommandException {
        if (args.length == 0) {
            sendNoArgsMessage(sender, rootLabel);
            return;
        }

        String subLabel = args[0];
        SubCommand subCommand = getSubCommandByName(subLabel);
        String[] subCommandArgs = Arrays.copyOfRange(args, 1, args.length);
        SubCommandExecution subCommandExecution = new SubCommandExecution(sender, subCommand, rootLabel, subLabel, subCommandArgs);

        if (subCommand == null) {
            sendUnknownSubCommandMessage(subCommandExecution);
            return;
        }

        String permission;
        if (!Strings.isEmpty(subCommand.getPermission())) {
            permission = subCommand.getPermission();
        } else {
            permission = getDefaultSubCommandPermission(subCommand);
        }

        if (!Strings.isEmpty(permission) && !sender.hasPermission(permission)) {
            if (subCommand.getPermissionMessage() != null) {
                sender.sendMessage(subCommand.getPermissionMessage());
            } else {
                sendSubCommandDefaultPermissionMessage(subCommandExecution);
            }
            return;
        }


        if (subCommandArgs.length < subCommand.getMinArgs()) {
            sendSubCommandUsage(subCommandExecution);
            return;
        }

        subCommand.execute(subCommandExecution);
    }

    protected final Iterable<? extends SubCommand> getAccessibleSubCommands(Permissible sender) {
        List<SubCommand> list = new ArrayList<>();
        for (SubCommand subCommand : getSubCommands()) {
            if (subCommand.getPermission() == null || sender.hasPermission(subCommand.getPermission())) {
                list.add(subCommand);
            }
        }
        return list;
    }

    protected abstract SubCommand getSubCommandByName(String name);

    protected abstract Iterable<? extends SubCommand> getSubCommands();

    protected String getDefaultSubCommandPermission(SubCommand subCommand) {
        return null;
    }

    protected void sendSubCommandUsage(SubCommandExecution execution) {
        String usageText = getUsageText(execution.getRootLabel(), execution.getSubCommand());
        execution.getSender().sendMessage(ChatColor.RED + "Command usage: " + usageText);
    }

    protected void sendSubCommandDefaultPermissionMessage(SubCommandExecution subCommandExecution) {
        subCommandExecution.getSender().sendMessage(ChatColor.RED + "You don't have permission for this sub-command.");
    }

    protected void sendUnknownSubCommandMessage(SubCommandExecution execution) {
        execution.getSender().sendMessage(ChatColor.RED + "Unknown sub-command \"" + execution.getSubLabel() 
                + "\". Use /" + execution.getRootLabel() + " to see available commands.");
    }

    protected void sendNoArgsMessage(CommandSender sender, String rootLabel) {
        sender.sendMessage("/" + rootLabel + " commands:");
        for (SubCommand subCommand : getSubCommands()) {
            sender.sendMessage(ChatColor.GRAY + getUsageText(rootLabel, subCommand));
        }
    }

    protected final String getUsageText(String rootCommandName, SubCommand subCommand) {
        return getUsageText(rootCommandName, subCommand.getName(), subCommand.getUsageArgs());
    }

    protected String getUsageText(String rootCommandName, String subCommandName, String usageArgs) {
        return "/" + rootCommandName + " " + subCommandName + (usageArgs != null ? " " + usageArgs : "");
    }

}
