/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.collection;

class CaseInsensitiveHashMapTest extends CaseInsensitiveMapTest {

    @Override
    protected CaseInsensitiveMap<Integer> createCaseInsensitiveMap() {
        return new CaseInsensitiveHashMap<>();
    }

}
