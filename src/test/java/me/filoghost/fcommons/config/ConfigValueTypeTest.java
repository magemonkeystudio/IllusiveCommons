package me.filoghost.fcommons.config;

import me.filoghost.fcommons.config.exception.ConfigException;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class ConfigValueTypeTest {

	@org.junit.jupiter.api.Test
	void isValidConfigValue() throws ConfigException {
		ConfigLoader configLoader = new ConfigLoader(Paths.get("test", "resources"), Paths.get("test.yml"));
		Config config = configLoader.load();

		assertDoesNotThrow(() -> {
			config.get("nested-list").asRequired(ConfigValueType.LIST);
		});
	}
}