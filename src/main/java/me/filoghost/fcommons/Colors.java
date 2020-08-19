/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons;

import org.bukkit.ChatColor;

public final class Colors {

	public static String addColors(String input) {
		if (Strings.isEmpty(input)) {
			return input;
		}
		return ChatColor.translateAlternateColorCodes('&', input);
	}

}
