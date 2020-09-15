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
package me.filoghost.fcommons.command.multi;

import me.filoghost.fcommons.Preconditions;
import me.filoghost.fcommons.command.CommandException;
import me.filoghost.fcommons.command.CommandManager;
import me.filoghost.fcommons.command.annotation.Label;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permissible;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public abstract class MultiCommandManager extends CommandManager {
	
	private final Set<SubCommand> subCommands;

	public MultiCommandManager(String label) {
		super(label);
		this.subCommands = new TreeSet<>(Comparator
				.comparing(SubCommand::getDisplayPriority).reversed()
				.thenComparing(SubCommand::getLabel, String.CASE_INSENSITIVE_ORDER));

		scanSubCommands();
	}

	private void scanSubCommands() {
		for (Method method : getClass().getDeclaredMethods()) {
			if (method.isAnnotationPresent(Label.class)) {
				MethodReflectionSubCommand subCommand = new MethodReflectionSubCommand(this, method);
				registerSubCommand(subCommand);
			}
		}
	}

	protected void registerSubCommand(SubCommand subCommand) {
		Preconditions.notNull(subCommand, "subCommand");
		Preconditions.notNull(subCommand.getLabel(), "subCommand's label");
		subCommands.add(subCommand);
	}

	@Override
	public final void execute(CommandSender sender, String rootCommandLabel, String[] args) throws CommandException {
		if (args.length == 0) {
			sendNoArgsMessage(sender, rootCommandLabel);
			return;
		}
		
		String subCommandLabel = args[0];
		SubCommand subCommand = getSubCommand(subCommandLabel);

		if (subCommand == null) {
			sendUnknownSubCommandMessage(sender, rootCommandLabel);
			return;
		}

		if (subCommand.getPermission() != null && !sender.hasPermission(subCommand.getPermission())) {
			if (subCommand.getPermissionMessage() != null) {
				sender.sendMessage(subCommand.getPermissionMessage());
			} else {
				sendSubCommandDefaultPermissionMessage(sender);
			}
			return;
		}

		String[] subCommandArgs = Arrays.copyOfRange(args, 1, args.length);

		if (subCommandArgs.length < subCommand.getMinArgs()) {
			sendSubCommandUsage(sender, rootCommandLabel, subCommand);
			return;
		}

		subCommand.execute(sender, subCommandArgs);
	}

	private SubCommand getSubCommand(String label) {
		for (SubCommand subCommand : subCommands) {
			if (subCommand.getLabel().equalsIgnoreCase(label)) {
				return subCommand;
			}
		}
		return null;
	}

	protected final List<SubCommand> getAllSubCommands() {
		return new ArrayList<>(subCommands);
	}

	protected final List<SubCommand> getAccessibleSubCommands(Permissible sender) {
		List<SubCommand> list = new ArrayList<>();
		for (SubCommand subCommand : subCommands) {
			if (subCommand.getPermission() == null || sender.hasPermission(subCommand.getPermission())) {
				list.add(subCommand);
			}
		}
		return list;
	}

	protected void sendSubCommandUsage(CommandSender sender, String rootCommandLabel, SubCommand subCommand) {
		sender.sendMessage(ChatColor.RED + "Usage: " + getUsageText(rootCommandLabel, subCommand));
	}

	protected void sendSubCommandDefaultPermissionMessage(CommandSender sender) {
		sender.sendMessage(ChatColor.RED + "You don't have permission for this sub-command.");
	}

	protected void sendUnknownSubCommandMessage(CommandSender sender, String rootCommandLabel) {
		sender.sendMessage(ChatColor.RED + "Unknown subcommand. Use /" + rootCommandLabel + " to see available commands.");
	}

	protected void sendNoArgsMessage(CommandSender sender, String rootCommandLabel) {
		sendCommandListMessage(sender, rootCommandLabel);
	}

	protected void sendCommandListMessage(CommandSender sender, String rootCommandLabel) {
		for (SubCommand subCommand : getAllSubCommands()) {
			sender.sendMessage(ChatColor.GRAY + getUsageText(rootCommandLabel, subCommand));
		}
	}

	protected String getUsageText(String rootCommandLabel, SubCommand subCommand) {
		return "/" + rootCommandLabel + " " + subCommand.getLabel() + (subCommand.getUsageArgs() != null ? " " + subCommand.getUsageArgs() : "");
	}

}
