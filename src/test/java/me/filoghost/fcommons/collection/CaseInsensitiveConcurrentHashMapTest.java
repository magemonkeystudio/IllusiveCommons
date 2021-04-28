/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.collection;

class CaseInsensitiveConcurrentHashMapTest extends CaseInsensitiveMapTest {

    @Override
    protected CaseInsensitiveMap<Integer> createCaseInsensitiveMap() {
        return new CaseInsensitiveConcurrentHashMap<>();
    }

}
