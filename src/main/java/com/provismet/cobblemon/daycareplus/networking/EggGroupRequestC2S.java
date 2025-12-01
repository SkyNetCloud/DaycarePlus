package com.provismet.cobblemon.daycareplus.networking;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.FormData;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import com.provismet.cobblemon.daycareplus.DaycarePlusMain;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record EggGroupRequestC2S (Identifier species, String form) implements CustomPayload {
    public static final Id<EggGroupRequestC2S> ID = new Id<>(DaycarePlusMain.identifier("egg_group_request"));
    public static final PacketCodec<RegistryByteBuf, EggGroupRequestC2S> CODEC = PacketCodec.tuple(
        Identifier.PACKET_CODEC,
        EggGroupRequestC2S::species,
        PacketCodecs.STRING,
        EggGroupRequestC2S::form,
        EggGroupRequestC2S::new
    );

    public static EggGroupRequestC2S fromPokemon (Pokemon pokemon) {
        return new EggGroupRequestC2S(pokemon.getSpecies().getResourceIdentifier(), pokemon.getForm().getName());
    }

    public FormData getFormData () {
        Species resolvedSpecies = PokemonSpecies.getByIdentifier(this.species);
        if (resolvedSpecies == null) return null;
        return resolvedSpecies.getFormByName(this.form);
    }

    @Override
    public Id<? extends CustomPayload> getId () {
        return ID;
    }
}
