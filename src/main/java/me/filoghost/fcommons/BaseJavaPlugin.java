/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseJavaPlugin extends JavaPlugin {

    @Override
    public final void onEnable() {
        try {
            FCommons.setPluginInstance(this);
            checkPackageRelocation();
            onCheckedEnable();
        } catch (PluginEnableException e) {
            criticalShutdown(e.getMessage(), null);
        } catch (Throwable t) {
            criticalShutdown(null, t);
        }
    }

    private void checkPackageRelocation() {
        // Prevent Maven's Relocate from changing strings too
        final String defaultPackage = "me-filoghost-fcommons".replace("-", ".");

        // Make sure package has been relocated
        if (BaseJavaPlugin.class.getPackage().getName().equals(defaultPackage)) {
            throw new IllegalStateException("FCommons must be relocated to another package");
        }
    }

    protected abstract void onCheckedEnable() throws PluginEnableException;


    private void criticalShutdown(String errorMessage, Throwable throwable) {
        Bukkit.getConsoleSender().sendMessage(getErrorMessage(errorMessage, throwable));

        Bukkit.getScheduler().runTaskLater(this, () -> {
            Bukkit.getConsoleSender().sendMessage(getFatalErrorPrefix()
                    + "Fatal error while enabling the plugin. Check previous logs for more information.");
        }, 10);

        setEnabled(false);
    }

    protected String getErrorMessage(String errorMessage, Throwable throwable) {
        List<String> output = new ArrayList<>();

        if (throwable != null) {
            output.add(getFatalErrorPrefix() + "Fatal unexpected error while enabling plugin:");
        } else {
            output.add(getFatalErrorPrefix() + "Fatal error while enabling plugin:");
        }
        if (throwable != null) {
            output.add(" ");
            output.add(CommonsUtil.getStackTraceString(throwable));
        }
        output.add(" ");
        if (errorMessage != null) {
            output.add(errorMessage);
        }
        output.add("The plugin has been disabled.");
        output.add(" ");

        return String.join("\n", output);
    }

    private String getFatalErrorPrefix() {
        return ChatColor.DARK_RED + "[" + getDescription().getName() + "] " + ChatColor.RED;
    }


    public static class PluginEnableException extends Exception {

        public PluginEnableException(String message) {
            super(message);
        }

    }
}
