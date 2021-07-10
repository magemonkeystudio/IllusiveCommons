/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons;

import org.bukkit.ChatColor;

public final class Colors {

    private static final int HEX_COLOR_LENGTH = 6;

    public static String addColors(String input) {
        if (Strings.isEmpty(input) || !input.contains("&")) {
            return input;
        }

        StringBuilder output = new StringBuilder(input.length());

        for (int i = 0; i < input.length(); i++) {
            char current = input.charAt(i);

            if (current == '&' && i + 1 < input.length()) {
                char next = input.charAt(i + 1);

                if (next == '#' && FeatureSupport.HEX_CHAT_COLORS && isValidHexColor(input, i + 2)) {
                    output.append(ChatColor.COLOR_CHAR);
                    output.append('x');

                    for (int j = 0; j < HEX_COLOR_LENGTH; j++) {
                        output.append(ChatColor.COLOR_CHAR);
                        output.append(Character.toLowerCase(input.charAt(i + 2 + j)));
                    }

                    i += 1 + HEX_COLOR_LENGTH; // Skip '#' and hex string, which are already converted and added

                } else if (isColorCode(next)) {
                    output.append(ChatColor.COLOR_CHAR);
                    output.append(Character.toLowerCase(next));

                    i++; // Skip next character, which is already added

                } else {
                    output.append(current);
                }
            } else {
                output.append(current);
            }
        }

        return output.toString();
    }

    private static boolean isValidHexColor(String input, int startIndex) {
        if (input.length() - startIndex < HEX_COLOR_LENGTH) {
            return false;
        }

        for (int i = 0; i < HEX_COLOR_LENGTH; i++) {
            if (!isHexCode(input.charAt(startIndex + i))) {
                return false;
            }
        }

        return true;
    }

    private static boolean isHexCode(char c) {
        return "0123456789AaBbCcDdEeFf".indexOf(c) > -1;
    }

    private static boolean isColorCode(char c) {
        return "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(c) > -1;
    }

    /**
     * Color-aware equivalent of {@link String#trim()}.
     * <p>
     * Removes leading and trailing transparent whitespace, ignoring colors and formats.
     * Does not remove whitespace made visible by {@link ChatColor#STRIKETHROUGH} and {@link ChatColor#UNDERLINE}.
     */
    public static String trimTransparentWhitespace(String string) {
        if (Strings.isEmpty(string)) {
            return string;
        }

        int firstVisibleChar = -1;
        int lastVisibleChar = -1;

        int length = string.length();
        boolean whitespaceVisible = false;

        for (int i = 0; i < length; i++) {
            char currentChar = string.charAt(i);

            if (currentChar == ' ' && !whitespaceVisible) {
                // Not visible, space if it's fully transparent
            } else if (currentChar == ChatColor.COLOR_CHAR) {
                // Not visible, COLOR_CHAR is not rendered

                if (i < length - 1) {
                    ChatColor chatColor = ChatColor.getByChar(string.charAt(i + 1));

                    if (chatColor != null) {
                        if (chatColor == ChatColor.STRIKETHROUGH || chatColor == ChatColor.UNDERLINE) {
                            // The above formats make whitespace visible
                            whitespaceVisible = true;
                        } else if (chatColor == ChatColor.RESET || chatColor.isColor()) {
                            // Colors and "reset" make whitespace transparent again
                            whitespaceVisible = false;
                        }
                    }

                    // Skip the next character because COLOR_CHAR prevents if from rendering, even if it's not a valid color or format
                    i++;
                }
            } else {
                // Visible character

                if (firstVisibleChar == -1) {
                    // Save the position of the first visible character once
                    firstVisibleChar = i;
                }
                lastVisibleChar = i + 1; // +1 because substring() ending index is not inclusive
            }
        }

        if (firstVisibleChar == -1) {
            // The whole string is whitespace, only keep colors and formats
            return string.replace(" ", "");
        }

        // Add the visible central part of the string, as is
        String result = string.substring(firstVisibleChar, lastVisibleChar);

        if (firstVisibleChar > 0) {
            // Add the transparent left part of the string, stripping whitespace
            String leftPart = string.substring(0, firstVisibleChar).replace(" ", "");
            if (!leftPart.isEmpty()) {
                result = leftPart + result;
            }
        }

        if (lastVisibleChar < string.length()) {
            // Add the transparent right part of the string, stripping whitespace
            String rightPart = string.substring(lastVisibleChar).replace(" ", "");
            if (!rightPart.isEmpty()) {
                result = result + rightPart;
            }
        }

        return result;
    }

    /**
     * Removes repeated combinations of color and formats from a string without changing how the string is rendered.
     */
    public static String optimize(String string) {
        if (Strings.isEmpty(string) || string.indexOf(ChatColor.COLOR_CHAR) == -1) {
            return string;
        }

        int length = string.length();
        StringBuilder result = new StringBuilder(length);
        StringBuilder previousColors = new StringBuilder();
        StringBuilder newColors = new StringBuilder();

        int i = 0;
        while (i < length) {
            char currentChar = string.charAt(i);

            if (currentChar == ChatColor.COLOR_CHAR && i < length - 1) {
                newColors.append(currentChar);
                newColors.append(string.charAt(i + 1));
                i += 2;
            } else {
                if (newColors.length() > 0) {
                    // Append new colors only if necessary
                    if (!contentEquals(newColors, previousColors)) {
                        result.append(newColors);
                    }

                    // Equivalent to, but more efficient:
                    // previousColors = newColors
                    // newColors = new StringBuilder()
                    previousColors.setLength(0);
                    previousColors.append(newColors);
                    newColors.setLength(0);
                }

                result.append(currentChar);
                i++;
            }
        }

        if (newColors.length() > 0) {
            result.append(newColors);
        }

        return result.toString();
    }

    private static boolean contentEquals(StringBuilder stringBuilder1, StringBuilder stringBuilder2) {
        if (stringBuilder1.length() != stringBuilder2.length()) {
            return false;
        }

        for (int i = 0; i < stringBuilder1.length(); i++) {
            if (stringBuilder1.charAt(i) != stringBuilder2.charAt(i)) {
                return false;
            }
        }

        return true;
    }

}
