/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.command;

import me.filoghost.fcommons.command.validation.CommandException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.logging.Level;

public abstract class ConfigurableRootCommand extends ConfigurableCommandProperties implements RootCommand {

    public final boolean register(JavaPlugin plugin) {
        super.validate();

        PluginCommand pluginCommand = plugin.getCommand(getName());
        if (pluginCommand == null) {
            return false;
        }

        if (getPermission() != null) {
            pluginCommand.setPermission(getPermission());
        }
        if (getPermissionMessage() != null) {
            pluginCommand.setPermissionMessage(getPermissionMessage());
        } else {
            pluginCommand.setPermissionMessage(ChatColor.RED + "You don't have permission for this command.");
        }

        BukkitCommandExecutorAdapter executor = new BukkitCommandExecutorAdapter(this);
        pluginCommand.setExecutor(executor);
        pluginCommand.setTabCompleter(executor);
        return true;
    }

    protected void handleUnexpectedException(CommandContext context, Throwable t) {
        Bukkit.getLogger().log(Level.SEVERE, "Internal error while executing /" + context.getRootLabel(), t);
        context.getSender().sendMessage(ChatColor.RED + "Internal error while executing command.");
    }

    protected void sendExecutionErrorMessage(CommandContext context, String errorMessage) {
        context.getSender().sendMessage(ChatColor.RED + errorMessage);
    }


    private static class BukkitCommandExecutorAdapter implements CommandExecutor, TabCompleter {

        private final ConfigurableRootCommand command;

        private BukkitCommandExecutorAdapter(ConfigurableRootCommand command) {
            this.command = command;
        }

        @Override
        public final boolean onCommand(CommandSender sender, Command bukkitCommand, String label, String[] args) {
            CommandContext context = new CommandContext(sender, label, args);

            try {
                command.execute(context.getSender(), context.getArgs(), context);
            } catch (CommandException ex) {
                command.sendExecutionErrorMessage(context, ex.getMessage());
            } catch (Throwable t) {
                command.handleUnexpectedException(context, t);
            }
            return true;
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
            return this.command.onTabComplete(sender, command, alias, args);
        }
    }
}
