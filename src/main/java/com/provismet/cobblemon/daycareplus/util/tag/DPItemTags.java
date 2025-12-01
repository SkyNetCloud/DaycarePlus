package com.provismet.cobblemon.daycareplus.util.tag;

import com.provismet.cobblemon.daycareplus.DaycarePlusMain;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public abstract class DPItemTags {
    public static final TagKey<Item> BREEDING_ITEM = DPItemTags.of("breeding");
    public static final TagKey<Item> COMPETITIVE_BREEDING = DPItemTags.of("competitive_breeding");
    public static final TagKey<Item> NONCOMPETITIVE_BREEDING = DPItemTags.of("noncompetitive_breeding");
    public static final TagKey<Item> NO_CONSUME_BREEDING = DPItemTags.of("no_consume_breeding");
    public static final TagKey<Item> INCUBATORS = DPItemTags.of("incubators");
    public static final TagKey<Item> BYPASS_DAYCARE_OPEN = DPItemTags.of("bypass_daycare_open");
    public static final TagKey<Item> GUI = DPItemTags.of("gui");

    private static TagKey<Item> of (String path) {
        return TagKey.of(RegistryKeys.ITEM, DaycarePlusMain.identifier(path));
    }
}
