/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons;

import java.util.ArrayList;
import java.util.List;

public final class Strings {


    public static String[] trim(String... strings) {
        String[] result = new String[strings.length];
        for (int i = 0; i < strings.length; i++) {
            result[i] = strings[i].trim();
        }
        return result;
    }

    /**
     * Splits a string without using regular expressions and keeps empty trailing strings.
     */
    public static String[] split(String string, String delimiter) {
        return split(string, delimiter, 0);
    }

    /**
     * Splits a string without using regular expressions and keeps empty trailing strings.
     */
    public static String[] split(String string, String delimiter, int limit) {
        Preconditions.notNull(string, "string");
        Preconditions.checkArgument(!delimiter.isEmpty(), "delimiter cannot be empty");
        Preconditions.checkArgument(limit >= 0, "limit cannot be negative");

        if (string.isEmpty() || limit == 1) {
            // Optimization for trivial cases where no splits would occur
            return new String[]{string};
        }

        List<String> result = new ArrayList<>();
        int fromIndex = 0;
        int matchIndex;

        while ((matchIndex = string.indexOf(delimiter, fromIndex)) != -1) {
            if (limit > 0 && result.size() >= limit - 1) {
                // Limit reached (keep one slot for the remaining part)
                break;
            }

            result.add(string.substring(fromIndex, matchIndex));
            fromIndex = matchIndex + delimiter.length();
        }

        if (result.isEmpty()) {
            // No match found, return the full input string
            return new String[]{string};
        }

        // Add the remaining part of the string
        result.add(string.substring(fromIndex));

        return result.toArray(new String[0]);
    }

    public static String[] splitAndTrim(String string, String delimiter) {
        return splitAndTrim(string, delimiter, 0);
    }

    public static String[] splitAndTrim(String string, String delimiter, int limit) {
        return trim(split(string, delimiter, limit));
    }

    public static String stripChars(String string, char... charsToRemove) {
        if (isEmpty(string) || charsToRemove.length == 0) {
            return string;
        }

        StringBuilder result = new StringBuilder(string.length());

        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if (!arrayContains(charsToRemove, c)) {
                result.append(c);
            }
        }

        return result.toString();
    }

    private static boolean arrayContains(char[] array, char valueToFind) {
        for (char c : array) {
            if (c == valueToFind) {
                return true;
            }
        }

        return false;
    }

    public static String capitalizeFully(String string) {
        if (isEmpty(string)) {
            return string;
        }

        string = string.toLowerCase();
        int length = string.length();
        StringBuilder result = new StringBuilder(length);
        boolean capitalizeNext = true;

        for (int i = 0; i < length; i++) {
            char c = string.charAt(i);

            if (Character.isWhitespace(c)) {
                result.append(c);
                capitalizeNext = true;
            } else if (capitalizeNext) {
                result.append(Character.toTitleCase(c));
                capitalizeNext = false;
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    public static String capitalizeFirst(String string) {
        if (isEmpty(string)) {
            return string;
        }

        return Character.toTitleCase(string.charAt(0)) + string.substring(1);
    }

    public static boolean isEmpty(String string) {
        return string == null || string.isEmpty();
    }

    public static boolean isWhitespace(String string) {
        if (isEmpty(string)) {
            return true;
        }

        for (int i = 0; i < string.length(); i++) {
            if (!Character.isWhitespace(string.charAt(i))) {
                return false;
            }
        }

        return true;
    }
}
