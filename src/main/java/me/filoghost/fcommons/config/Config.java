/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config;

import me.filoghost.fcommons.Preconditions;
import org.bukkit.configuration.file.YamlConfiguration;

import java.nio.file.Path;

public class Config extends ConfigSection {

	private final YamlConfiguration yaml;
	private final Path sourceFile;

	public Config(Path sourceFile) {
		this(new YamlConfiguration(), sourceFile);
	}

	public Config(YamlConfiguration yaml, Path sourceFile) {
		super(yaml);
		Preconditions.notNull(sourceFile, "sourceFile");
		this.yaml = yaml;
		this.sourceFile = sourceFile;
	}

	public Path getSourceFile() {
		return sourceFile;
	}

	public String saveToString() {
		return yaml.saveToString();
	}

	public void setHeader(String value) {
		yaml.options().header(value);
	}

}
