package com.provismet.cobblemon.daycareplus.storage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.provismet.cobblemon.daycareplus.api.EggHelper;
import com.provismet.cobblemon.daycareplus.api.PokemonEgg;
import com.provismet.cobblemon.daycareplus.config.IncubatorTiers;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class EggStorage {
    public static final Codec<EggStorage> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.fieldOf("tier").forGetter(EggStorage::getTier),
        PokemonEgg.CODEC.listOf().fieldOf("storage").forGetter(EggStorage::getStorage)
    ).apply(instance, EggStorage::new));

    private final List<PokemonEgg> storage;
    private int capacity;
    private int eggsToTick;
    private String tier;

    public EggStorage (String tier, List<PokemonEgg> eggs) {
        this.storage = new LinkedList<>(eggs);
        this.tier = tier;

        Optional<IncubatorTiers.IncubatorSettings> settings = IncubatorTiers.get(tier);
        if (settings.isPresent()) {
            this.capacity = settings.get().capacity();
            this.eggsToTick = settings.get().eggsToTick();
        }
        else {
            this.capacity = 1;
            this.eggsToTick= 1;
        }
    }

    @Nullable
    public static EggStorage fromSettings (String tier) {
        Optional<IncubatorTiers.IncubatorSettings> settings = IncubatorTiers.get(tier);
        return settings.map(incubatorSettings -> new EggStorage(tier, List.of())).orElse(null);
    }

    public void setTier (String tier) {
        this.tier = tier;
    }

    public String getTier () {
        return this.tier;
    }

    public void setCapacity (int capacity) {
        this.capacity = capacity;
    }

    public int getCapacity () {
        return this.capacity;
    }

    public void setEggsToTick (int eggsToTick) {
        this.eggsToTick = eggsToTick;
    }

    public int getEggsToTick () {
        return this.eggsToTick;
    }

    public List<PokemonEgg> getStorage () {
        return this.storage;
    }

    public boolean isFull () {
        return this.storage.size() >= this.capacity;
    }

    public int size () {
        return this.storage.size();
    }

    public void addCopyAndEmpty (ItemStack stack) {
        if (this.isFull()) return;

        EggHelper.tryGetEgg(stack.copy()).ifPresent(egg -> {
            this.storage.add(egg);
            stack.setCount(0);
        });
    }

    public void remove (int index) {
        if (index < this.storage.size() && index >= 0) this.storage.remove(index);
    }

    public ItemStack getItem (int index) {
        if (index >= this.storage.size()) return ItemStack.EMPTY;
        return this.storage.get(index).getItem();
    }

    public void tick (int stepsToProcess, ServerPlayerEntity player) {
        for (int i = 0; i < this.eggsToTick && i < this.storage.size(); ++i) {
            this.storage.get(i).decrementEggSteps(stepsToProcess, player);
        }
        this.storage.removeIf(PokemonEgg::isHatched);
    }

    public void tryUpgradeTo (String tier) {
        Optional<IncubatorTiers.IncubatorSettings> settings = IncubatorTiers.get(tier);
        if (settings.isPresent() && settings.get().capacity() > this.capacity && settings.get().eggsToTick() > this.eggsToTick) {
            this.tier = tier;
            this.capacity = settings.get().capacity();
            this.eggsToTick = settings.get().eggsToTick();
        }
    }
}
