/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.mapped;

import me.filoghost.fcommons.config.Config;

import java.util.Collections;
import java.util.List;

public interface MappedConfig extends MappedConfigSection {

    default List<String> getHeader() {
        return Collections.emptyList();
    }

    /**
     * Applies changes to the raw config before loading.
     *
     * @return true if the applied changes should trigger a file save, false otherwise
     */
    default boolean beforeLoad(Config rawConfig) {
        return false;
    }

    /**
     * Applies changes to the raw config before saving.
     *
     * @return true if the applied changes should trigger a file save, false otherwise
     */
    default boolean beforeSave(Config rawConfig) {
        return false;
    }

}
