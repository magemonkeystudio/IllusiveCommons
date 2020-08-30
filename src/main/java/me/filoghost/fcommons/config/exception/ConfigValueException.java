/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.exception;

public abstract class ConfigValueException extends ConfigException {

	private final String path;

	public ConfigValueException(String path, String message) {
		super(message);
		this.path = path;
	}

	public String getPath() {
		return path;
	}

}
