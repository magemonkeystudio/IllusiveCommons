/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */

package me.filoghost.fcommons.config;

import me.filoghost.fcommons.Preconditions;
import me.filoghost.fcommons.Strings;
import me.filoghost.fcommons.config.exception.ConfigLoadException;
import me.filoghost.fcommons.config.exception.ConfigSyntaxException;
import org.bukkit.configuration.file.YamlConstructor;
import org.bukkit.configuration.file.YamlRepresenter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.representer.Representer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class YamlSerializer {

	private static final String COMMENT_PREFIX = "#";
	private static final String BLANK_CONFIG = "{}\n";

	private final Yaml yaml;

	public YamlSerializer() {
		YamlConstructor bukkitYamlConstructor = new YamlConstructor();
		Representer bukkitYamlRepresenter = new YamlRepresenter();
		bukkitYamlRepresenter.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		DumperOptions yamlOptions = new DumperOptions();
		yamlOptions.setIndent(2);
		yamlOptions.setWidth(Integer.MAX_VALUE); // Avoid lines wrapping
		yamlOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		this.yaml = new Yaml(bukkitYamlConstructor, bukkitYamlRepresenter, yamlOptions);
	}

	public LinkedHashMap<String, Object> parseConfigValues(List<String> fileContents) throws ConfigLoadException {
		Preconditions.notNull(fileContents, "fileContents cannot be null");

		Map<?, ?> yamlValues = parseYamlMap(String.join("\n", fileContents));
		if (yamlValues != null) {
			return yamlMapToConfigValues(yamlValues);
		} else {
			return null;
		}
	}

	public String serializeConfigValues(LinkedHashMap<String, Object> configValues) {
		Map<String, Object> yamlMap = configValuesToYamlMap(configValues);
		return serializeYamlMap(yamlMap);
	}

	public List<String> parseHeader(List<String> fileContents) {
		List<String> headerLines = new ArrayList<>();
		boolean foundHeader = false;

		for (String line : fileContents) {
			if (line.startsWith(COMMENT_PREFIX)) {
				foundHeader = true;

				int headerCommentStart = COMMENT_PREFIX.length();
				if (line.length() > headerCommentStart && Character.isWhitespace(line.charAt(headerCommentStart))) {
					headerCommentStart++;
				}

				String lineContent = line.substring(headerCommentStart);
				headerLines.add(lineContent);
			} else {
				if (foundHeader || !Strings.isWhitespace(line)) {
					break;
				}
			}
		}

		return headerLines;
	}

	public String serializeHeader(List<String> header) {
		if (header != null && !header.isEmpty()) {
			return header.stream().map(s -> COMMENT_PREFIX + " " + s + "\n").collect(Collectors.joining()) + "\n";
		} else {
			return "";
		}
	}

	public LinkedHashMap<String, Object> yamlMapToConfigValues(Map<?, ?> yamlMap) {
		LinkedHashMap<String, Object> configValues = new LinkedHashMap<>();

		for (Map.Entry<?, ?> entry : yamlMap.entrySet()) {
			String key = entry.getKey().toString();
			Object value = entry.getValue();

			configValues.put(key, yamlValueToConfigValue(value));
		}

		return configValues;
	}

	private Object yamlValueToConfigValue(Object yamlValue) {
		if (yamlValue instanceof Map) {
			Map<?, ?> yamlMap = (Map<?, ?>) yamlValue;
			return new ConfigSection(yamlMapToConfigValues(yamlMap));

		} else if (yamlValue instanceof List) {
			List<?> yamlList = (List<?>) yamlValue;
			List<Object> configList = new ArrayList<>();

			for (Object element : yamlList) {
				configList.add(yamlValueToConfigValue(element));
			}

			return configList;

		} else {
			return yamlValue;
		}
	}

	public Map<String, Object> configValuesToYamlMap(Map<String, Object> configValues) {
		Map<String, Object> yamlMap = new LinkedHashMap<>();

		for (Map.Entry<String, ?> entry : configValues.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();

			yamlMap.put(key, configValueToYamlValue(value));
		}

		return yamlMap;
	}

	private Object configValueToYamlValue(Object configValue) {
		if (configValue instanceof ConfigSection) {
			ConfigSection configSection = (ConfigSection) configValue;
			return configValuesToYamlMap(configSection.getInternalValues());

		} else if (configValue instanceof List) {
			List<?> configList = (List<?>) configValue;
			List<Object> yamlList = new ArrayList<>();

			for (Object configElement : configList) {
				yamlList.add(configValueToYamlValue(configElement));
			}

			return yamlList;

		} else {
			return configValue;
		}
	}

	private Map<?, ?> parseYamlMap(String serializedYamlMap) throws ConfigSyntaxException {
		Object loadedObject;
		try {
			loadedObject = yaml.load(serializedYamlMap);
		} catch (YAMLException e) {
			throw new ConfigSyntaxException(ConfigErrors.invalidYamlSyntax, e);
		}

		if (loadedObject == null) {
			return null;
		} else if (loadedObject instanceof Map) {
			return (Map<?, ?>) loadedObject;
		} else {
			throw new ConfigSyntaxException(ConfigErrors.invalidYamlSyntax, "Top level is not a Map.");
		}
	}

	private String serializeYamlMap(Map<?, ?> yamlMap) {
		String serializedYamlMap = yaml.dump(yamlMap);
		if (serializedYamlMap.equals(BLANK_CONFIG)) {
			serializedYamlMap = "";
		}
		return serializedYamlMap;
	}

}
