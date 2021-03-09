/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import me.filoghost.fcommons.Preconditions;
import me.filoghost.fcommons.Strings;

public class ConfigPath {
    
    private final ImmutableList<String> parts;
    
    private ConfigPath(ImmutableList<String> parts) {
        this.parts = parts;
    }

    public static ConfigPath dotDelimited(String delimitedPath) {
        return delimitedBy(delimitedPath, ".");
    }

    public static ConfigPath delimitedBy(String delimitedPath, String delimiter) {
        Preconditions.notEmpty(delimitedPath, "delimitedPath");
        return exact(Strings.split(delimitedPath, delimiter));
    }

    public static ConfigPath exact(String path) {
        Preconditions.notEmpty(path, "path");
        return new ConfigPath(ImmutableList.of(path));
    }
    
    public static ConfigPath exact(String... parts) {
        Preconditions.notEmpty(parts, "parts");
        for (String part : parts) {
            Preconditions.notEmpty(part, "parts element");
        }
        return new ConfigPath(ImmutableList.copyOf(parts));
    }

    public int getPartsLength() {
        return parts.size();
    }
    
    public String getPart(int index) {
        return parts.get(index);
    }

    public String getLastPart() {
        return parts.get(parts.size() - 1);
    }

    public ConfigPath replace(String target, String replacement) {
        Builder<String> pathPartsBuilder = ImmutableList.builder();
        for (String pathPart : parts) {
            pathPartsBuilder.add(pathPart.replace(target, replacement));
        }
        return new ConfigPath(pathPartsBuilder.build());
    }
    
    public String format(String delimiter) {
        return String.join(delimiter, parts);
    }

    @Override
    public String toString() {
        return format(" > ");
    }

}
