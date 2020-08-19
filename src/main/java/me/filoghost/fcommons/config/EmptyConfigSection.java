/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config;

import org.bukkit.configuration.MemoryConfiguration;

public class EmptyConfigSection extends ConfigSection {

	public EmptyConfigSection() {
		super(new MemoryConfiguration());
	}

}
