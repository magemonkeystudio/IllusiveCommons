/*
 * Copyright (c) 2020, Wild Adventure
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 * 4. Redistribution of this software in source or binary forms shall be free
 *    of all charges or fees to the recipient of this software.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
