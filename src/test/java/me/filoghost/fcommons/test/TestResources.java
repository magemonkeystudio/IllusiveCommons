/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.test;

import java.nio.file.Path;
import java.nio.file.Paths;

public class TestResources {

    public static final Path FOLDER = Paths.get("src", "test", "resources");

    public static Path get(String fileName) {
        return FOLDER.resolve(fileName);
    }

    public static Path resolve(String path, String... pathMore) {
        return FOLDER.resolve(Paths.get(path, pathMore));
    }

}
