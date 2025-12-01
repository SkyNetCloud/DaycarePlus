package com.provismet.cobblemon.daycareplus.registries;

import com.provismet.cobblemon.daycareplus.DaycarePlusMain;
import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import net.minecraft.item.ItemGroup;
import net.minecraft.text.Text;

public abstract class DPItemGroups {
    public static final ItemGroup DP_ITEMS = PolymerItemGroupUtils.builder()
        .icon(DPItems.POKEMON_EGG::getDefaultStack)
        .displayName(Text.translatable("title.daycareplus.item_group"))
        .entries((displayContext, entries) -> {
            entries.add(DPItems.COPPER_INCUBATOR);
            entries.add(DPItems.IRON_INCUBATOR);
            entries.add(DPItems.GOLD_INCUBATOR);
            entries.add(DPItems.DIAMOND_INCUBATOR);
            entries.add(DPItems.NETHERITE_INCUBATOR);
            entries.add(DPItems.FERTILITY_CANDY);
            entries.add(DPItems.DAYCARE_SPARK);
            entries.add(DPItems.DAYCARE_BOOSTER);
            entries.add(DPItems.SHINY_BOOSTER);
        })
        .build();

    public static void register () {
        PolymerItemGroupUtils.registerPolymerItemGroup(DaycarePlusMain.identifier("item_group"), DP_ITEMS);
    }
}
