/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.command.sub;

import me.filoghost.fcommons.command.CommandContext;
import me.filoghost.fcommons.command.CommandHelper;
import me.filoghost.fcommons.command.ConfigurableRootCommand;
import me.filoghost.fcommons.command.validation.CommandException;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permissible;

import java.util.ArrayList;
import java.util.List;

public abstract class SubCommandManager extends ConfigurableRootCommand {

    protected abstract SubCommand getSubCommandByName(String name);

    protected abstract Iterable<? extends SubCommand> getSubCommands();

    @Override
    public final void execute(CommandSender sender, String[] args, CommandContext context) throws CommandException {
        if (args.length == 0) {
            sendNoArgsMessage(context);
            return;
        }

        String     subLabel       = args[0];
        SubCommand subCommand     = getSubCommandByName(subLabel);
        String[]   subCommandArgs = CommandHelper.getArgsFromIndex(args, 1);
        SubCommandContext subContext = new SubCommandContext(
                context.getSender(),
                context.getRootLabel(),
                subCommandArgs,
                subCommand,
                subLabel);

        if (subCommand == null) {
            sendUnknownSubCommandMessage(subContext);
            return;
        }

        if (!subCommand.hasPermission(sender)) {
            if (subCommand.getPermissionMessage() != null) {
                sender.sendMessage(subCommand.getPermissionMessage());
            } else {
                sendSubCommandDefaultPermissionMessage(subContext);
            }
            return;
        }


        if (subCommandArgs.length < subCommand.getMinArgs()) {
            sendSubCommandUsage(subContext);
            return;
        }

        subCommand.execute(subContext.getSender(), subContext.getArgs(), subContext);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> subCommandNames = new ArrayList<>();
            for (SubCommand subCommand : getAccessibleSubCommands(sender)) {
                subCommandNames.add(subCommand.getName());
            }
            return CommandHelper.filterStartingWith(args[0], subCommandNames);
        }

        SubCommand subCommand = getSubCommandByName(args[0]);
        if (subCommand != null) {
            return subCommand.onTabComplete(sender, command, alias, CommandHelper.getArgsFromIndex(args, 1));
        }

        return new ArrayList<>();
    }

    protected Iterable<? extends SubCommand> getAccessibleSubCommands(Permissible sender) {
        List<SubCommand> list = new ArrayList<>();
        for (SubCommand subCommand : getSubCommands()) {
            if (subCommand.hasPermission(sender)) {
                list.add(subCommand);
            }
        }
        return list;
    }

    protected void sendSubCommandUsage(SubCommandContext context) {
        String usageText = getUsageText(context, context.getSubCommand());
        context.getSender().sendMessage(ChatColor.RED + "Command usage: " + usageText);
    }

    protected void sendSubCommandDefaultPermissionMessage(SubCommandContext context) {
        context.getSender().sendMessage(ChatColor.RED + "You don't have permission for this sub-command.");
    }

    protected void sendUnknownSubCommandMessage(SubCommandContext context) {
        context.getSender().sendMessage(ChatColor.RED + "Unknown sub-command \"" + context.getSubLabel() + "\"."
                + " Use /" + context.getRootLabel() + " to see available commands.");
    }

    protected void sendNoArgsMessage(CommandContext context) {
        CommandSender sender = context.getSender();
        sender.sendMessage(ChatColor.WHITE + "/" + context.getRootLabel() + " commands:");
        for (SubCommand subCommand : getAccessibleSubCommands(sender)) {
            sender.sendMessage(ChatColor.GRAY + getUsageText(context, subCommand));
        }
    }

    protected String getUsageText(CommandContext context, SubCommand subCommand) {
        String rootLabel = context.getRootLabel();
        String usageArgs = subCommand.getUsageArgs();
        return "/" + rootLabel + " " + subCommand.getName() + (usageArgs != null ? " " + usageArgs : "");
    }

}
