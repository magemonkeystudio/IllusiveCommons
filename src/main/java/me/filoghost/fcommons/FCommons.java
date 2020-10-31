package me.filoghost.fcommons;

import org.bukkit.plugin.Plugin;

public class FCommons {

    private static Plugin pluginInstance;

    public static void setPluginInstance(Plugin pluginInstance) {
        FCommons.pluginInstance = pluginInstance;
    }

    public static Plugin getPluginInstance() {
        if (pluginInstance == null) {
            throw new IllegalStateException("no plugin instance has been set");
        }
        return pluginInstance;
    }

}
