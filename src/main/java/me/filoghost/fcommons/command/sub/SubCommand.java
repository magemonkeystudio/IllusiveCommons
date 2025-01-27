/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.command.sub;

import me.filoghost.fcommons.command.CommandProperties;
import me.filoghost.fcommons.command.validation.CommandException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public interface SubCommand extends CommandProperties {

    void execute(CommandSender sender, String[] args, SubCommandContext context) throws CommandException;

    List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args);

}
