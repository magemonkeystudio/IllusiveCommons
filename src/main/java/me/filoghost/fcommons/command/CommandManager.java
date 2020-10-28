/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.command;

import me.filoghost.fcommons.command.annotation.Permission;
import me.filoghost.fcommons.command.annotation.PermissionMessage;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Wrapper for the default command executor.
 */
public abstract class CommandManager implements CommandExecutor {

    private final String label;

    public CommandManager(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public boolean register(JavaPlugin plugin) {
        PluginCommand pluginCommand = plugin.getCommand(label);
        if (pluginCommand == null) {
            return false;
        }

        Permission permission = getClass().getAnnotation(Permission.class);
        if (permission != null) {
            pluginCommand.setPermission(permission.value());
        }

        PermissionMessage noPermissionMessage = getClass().getAnnotation(PermissionMessage.class);
        if (noPermissionMessage != null) {
            pluginCommand.setPermissionMessage(noPermissionMessage.value());
        } else {
            pluginCommand.setPermissionMessage(getDefaultPermissionMessage());
        }

        pluginCommand.setExecutor(this);
        return true;
    }

    @Override
    public final boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            execute(sender, label, args);
        } catch (CommandException ex) {
            if (ex.getMessage() != null && !ex.getMessage().isEmpty()) {
                sender.sendMessage(formatCommandExceptionMessage(ex.getMessage()));
            }
        }
        return true;
    }

    public abstract void execute(CommandSender sender, String label, String[] args) throws CommandException;

    protected String formatCommandExceptionMessage(String message) {
        return ChatColor.RED + message;
    }

    protected String getDefaultPermissionMessage() {
        return ChatColor.RED + "You don't have permission for this command.";
    }

}
