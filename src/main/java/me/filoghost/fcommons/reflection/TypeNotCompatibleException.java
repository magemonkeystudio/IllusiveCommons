/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.reflection;

public class TypeNotCompatibleException extends ReflectiveOperationException {

    public TypeNotCompatibleException(String message) {
        super(message);
    }

}
