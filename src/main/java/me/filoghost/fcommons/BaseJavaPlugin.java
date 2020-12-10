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
import java.util.Arrays;
import java.util.List;

public abstract class BaseJavaPlugin extends JavaPlugin {

    @Override
    public final void onEnable() {
        try {
            FCommons.setPluginInstance(this);
            checkPackageRelocation();
            onCheckedEnable();
        } catch (PluginEnableException e) {
            criticalShutdown(e.getMessageLines(), e.getCause());
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


    private void criticalShutdown(List<String> errorMessageLines, Throwable throwable) {
        printCriticalError(errorMessageLines, throwable);

        Bukkit.getScheduler().runTaskLater(this, () -> {
            Bukkit.getConsoleSender().sendMessage(getFatalErrorPrefix()
                    + "Fatal error while enabling the plugin. Check previous logs for more information.");
        }, 10);

        setEnabled(false);
    }

    protected void printCriticalError(List<String> errorMessageLines, Throwable throwable) {
        List<String> output = new ArrayList<>();

        if (errorMessageLines != null) {
            output.add(getFatalErrorPrefix() + "Fatal error while enabling plugin:");
        } else {
            output.add(getFatalErrorPrefix() + "Fatal unexpected error while enabling plugin:");
        }
        if (errorMessageLines != null) {
            output.add("");
            output.addAll(errorMessageLines);
        }
        if (throwable != null) {
            output.add("");
            output.addAll(CommonsUtil.getStackTraceOutputLines(throwable));
            output.add("");
        }
        output.add(getDescription().getName() + " has been disabled.");
        output.add("");

        Bukkit.getConsoleSender().sendMessage(String.join("\n", output));
    }

    private String getFatalErrorPrefix() {
        return ChatColor.DARK_RED + "[" + getDescription().getName() + "] " + ChatColor.RED;
    }


    public static class PluginEnableException extends Exception {

        private final List<String> messageLines;

        public PluginEnableException(String... message) {
            this(null, message);
        }

        public PluginEnableException(Throwable cause, String... message) {
            super(String.join(" ", message), cause);
            this.messageLines = Arrays.asList(message);
        }

        public List<String> getMessageLines() {
            return messageLines;
        }

    }
}
