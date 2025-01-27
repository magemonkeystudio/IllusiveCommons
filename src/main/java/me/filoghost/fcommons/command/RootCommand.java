/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.command;

import me.filoghost.fcommons.command.validation.CommandException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public interface RootCommand extends CommandProperties {

    void execute(CommandSender sender, String[] args, CommandContext commandContext) throws CommandException;

    List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args);

}
