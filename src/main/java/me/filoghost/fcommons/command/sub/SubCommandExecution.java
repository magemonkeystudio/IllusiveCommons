/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.command.sub;

import org.bukkit.command.CommandSender;

public class SubCommandExecution {

    private final CommandSender sender;
    private final SubCommand subCommand;
    private final String rootLabel;
    private final String subLabel;
    private final String[] args;

    public SubCommandExecution(CommandSender sender, SubCommand subCommand, String rootLabel, String subLabel, String[] args) {
        this.sender = sender;
        this.subCommand = subCommand;
        this.rootLabel = rootLabel;
        this.subLabel = subLabel;
        this.args = args;
    }

    public CommandSender getSender() {
        return sender;
    }

    public SubCommand getSubCommand() {
        return subCommand;
    }

    public String getRootLabel() {
        return rootLabel;
    }

    public String getSubLabel() {
        return subLabel;
    }

    public String[] getArgs() {
        return args;
    }

}
