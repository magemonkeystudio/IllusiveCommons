/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class ColorsTest {

    @ParameterizedTest
    @MethodSource("colorizeArguments")
    void colorize(String input, String expectedOutput) {
        assertThat(Colors.colorize(input)).isEqualTo(expectedOutput);
    }

    static Stream<Arguments> colorizeArguments() {
        String validHexInput = "&#00ff00";
        String validHexOutput = "§x§0§0§f§f§0§0";

        return Stream.of(
                Arguments.of(null, null),
                Arguments.of("", ""),
                Arguments.of("&0&9&a&f&k&l&m&o&r", "§0§9§a§f§k§l§m§o§r"), // Standard colors
                Arguments.of("&A", "§a"), // Uppercase color
                Arguments.of("&z", "&z"), // Invalid color
                Arguments.of("&", "&"), // Incomplete color

                // Valid HEX colors
                Arguments.of(validHexInput + "7890", validHexOutput + "7890"), // Long HEX color, should not read too many chars
                Arguments.of(validHexInput.toUpperCase(), validHexOutput),
                Arguments.of(validHexInput + validHexInput, validHexOutput + validHexOutput),
                Arguments.of("&" + validHexInput + "&", "&" + validHexOutput + "&"),

                // Invalid HEX colors
                Arguments.of("&#12345", "&#12345"),
                Arguments.of("&#123#456", "&#123#456"),
                Arguments.of("&#bcdefg", "&#bcdefg"),
                Arguments.of("#123456", "#123456"),
                Arguments.of("&##123456", "&##123456")
        );
    }

    @ParameterizedTest
    @MethodSource("uncolorizeArguments")
    void uncolorize(String input, String expectedOutput) {
        assertThat(Colors.uncolorize(input)).isEqualTo(expectedOutput);
    }

    static Stream<Arguments> uncolorizeArguments() {
        String validHexInput = "§x§0§0§f§f§0§0";
        String validHexOutput = "&#00ff00";

        return Stream.of(
                Arguments.of(null, null),
                Arguments.of("", ""),
                Arguments.of("§0§9§a§f§k§l§m§o§r", "&0&9&a&f&k&l&m&o&r"), // Standard colors
                Arguments.of("§A", "§A"), // Invalid uppercase color
                Arguments.of("§z", "§z"), // Invalid non-existing color
                Arguments.of("§", "§"), // Incomplete color

                // Valid HEX colors
                Arguments.of(validHexInput + "§7§8§9§0", validHexOutput + "&7&8&9&0"), // Long HEX color, should not read too many chars
                Arguments.of(validHexInput + validHexInput, validHexOutput + validHexOutput),
                Arguments.of("&" + validHexInput + "&", "&" + validHexOutput + "&"),

                // Invalid HEX colors
                Arguments.of("§x§1§2§3§4§5§F", "§x&1&2&3&4&5§F"), // Invalid uppercase color
                Arguments.of("§x§1§2§3§4§5", "§x&1&2&3&4&5"), // Too short
                Arguments.of("§x§b§c§d§e§f§g", "§x&b&c&d&e&f§g"), // Invalid non-existing color
                Arguments.of("§x§1§2§3a§4§5§6", "§x&1&2&3a&4&5&6"), // Extra char between colors
                Arguments.of("x§1§2§3§4§5§6", "x&1&2&3&4&5&6"), // Wrong prefix
                Arguments.of("§xx§1§2§3§4§5§6", "§xx&1&2&3&4&5&6") // Wrong prefix
        );
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

    @ParameterizedTest
    @MethodSource("optimizeArguments")
    void optimize(String input, String expectedOutput) {
        assertThat(Colors.optimize(input)).isEqualTo(expectedOutput);
    }

    static Stream<Arguments> optimizeArguments() {
        return Stream.of(
                Arguments.of("§a_§a_", "§a__"),
                Arguments.of("§a_§b§l_§c_", "§a_§b§l_§c_"),
                Arguments.of("§a§l_§l§a_", "§a§l_§l§a_"),
                Arguments.of("§a§x§0_§a§x§0_", "§a§x§0__"),
                Arguments.of("_§a", "_§a"),
                Arguments.of("__", "__")
        );
    }

}
