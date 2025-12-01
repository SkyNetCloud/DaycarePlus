package com.provismet.cobblemon.daycareplus.api;

import net.minecraft.item.ItemStack;

import java.util.Optional;

public interface PokemonEggProviderItem {
    Optional<PokemonEgg> getEgg (ItemStack stack);
}
