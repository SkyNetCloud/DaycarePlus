package com.provismet.cobblemon.daycareplus.feature;

import com.cobblemon.mod.common.api.properties.CustomPokemonPropertyType;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.properties.BooleanProperty;
import kotlin.Unit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public class BreedableProperty implements CustomPokemonPropertyType<BooleanProperty> {
    private static final String KEY = "breedable";

    public BooleanProperty create (boolean value) {
        return new BooleanProperty(
            KEY,
            value,
            BreedableProperty::applyToPokemon,
            BreedableProperty::applyToPokemonEntity,
            BreedableProperty::matchPokemon,
            BreedableProperty::matchPokemonEntity
        );
    }

    public static boolean get (Pokemon pokemon) {
        return matchPokemon(pokemon, true);
    }

    private static Unit applyToPokemon (Pokemon pokemon, boolean value) {
        pokemon.getPersistentData().putBoolean(KEY, value);
        pokemon.onChange(null);
        return Unit.INSTANCE;
    }

    private static Unit applyToPokemonEntity (PokemonEntity pokemon, boolean value) {
        return applyToPokemon(pokemon.getPokemon(), value);
    }

    private static boolean matchPokemon (Pokemon pokemon, boolean value) {
        if (pokemon.getPersistentData().contains(KEY)) {
            return pokemon.getPersistentData().getBoolean(KEY) == value;
        }
        return true;
    }

    private static boolean matchPokemonEntity (PokemonEntity pokemon, boolean value) {
        return matchPokemon(pokemon.getPokemon(), value);
    }

    @NotNull
    @Override
    public Iterable<String> getKeys () {
        return List.of(KEY);
    }

    @Override
    public boolean getNeedsKey () {
        return true;
    }

    @Nullable
    @Override
    public BooleanProperty fromString (@Nullable String stringValue) {
        if (stringValue == null) return null;

        boolean value = "true".equalsIgnoreCase(stringValue) || "yes".equalsIgnoreCase(stringValue);
        return this.create(value);
    }

    @NotNull
    @Override
    public Collection<String> examples () {
        return List.of("true", "false");
    }
}
