/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons;

import org.assertj.core.api.AbstractStringAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class ColorsTest {

    @Test
    void nullString() {
        assertThatColored(null).isNull();
    }

    @Test
    void emptyString() {
        assertThatColored("").isEqualTo("");
    }

    @Test
    void color() {
        testStringPositions("&a").forEach(input -> {
            assertThatColored(input).isEqualTo(input.replace("&", "§"));
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"&0", "&9", "&a", "&f", "&k", "&l", "&m", "&o", "&r"})
    void colorsAndFormatting(String input) {
        assertThatColored(input).isEqualTo(input.replace("&", "§"));
    }

    @Test
    void uppercaseColor() {
        assertThatColored("&A").isEqualTo("§a");
    }

    @Test
    void invalidColorChar() {
        assertThatColored("&z").isEqualTo("&z");
    }

    @Test
    void incompleteColor() {
        assertThatColored("&").isEqualTo("&");
    }

    @Test
    void hexColor() {
        String hexColor = "&#09af00";
        String hexColorOutput = "§x§0§9§a§f§0§0";
        testStringPositions(hexColor).forEach(input -> {
            assertThatColored(input).isEqualTo(input.replace(hexColor, hexColorOutput));
        });
    }

    @Test
    void uppercaseHexColor() {
        assertThatColored("&#00FF00").isEqualTo("§x§0§0§f§f§0§0");
    }

    @Test
    void longHexColor() {
        assertThatColored("&#1234567890").isEqualTo("§x§1§2§3§4§5§67890");
    }

    @ParameterizedTest
    @ValueSource(strings = {"&#12345", "&#123#456", "&#bcdefg", "&z#123456", "#123456", "&##123456"})
    void invalidHexColors(String input) {
        testStringPositions(input).forEach(s -> {
            assertThatColored(s).isEqualTo(s);
        });
    }

    private Stream<String> testStringPositions(String colorString) {
        return Stream.of(
                colorString,
                colorString + colorString,
                "z" + colorString,
                "z" + colorString + "z",
                colorString + "z"
        );
    }

    private AbstractStringAssert<?> assertThatColored(String actual) {
        return assertThat(Colors.addColors(actual));
    }

    @ParameterizedTest
    @MethodSource("trimTransparentWhitespaceArguments")
    void trimTransparentWhitespace(String input, String expectedOutput) {
        assertThat(Colors.trimTransparentWhitespace(input)).isEqualTo(expectedOutput);
    }

    static Stream<Arguments> trimTransparentWhitespaceArguments() {
        return Stream.of(
                Arguments.of("__", "__"),
                Arguments.of(" _ _ ", "_ _"),
                Arguments.of(" § § ", "§§"),
                Arguments.of(" §r §r ", "§r§r"),
                Arguments.of("_§r §r_", "_§r §r_"),
                Arguments.of(" §r _ §r ", "§r_§r"),
                Arguments.of(" §n _ §r ", "§n _ §r"),
                Arguments.of(" §n§r _ §n§r ", "§n§r_§n§r"),
                Arguments.of("§n§a _", "§n§a_") // Colors partially behave like "reset": they remove formats
        );
    }

}
