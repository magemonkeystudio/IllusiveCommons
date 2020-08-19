/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.command;

public class CommandException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CommandException(String msg) {
		super(msg);
	}

}