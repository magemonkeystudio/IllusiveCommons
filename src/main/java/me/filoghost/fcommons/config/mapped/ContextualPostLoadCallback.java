/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.mapped;

import me.filoghost.fcommons.config.exception.ConfigPostLoadException;

public interface ContextualPostLoadCallback<C> {

    void postLoad(C context) throws ConfigPostLoadException;

}
