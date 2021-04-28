/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.collection;

public class CaseInsensitiveConcurrentHashSetTest extends CaseInsensitiveSetTest {

    @Override
    protected CaseInsensitiveSet createCaseInsensitiveSet() {
        return new CaseInsensitiveConcurrentHashSet();
    }

}
