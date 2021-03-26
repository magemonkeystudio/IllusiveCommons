/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons;

import me.filoghost.fcommons.collection.EnumLookupRegistry;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public final class MaterialsHelper {

    // LookupRegistry of materials by numerical ID (before 1.13), name and aliases
    private static final EnumLookupRegistry<Material> MATERIALS_REGISTRY = initMaterialsRegistry();

    // Materials that are considered air (with 1.13+ compatibility)
    private static final Set<Material> AIR_MATERIALS = getExistingMaterials("AIR", "CAVE_AIR", "VOID_AIR");


    @SuppressWarnings("deprecation")
    private static EnumLookupRegistry<Material> initMaterialsRegistry() {
        EnumLookupRegistry<Material> materialsRegistry = EnumLookupRegistry.fromEnumValues(Material.class);

        // Add numerical IDs in legacy versions
        if (FeatureSupport.LEGACY_MATERIAL_IDS) {
            for (Material material : Material.values()) {
                materialsRegistry.put(Integer.toString(material.getId()), material);
            }
        }

        // Add some default useful aliases (when present)
        materialsRegistry.putEnumIfExisting("iron bar", "IRON_FENCE");
        materialsRegistry.putEnumIfExisting("iron bars", "IRON_FENCE");
        materialsRegistry.putEnumIfExisting("glass pane", "THIN_GLASS");
        materialsRegistry.putEnumIfExisting("nether wart", "NETHER_STALK");
        materialsRegistry.putEnumIfExisting("nether warts", "NETHER_STALK");
        materialsRegistry.putEnumIfExisting("slab", "STEP");
        materialsRegistry.putEnumIfExisting("double slab", "DOUBLE_STEP");
        materialsRegistry.putEnumIfExisting("stone brick", "SMOOTH_BRICK");
        materialsRegistry.putEnumIfExisting("stone bricks", "SMOOTH_BRICK");
        materialsRegistry.putEnumIfExisting("stone stair", "SMOOTH_STAIRS");
        materialsRegistry.putEnumIfExisting("stone stairs", "SMOOTH_STAIRS");
        materialsRegistry.putEnumIfExisting("potato", "POTATO_ITEM");
        materialsRegistry.putEnumIfExisting("carrot", "CARROT_ITEM");
        materialsRegistry.putEnumIfExisting("brewing stand", "BREWING_STAND_ITEM");
        materialsRegistry.putEnumIfExisting("cauldron", "CAULDRON_ITEM");
        materialsRegistry.putEnumIfExisting("carrot on stick", "CARROT_STICK");
        materialsRegistry.putEnumIfExisting("carrot on a stick", "CARROT_STICK");
        materialsRegistry.putEnumIfExisting("cobblestone wall", "COBBLE_WALL");
        materialsRegistry.putEnumIfExisting("acacia wood stairs", "ACACIA_STAIRS");
        materialsRegistry.putEnumIfExisting("dark oak wood stairs", "DARK_OAK_STAIRS");
        materialsRegistry.putEnumIfExisting("wood slab", "WOOD_STEP");
        materialsRegistry.putEnumIfExisting("double wood slab", "WOOD_DOUBLE_STEP");
        materialsRegistry.putEnumIfExisting("repeater", "DIODE");
        materialsRegistry.putEnumIfExisting("piston", "PISTON_BASE");
        materialsRegistry.putEnumIfExisting("sticky piston", "PISTON_STICKY_BASE");
        materialsRegistry.putEnumIfExisting("flower pot", "FLOWER_POT_ITEM");
        materialsRegistry.putEnumIfExisting("wood shovel", "WOOD_SPADE");
        materialsRegistry.putEnumIfExisting("stone shovel", "STONE_SPADE");
        materialsRegistry.putEnumIfExisting("gold shovel", "GOLD_SPADE");
        materialsRegistry.putEnumIfExisting("iron shovel", "IRON_SPADE");
        materialsRegistry.putEnumIfExisting("diamond shovel", "DIAMOND_SPADE");
        materialsRegistry.putEnumIfExisting("steak", "COOKED_BEEF");
        materialsRegistry.putEnumIfExisting("cooked porkchop", "GRILLED_PORK");
        materialsRegistry.putEnumIfExisting("raw porkchop", "PORK");
        materialsRegistry.putEnumIfExisting("hardened clay", "HARD_CLAY");
        materialsRegistry.putEnumIfExisting("huge brown mushroom", "HUGE_MUSHROOM_1");
        materialsRegistry.putEnumIfExisting("huge red mushroom", "HUGE_MUSHROOM_2");
        materialsRegistry.putEnumIfExisting("mycelium", "MYCEL");
        materialsRegistry.putEnumIfExisting("poppy", "RED_ROSE");
        materialsRegistry.putEnumIfExisting("comparator", "REDSTONE_COMPARATOR");
        materialsRegistry.putEnumIfExisting("skull", "SKULL_ITEM");
        materialsRegistry.putEnumIfExisting("head", "SKULL_ITEM");
        materialsRegistry.putEnumIfExisting("redstone torch", "REDSTONE_TORCH_ON");
        materialsRegistry.putEnumIfExisting("redstone lamp", "REDSTONE_LAMP_OFF");
        materialsRegistry.putEnumIfExisting("glistering melon", "SPECKLED_MELON");
        materialsRegistry.putEnumIfExisting("acacia leaves", "LEAVES_2");
        materialsRegistry.putEnumIfExisting("acacia log", "LOG_2");
        materialsRegistry.putEnumIfExisting("gunpowder", "SULPHUR");
        materialsRegistry.putEnumIfExisting("lilypad", "WATER_LILY");
        materialsRegistry.putEnumIfExisting("command block", "COMMAND");
        materialsRegistry.putEnumIfExisting("dye", "INK_SACK");

        return materialsRegistry;
    }

    @Nullable
    public static Material matchMaterial(String materialName) {
        return MATERIALS_REGISTRY.lookup(materialName);
    }

    private static Set<Material> getExistingMaterials(String... materialEnumNames) {
        Set<Material> existingMaterials = new HashSet<>();

        for (String materialEnumName : materialEnumNames) {
            try {
                existingMaterials.add(Material.valueOf(materialEnumName));
            } catch (IllegalArgumentException e) {
                // Ignore, not existing
            }
        }

        return existingMaterials;
    }

    public static boolean isAir(Material material) {
        return AIR_MATERIALS.contains(material);
    }

}
