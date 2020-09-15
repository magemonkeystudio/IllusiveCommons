package me.filoghost.fcommons.command.multi;

import me.filoghost.fcommons.command.CommandException;
import me.filoghost.fcommons.command.annotation.DisplayPriority;
import me.filoghost.fcommons.command.annotation.Label;
import me.filoghost.fcommons.command.annotation.MinArgs;
import me.filoghost.fcommons.command.annotation.Permission;
import me.filoghost.fcommons.command.annotation.PermissionMessage;
import me.filoghost.fcommons.command.annotation.UsageArgs;
import org.bukkit.command.CommandSender;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MethodReflectionSubCommand implements SubCommand {

	private final String label;
	private final Object instance;
	private final Method method;
	private String permission;
	private String permissionMessage;
	private String usageArgs;
	private int minArgs;
	private int displayPriority;

	protected MethodReflectionSubCommand(Object instance, Method method) {
		this.instance = instance;
		this.method = method;

		Label label = method.getAnnotation(Label.class);
		if (label == null) {
			throw new IllegalArgumentException("Missing @Label annotation");
		}

		this.label = label.value();
		method.setAccessible(true);

		Permission permission = method.getAnnotation(Permission.class);
		if (permission != null) {
			this.permission = permission.value();
		}

		PermissionMessage permissionMessage = method.getAnnotation(PermissionMessage.class);
		if (permissionMessage != null) {
			this.permissionMessage = permissionMessage.value();
		}

		UsageArgs usage = method.getAnnotation(UsageArgs.class);
		if (usage != null) {
			this.usageArgs = usage.value();
		}

		MinArgs minArgs = method.getAnnotation(MinArgs.class);
		if (minArgs != null) {
			this.minArgs = minArgs.value();
		}

		DisplayPriority displayPriority = method.getAnnotation(DisplayPriority.class);
		if (displayPriority != null) {
			this.displayPriority = displayPriority.value();
		}

		Class<?>[] params = method.getParameterTypes();
		if (params.length != 2 || params[0] != CommandSender.class || params[1] != String[].class) {
			throw new IllegalArgumentException("Parameters of method " + method.getName() + " must be 2: CommandSender, String[]");
		}
	}

	@Override
	public void execute(CommandSender sender, String[] args) throws CommandException {
		try {
			method.invoke(instance, sender, args);
		} catch (InvocationTargetException e) {
			if (e.getTargetException() instanceof CommandException) {
				throw (CommandException) e.getTargetException();
			} else {
				throw new RuntimeException(e.getTargetException());
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getLabel() {
		return label;
	}

	protected Method getMethod() {
		return method;
	}

	@Override
	public String getPermission() {
		return permission;
	}

	@Override
	public String getPermissionMessage() {
		return permissionMessage;
	}

	@Override
	public String getUsageArgs() {
		return usageArgs;
	}

	@Override
	public int getMinArgs() {
		return minArgs;
	}

	@Override
	public int getDisplayPriority() {
		return displayPriority;
	}

}
