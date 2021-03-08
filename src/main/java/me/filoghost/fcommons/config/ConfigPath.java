/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config;

import com.google.common.collect.ImmutableList;
import me.filoghost.fcommons.Preconditions;
import me.filoghost.fcommons.Strings;

public class ConfigPath {
    
    private final ImmutableList<String> pathParts;
    
    private ConfigPath(ImmutableList<String> pathParts) {
        this.pathParts = pathParts;
    }

    public static ConfigPath tokenDelimited(String path) {
        return tokenDelimited(path, ".");
    }

    public static ConfigPath tokenDelimited(String path, String token) {
        Preconditions.notEmpty(path, "path");
        return literal(Strings.split(path, token));
    }

    public static ConfigPath literal(String path) {
        Preconditions.notEmpty(path, "path");
        return new ConfigPath(ImmutableList.of(path));
    }
    
    public static ConfigPath literal(String... pathParts) {
        Preconditions.notEmpty(pathParts, "pathParts");
        for (String pathPart : pathParts) {
            Preconditions.notEmpty(pathPart, "pathParts element");
        }
        return new ConfigPath(ImmutableList.copyOf(pathParts));
    }

    public int getPartsLength() {
        return pathParts.size();
    }
    
    public String getPart(int index) {
        return pathParts.get(index);
    }

    public String getLastPart() {
        return pathParts.get(pathParts.size() - 1);
    }

}
