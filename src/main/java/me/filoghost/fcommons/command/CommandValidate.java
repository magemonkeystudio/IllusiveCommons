/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.command;

public class CommandValidate {

	public static void notNull(Object o, String msg) {
		if (o == null) {
			throw new CommandException(msg);
		}
	}

	public static void isTrue(boolean b, String msg) {
		if (!b) {
			throw new CommandException(msg);
		}
	}

	public static void minLength(Object[] array, int minLength, String msg) {
		if (array.length < minLength) {
			throw new CommandException(msg);
		}
	}

}