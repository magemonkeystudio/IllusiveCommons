/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.exception;

import org.bukkit.configuration.InvalidConfigurationException;

public class ConfigSyntaxException extends ConfigLoadException {

	private final String parsingErrorDetails;

	public ConfigSyntaxException(String message, InvalidConfigurationException cause) {
		super(message, cause);
		this.parsingErrorDetails = cause.getMessage();
	}

	public String getParsingErrorDetails() {
		return parsingErrorDetails;
	}

}
