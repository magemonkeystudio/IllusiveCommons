/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.mapped;

import java.util.Collections;
import java.util.List;

public interface MappedConfig extends MappedConfigSection {

    default List<String> getHeader() {
        return Collections.emptyList();
    }

}
