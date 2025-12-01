package com.provismet.cobblemon.daycareplus.datagen;

import com.cobblemon.mod.common.CobblemonItems;
import com.provismet.cobblemon.daycareplus.registries.DPIconItems;
import com.provismet.cobblemon.daycareplus.registries.DPItems;
import com.provismet.cobblemon.daycareplus.util.tag.DPItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class ItemTagGenerator extends FabricTagProvider.ItemTagProvider {
    public ItemTagGenerator (FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture, null);
    }

    @Override
    protected void configure (RegistryWrapper.WrapperLookup wrapperLookup) {
        this.getOrCreateTagBuilder(DPItemTags.BREEDING_ITEM)
            .add(CobblemonItems.EVERSTONE)
            .add(CobblemonItems.POWER_ANKLET)
            .add(CobblemonItems.POWER_BAND)
            .add(CobblemonItems.POWER_BELT)
            .add(CobblemonItems.POWER_BRACER)
            .add(CobblemonItems.POWER_LENS)
            .add(CobblemonItems.POWER_WEIGHT)
            .add(CobblemonItems.MIRROR_HERB);

        this.getOrCreateTagBuilder(DPItemTags.COMPETITIVE_BREEDING)
            .addOptionalTag(DPItemTags.BREEDING_ITEM);

        this.getOrCreateTagBuilder(DPItemTags.NONCOMPETITIVE_BREEDING)
            .addOptionalTag(DPItemTags.BREEDING_ITEM)
            .add(CobblemonItems.DESTINY_KNOT);

        this.getOrCreateTagBuilder(DPItemTags.NO_CONSUME_BREEDING)
            .add(CobblemonItems.MIRROR_HERB);

        this.getOrCreateTagBuilder(DPItemTags.INCUBATORS)
            .add(DPItems.COPPER_INCUBATOR)
            .add(DPItems.IRON_INCUBATOR)
            .add(DPItems.GOLD_INCUBATOR)
            .add(DPItems.DIAMOND_INCUBATOR)
            .add(DPItems.NETHERITE_INCUBATOR);

        this.getOrCreateTagBuilder(DPItemTags.BYPASS_DAYCARE_OPEN)
            .addOptionalTag(DPItemTags.INCUBATORS)
            .add(DPItems.DAYCARE_SPARK)
            .add(DPItems.DAYCARE_BOOSTER)
            .add(DPItems.SHINY_BOOSTER);

        this.getOrCreateTagBuilder(DPItemTags.GUI)
            .add(DPIconItems.INFO)
            .add(DPIconItems.LEFT)
            .add(DPIconItems.RIGHT)
            .add(DPIconItems.TAKE_ALL);

        this.getOrCreateTagBuilder(TagKey.of(RegistryKeys.ITEM, Identifier.of("c", "hidden_from_recipe_viewers")))
            .addOptionalTag(DPItemTags.GUI);
    }
}
