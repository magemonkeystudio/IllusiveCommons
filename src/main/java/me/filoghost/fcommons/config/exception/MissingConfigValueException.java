/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.exception;

public class MissingConfigValueException extends ConfigValueException {

    public MissingConfigValueException(String path, String message) {
        super(path, message);
    }

}
