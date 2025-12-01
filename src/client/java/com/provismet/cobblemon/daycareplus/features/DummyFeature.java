package com.provismet.cobblemon.daycareplus.features;

import com.cobblemon.mod.common.api.pokemon.feature.SpeciesFeature;
import com.cobblemon.mod.common.api.pokemon.feature.SynchronizedSpeciesFeature;
import com.google.gson.JsonObject;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import org.jetbrains.annotations.NotNull;

public class DummyFeature implements SynchronizedSpeciesFeature {
    @Override
    public @NotNull String getName () {
        return "daycareplus_dummy";
    }

    @Override
    public @NotNull NbtCompound saveToNBT (@NotNull NbtCompound nbtCompound) {
        return new NbtCompound();
    }

    @Override
    public @NotNull SpeciesFeature loadFromNBT (@NotNull NbtCompound nbtCompound) {
        return new DummyFeature();
    }

    @Override
    public @NotNull JsonObject saveToJSON (@NotNull JsonObject jsonObject) {
        return new JsonObject();
    }

    @Override
    public @NotNull SpeciesFeature loadFromJSON (@NotNull JsonObject jsonObject) {
        return new DummyFeature();
    }

    @Override
    public void saveToBuffer (@NotNull RegistryByteBuf registryByteBuf, boolean b) {

    }

    @Override
    public void loadFromBuffer (@NotNull RegistryByteBuf registryByteBuf) {

    }
}
