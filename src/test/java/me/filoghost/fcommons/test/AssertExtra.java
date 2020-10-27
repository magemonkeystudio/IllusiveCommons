package me.filoghost.fcommons.test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

public class AssertExtra {

    public static void fileContentMatches(Path file, String... contents) throws IOException {
        assertThat(file).exists();
        assertThat(Files.readAllLines(file)).containsExactly(contents);
    }

}
