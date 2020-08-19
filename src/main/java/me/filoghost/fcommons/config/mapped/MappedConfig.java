/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.mapped;

import me.filoghost.fcommons.config.exception.ConfigLoadException;

public class MappedConfig {

	private String header;

	protected void setHeader(String... header) {
		this.header = String.join("\n", header) + "\n";
	}

	public String getHeader() {
		return header;
	}

	public void postLoad() throws ConfigLoadException {}

}
