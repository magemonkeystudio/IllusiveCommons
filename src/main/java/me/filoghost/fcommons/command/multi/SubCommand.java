package me.filoghost.fcommons.command.multi;

import me.filoghost.fcommons.command.CommandException;
import org.bukkit.command.CommandSender;

public interface SubCommand {

	void execute(CommandSender sender, String[] args) throws CommandException;

	String getLabel();

	String getPermission();

	String getPermissionMessage();

	String getUsageArgs();

	int getMinArgs();

	int getDisplayPriority();

}
