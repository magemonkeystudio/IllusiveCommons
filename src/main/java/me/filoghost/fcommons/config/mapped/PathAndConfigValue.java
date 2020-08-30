/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */

package me.filoghost.fcommons.config.mapped;

import me.filoghost.fcommons.config.ConfigValue;

public class PathAndConfigValue {

    private final String path;
    private final ConfigValue value;

    public PathAndConfigValue(String path, ConfigValue value) {
        this.path = path;
        this.value = value;
    }

    public String getPath() {
        return path;
    }

    public ConfigValue getConfigValue() {
        return value;
    }

}
