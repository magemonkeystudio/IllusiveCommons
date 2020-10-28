/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.command.multi;

import me.filoghost.fcommons.command.CommandException;

public interface SubCommand {

    void execute(SubCommandSession subCommandSession) throws CommandException;

    String getName();

    String getPermission();

    String getPermissionMessage();

    String getUsageArgs();

    int getMinArgs();

    int getDisplayPriority();

    String getDescription();

}
