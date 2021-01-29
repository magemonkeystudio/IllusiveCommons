/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.command;

import me.filoghost.fcommons.command.annotation.Permission;
import me.filoghost.fcommons.command.annotation.PermissionMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public abstract class CommandManager implements CommandExecutor {
    
    public boolean register(JavaPlugin plugin, String label) {
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
                sendCommandExceptionMessage(sender, ex.getMessage());
            } else {
                sendCommandExceptionMessage(sender, "Error while executing command.");
            }
        } catch (Exception ex) {
            handleUnexpectedException(sender, label, ex);
        }
        return true;
    }

    protected void handleUnexpectedException(CommandSender sender, String label, Exception ex) {
        Bukkit.getLogger().log(Level.SEVERE, "Unhandled exception while executing /" + label, ex);
        sender.sendMessage(ChatColor.RED + "Internal error while executing command.");
    }

    public abstract void execute(CommandSender sender, String label, String[] args) throws CommandException;

    protected void sendCommandExceptionMessage(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.RED + message);
    }

    protected String getDefaultPermissionMessage() {
        return ChatColor.RED + "You don't have permission for this command.";
    }

}
