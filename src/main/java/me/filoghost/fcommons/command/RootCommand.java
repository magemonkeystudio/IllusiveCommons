/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.command;

import me.filoghost.fcommons.command.validation.CommandException;
import org.bukkit.command.CommandSender;

public interface RootCommand extends CommandProperties {
    
    void execute(CommandSender sender, String[] args, CommandContext commandContext) throws CommandException;

}
