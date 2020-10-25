/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */

package me.filoghost.fcommons;

import me.filoghost.fcommons.reflection.ReflectionUtils;

public class FeatureSupport {

    public static final boolean V1_8 = ReflectionUtils.isClassLoaded("org.bukkit.entity.Rabbit");
    public static final boolean V1_9 = ReflectionUtils.isClassLoaded("org.bukkit.entity.Shulker");
    public static final boolean V1_10 = ReflectionUtils.isClassLoaded("org.bukkit.entity.PolarBear");
    public static final boolean V1_11 = ReflectionUtils.isClassLoaded("org.bukkit.entity.Llama");
    public static final boolean V1_12 = ReflectionUtils.isClassLoaded("org.bukkit.entity.Parrot");
    public static final boolean V1_13 = ReflectionUtils.isClassLoaded("org.bukkit.entity.Dolphin");
    public static final boolean V1_14 = ReflectionUtils.isClassLoaded("org.bukkit.entity.Fox");
    public static final boolean V1_15 = ReflectionUtils.isClassLoaded("org.bukkit.entity.Bee");
    public static final boolean V1_16 = ReflectionUtils.isClassLoaded("org.bukkit.entity.Strider");

    public static final boolean LEGACY_MATERIAL_IDS = !V1_13;
    public static final boolean HEX_CHAT_COLORS = V1_16;
    public static final boolean CHAT_COMPONENTS = ReflectionUtils.isClassLoaded("net.md_5.bungee.api.chat.ComponentBuilder");

}
