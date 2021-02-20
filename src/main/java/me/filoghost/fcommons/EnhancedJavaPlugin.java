/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class EnhancedJavaPlugin extends JavaPlugin {
    
    public final void registerListener(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, this);
    }

}
