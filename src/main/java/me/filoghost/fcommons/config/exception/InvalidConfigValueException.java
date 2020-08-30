/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.exception;

public class InvalidConfigValueException extends ConfigValueException {

	public InvalidConfigValueException(String path, String message) {
		super(path, message);
	}

}
