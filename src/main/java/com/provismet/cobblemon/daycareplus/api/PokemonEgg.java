package com.provismet.cobblemon.daycareplus.api;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.pokemon.HatchEggEvent;
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import com.cobblemon.mod.common.util.PlayerExtensionsKt;
import com.cobblemon.mod.common.util.ResourceLocationExtensionsKt;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.provismet.cobblemon.daycareplus.config.DaycarePlusOptions;
import com.provismet.cobblemon.daycareplus.registries.DPItemDataComponents;
import com.provismet.cobblemon.daycareplus.registries.DPItems;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class PokemonEgg {
    public static final int DEFAULT_STEPS = 7200;

    public static final Codec<PokemonEgg> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.optionalFieldOf("pokemon", "random level=1").forGetter(PokemonEgg::getPropertyString),
        Codec.INT.optionalFieldOf("steps", DEFAULT_STEPS).forGetter(PokemonEgg::getSteps),
        Codec.INT.optionalFieldOf("max_steps", DEFAULT_STEPS).forGetter(PokemonEgg::getMaxSteps),
        Codec.BOOL.optionalFieldOf("hatched", false).forGetter(PokemonEgg::isHatched)
    ).apply(instance, PokemonEgg::new));

    private final String propertyString;
    private final int maxSteps;
    private int steps;
    private PokemonProperties pokemonProperties;
    private boolean hatched;

    public PokemonEgg (String pokemonProperties, int steps, int maxSteps, boolean hatched) {
        this.propertyString = pokemonProperties;
        this.maxSteps = maxSteps;
        this.steps = steps;
        this.hatched = hatched;
        this.pokemonProperties = null;
    }

    public PokemonEgg (PokemonProperties pokemonProperties, int steps, int maxSteps, boolean hatched) {
        this.pokemonProperties = pokemonProperties;
        this.propertyString = pokemonProperties.asString(" ");
        this.maxSteps = maxSteps;
        this.steps = steps;
        this.hatched = hatched;
    }

    public PokemonEgg (PokemonProperties pokemonProperties) {
        this.pokemonProperties = pokemonProperties;
        this.propertyString = pokemonProperties.asString(" ");
        this.hatched = false;

        if (pokemonProperties.getSpecies() != null) {
            Identifier speciesId = ResourceLocationExtensionsKt.asIdentifierDefaultingNamespace(pokemonProperties.getSpecies(), Cobblemon.MODID);
            Species species = PokemonSpecies.getByIdentifier(speciesId);
            if (species != null) {
                this.steps = DaycarePlusOptions.getEggPoints(species.getEggCycles());
                this.maxSteps = this.steps;
            }
            else {
                this.steps = DEFAULT_STEPS;
                this.maxSteps = DEFAULT_STEPS;
            }
        }
        else {
            this.steps = DEFAULT_STEPS;
            this.maxSteps = DEFAULT_STEPS;
        }
    }

    public String getPropertyString () {
        return this.propertyString;
    }

    public int getSteps () {
        return this.steps;
    }

    public int getMaxSteps () {
        return this.maxSteps;
    }

    public boolean isHatched () {
        return this.hatched;
    }

    public ItemStack getItem () {
        ItemStack stack = DPItems.POKEMON_EGG.getDefaultStack();
        stack.set(DPItemDataComponents.POKEMON_PROPERTIES, this.getPropertyString());

        if (this.pokemonProperties == null) {
            this.pokemonProperties = PokemonProperties.Companion.parse(this.propertyString);
        }

        stack.set(DPItemDataComponents.EGG_STEPS, this.steps);
        stack.set(DPItemDataComponents.MAX_EGG_STEPS, this.maxSteps);
        if (this.steps < this.maxSteps) stack.setDamage(MathHelper.lerp(1f - ((float)this.steps / this.maxSteps), 1, 100));

        DaycarePlusEvents.POST_EGG_PRODUCED.invoker().afterItemCreated(stack);
        return stack;
    }

    public void decrementEggSteps (int amount, ServerPlayerEntity player) {
        this.steps = Math.max(0, steps - amount);

        if (steps == 0) {
            boolean playerPartyBusy = PlayerExtensionsKt.isPartyBusy(player) || PlayerExtensionsKt.isInBattle(player);
            boolean partyHasSpace = PlayerExtensionsKt.party(player).getFirstAvailablePosition() != null || PlayerExtensionsKt.pc(player).getFirstAvailablePosition() != null;

            // Don't waste eggs, only hatch if there is space!
            if (!playerPartyBusy && partyHasSpace) this.hatch(player);
        }
    }

    public void hatch (ServerPlayerEntity player) {
        if (this.pokemonProperties == null) {
            this.pokemonProperties = PokemonProperties.Companion.parse(this.propertyString);
        }

        CobblemonEvents.HATCH_EGG_PRE.emit(new HatchEggEvent.Pre(this.pokemonProperties, player));
        Pokemon pokemon = this.pokemonProperties.create(player);
        pokemon.getAbility().setForced$common(false);
        player.sendMessage(Text.translatable("message.overlay.daycareplus.egg.hatch"), true);
        PlayerExtensionsKt.party(player).add(pokemon);
        CobblemonEvents.HATCH_EGG_POST.emit(new HatchEggEvent.Post(player, pokemon));
        this.hatched = true;
    }
}
