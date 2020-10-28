/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.command.multi;

import org.bukkit.command.CommandSender;

public class SubCommandSession {

    private final CommandSender sender;
    private final SubCommand subCommand;
    private final String rootLabelUsed;
    private final String subLabelUsed;
    private final String[] args;

    public SubCommandSession(CommandSender sender, SubCommand subCommand, String rootLabelUsed, String subLabelUsed, String[] args) {
        this.sender = sender;
        this.subCommand = subCommand;
        this.rootLabelUsed = rootLabelUsed;
        this.subLabelUsed = subLabelUsed;
        this.args = args;
    }

    public CommandSender getSender() {
        return sender;
    }

    public SubCommand getSubCommand() {
        return subCommand;
    }

    public String getRootLabelUsed() {
        return rootLabelUsed;
    }

    public String getSubLabelUsed() {
        return subLabelUsed;
    }

    public String[] getArgs() {
        return args;
    }

}
