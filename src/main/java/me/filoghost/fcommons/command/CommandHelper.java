/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.command;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class CommandHelper {

    public static String[] getArgsFromIndex(String[] args, int fromIndex) {
        return Arrays.copyOfRange(args, fromIndex, args.length);
    }

    public static String joinArgsFromIndex(String[] args, int fromIndex) {
        return String.join(" ", getArgsFromIndex(args, fromIndex));
    }

    public static List<String> filterStartingWith(@NotNull String prefix, List<String> values) {
        prefix = prefix.toLowerCase(Locale.US);
        List<String> filtered = new ArrayList<>();
        for (String value : values) {
            if (value.toLowerCase(Locale.US).startsWith(prefix)) {
                filtered.add(value);
            }
        }
        return filtered;
    }

    public static List<String> filterStartingWith(@NotNull String prefix, String... values) {
        return filterStartingWith(prefix, Arrays.asList(values));
    }

}
