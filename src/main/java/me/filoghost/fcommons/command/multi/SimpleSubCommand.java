package me.filoghost.fcommons.command.multi;

public abstract class SimpleSubCommand implements SubCommand {

	private final String name;
	private String permission;
	private String permissionMessage;
	private String usageArgs;
	private int minArgs;
	private int displayPriority;
	private String description;

	public SimpleSubCommand(String name) {
		this.name = name;
	}

	@Override
	public final String getName() {
		return name;
	}

	@Override
	public final String getPermission() {
		return permission;
	}

	public final void setPermission(String permission) {
		this.permission = permission;
	}

	@Override
	public final String getPermissionMessage() {
		return permissionMessage;
	}

	public final void setPermissionMessage(String permissionMessage) {
		this.permissionMessage = permissionMessage;
	}

	@Override
	public final String getUsageArgs() {
		return usageArgs;
	}

	public final void setUsageArgs(String usageArgs) {
		this.usageArgs = usageArgs;
	}

	@Override
	public final int getMinArgs() {
		return minArgs;
	}

	public final void setMinArgs(int minArgs) {
		this.minArgs = minArgs;
	}

	@Override
	public final int getDisplayPriority() {
		return displayPriority;
	}

	public final void setDisplayPriority(int displayPriority) {
		this.displayPriority = displayPriority;
	}

	@Override
	public final String getDescription() {
		return description;
	}

	public final void setDescription(String description) {
		this.description = description;
	}

}
