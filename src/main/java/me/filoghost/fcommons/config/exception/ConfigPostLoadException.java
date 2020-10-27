/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.exception;

public class ConfigPostLoadException extends ConfigLoadException {

    public ConfigPostLoadException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigPostLoadException(String message) {
        super(message);
    }

}
