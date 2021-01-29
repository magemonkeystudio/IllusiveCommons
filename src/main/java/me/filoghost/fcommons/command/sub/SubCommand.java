/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.command.sub;

import me.filoghost.fcommons.command.CommandException;

public interface SubCommand {

    void execute(SubCommandExecution subCommandExecution) throws CommandException;

    String getName();

    String getPermission();

    String getPermissionMessage();

    String getUsageArgs();

    int getMinArgs();

    int getDisplayPriority();

    String getDescription();

}
