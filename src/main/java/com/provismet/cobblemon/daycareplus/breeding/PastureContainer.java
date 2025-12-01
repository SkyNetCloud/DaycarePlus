package com.provismet.cobblemon.daycareplus.breeding;

import com.provismet.cobblemon.daycareplus.config.DaycarePlusOptions;
import com.provismet.cobblemon.daycareplus.registries.DPItems;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface PastureContainer extends SidedInventory {
    void add (ItemStack stack);
    int count ();
    List<ItemStack> withdraw (int amount);

    @Override
    default int[] getAvailableSlots (Direction side) {
        if (side == Direction.DOWN) {
            int[] slots = new int[this.size()];
            for (int i = 0; i < slots.length; ++i) {
                slots[i] = i;
            }
            return slots;
        }
        return new int[0];
    }

    @Override
    default boolean isValid (int slot, ItemStack stack) {
        if (!stack.isOf(DPItems.POKEMON_EGG)) return false;
        return SidedInventory.super.isValid(slot, stack);
    }

    @Override
    default boolean canInsert (int slot, ItemStack stack, @Nullable Direction dir) {
        return false;
    }

    @Override
    default boolean canExtract (int slot, ItemStack stack, Direction dir) {
        return DaycarePlusOptions.shouldAllowHoppers() && !stack.isEmpty();
    }
}
