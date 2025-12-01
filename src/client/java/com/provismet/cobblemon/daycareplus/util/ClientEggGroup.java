package com.provismet.cobblemon.daycareplus.util;

import com.cobblemon.mod.common.api.pokemon.egg.EggGroup;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.provismet.cobblemon.daycareplus.networking.EggGroupRequestC2S;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class ClientEggGroup {
    private static final Map<String, Set<EggGroup>> EGG_GROUPS = new HashMap<>();

    public static void saveGroup (String speciesFormShowdownId, Set<EggGroup> groups) {
        EGG_GROUPS.put(speciesFormShowdownId, groups);
    }

    public static Set<EggGroup> getGroups (Pokemon pokemon) {
        if (EGG_GROUPS.containsKey(pokemon.getForm().showdownId())) {
            return EGG_GROUPS.get(pokemon.getForm().showdownId());
        }

        Set<EggGroup> clientGroups = pokemon.getForm().getEggGroups();
        if (!clientGroups.isEmpty()) return clientGroups;

        // Nothing found, ask the server.
        ClientPlayNetworking.send(EggGroupRequestC2S.fromPokemon(pokemon));
        return Set.of();
    }
}
