/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons;

import me.filoghost.fcommons.reflection.ReflectionUtils;
import org.assertj.core.api.AbstractStringAssert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class ColorsTest {

    @BeforeAll
    static void beforeAll() throws ReflectiveOperationException {
        ReflectionUtils.setFinalFieldValue(
                FeatureSupport.class.getDeclaredField("HEX_CHAT_COLORS"),
                null,
                true);
    }

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
    void hexColors() {
        String hexColor = "&#09af00";
        String hexColorOutput = "§x§0§9§a§f§0§0";
        testStringPositions(hexColor).forEach(input -> {
            assertThatColored(input).isEqualTo(input.replace(hexColor, hexColorOutput));
        });
    }

    @Test
    void uppercaseHexColor() {
        String input = "&#00FF00";
        String output = "§x§0§0§f§f§0§0";
        assertThatColored(input).isEqualTo(output);
    }

    @Test
    void longHexColor() {
        String input = "&#1234567890";
        String output = "§x§1§2§3§4§5§67890";
        assertThatColored(input).isEqualTo(output);
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

}