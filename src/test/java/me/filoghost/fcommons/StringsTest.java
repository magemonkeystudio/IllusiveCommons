/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class StringsTest {

    @Test
    void trim() {
        String[] input = {" a", " a ", "a "};
        assertThat(Strings.trim(input)).containsExactly("a", "a", "a");
    }

    @Test
    void trimPreserveInput() {
        String[] input = {" a "};
        Strings.trim(input);
        assertThat(input).containsExactly(" a ");
    }

    @Test
    void splitNoRegex() {
        assertThat(Strings.split("ab.cd", ".")).containsExactly("ab", "cd");
    }

    @Test
    void splitEmptyParts() {
        assertThat(Strings.split("/ab///cd/", "/")).containsExactly("", "ab", "", "", "cd", "");
    }

    @Test
    void doNotSplit() {
        assertThat(Strings.split("ab/cd/ef", "/", 1)).containsExactly("ab/cd/ef");
    }

    @Test
    void splitMissingDelimiter() {
        assertThat(Strings.split("abc", "/", 2)).containsExactly("abc");
    }

    @Test
    void splitLimit() {
        assertThat(Strings.split("ab/cd/ef", "/", 2)).containsExactly("ab", "cd/ef");
    }

    @Test
    void splitLimitExact() {
        assertThat(Strings.split("ab/cd/ef", "/", 3)).containsExactly("ab", "cd", "ef");
    }

    @Test
    void stripChars() {
        assertThat(Strings.stripChars("./ab/./cd/.", '.', '/')).isEqualTo("abcd");
    }

    @Test
    void capitalizeFully() {
        assertThat(Strings.capitalizeFully("hello world\nnew line")).isEqualTo("Hello World\nNew Line");
    }

    @Test
    void capitalizeFirst() {
        assertThat(Strings.capitalizeFirst("hello world")).isEqualTo("Hello world");
    }

}