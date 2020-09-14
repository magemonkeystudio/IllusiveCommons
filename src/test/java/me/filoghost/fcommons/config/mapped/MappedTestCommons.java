package me.filoghost.fcommons.config.mapped;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class MappedTestCommons {

	public static <T extends MappedConfig> MappedConfigLoader<T> newNonExistingConfig(Path tempDir, Class<T> mappedConfigClass) {
		Path configPath = tempDir.resolve("temp-config.yml");
		return new MappedConfigLoader<>(tempDir, configPath, mappedConfigClass);
	}

	public static <T extends MappedConfig> MappedConfigLoader<T> newExistingConfig(
			Path tempDir, Class<T> mappedConfigClass, String... contents) throws IOException {

		Path configPath = tempDir.resolve("temp-config.yml");
		if (contents != null && contents.length > 0) {
			Files.write(configPath, Arrays.asList(contents));
		} else {
			Files.createFile(configPath);
		}

		return new MappedConfigLoader<>(tempDir, configPath, mappedConfigClass);
	}

}
