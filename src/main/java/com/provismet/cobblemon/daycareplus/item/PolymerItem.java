package com.provismet.cobblemon.daycareplus.item;

import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public class PolymerItem extends SimplePolymerItem {
    private final PolymerModelData modelData;

    public PolymerItem (Settings settings, Item baseVanillaItem, PolymerModelData modelData) {
        super(settings, baseVanillaItem);
        this.modelData = modelData;
    }

    @Override
    public int getPolymerCustomModelData (ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return this.modelData.value();
    }
}
