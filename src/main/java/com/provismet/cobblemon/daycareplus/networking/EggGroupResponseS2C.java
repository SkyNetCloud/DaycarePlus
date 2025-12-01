package com.provismet.cobblemon.daycareplus.networking;

import com.cobblemon.mod.common.api.pokemon.egg.EggGroup;
import com.cobblemon.mod.common.pokemon.FormData;
import com.provismet.cobblemon.daycareplus.DaycarePlusMain;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record EggGroupResponseS2C (String speciesFormShowdownId, List<String> eggGroups) implements CustomPayload {
    public static final Id<EggGroupResponseS2C> ID = new Id<>(DaycarePlusMain.identifier("egg_group_response"));
    public static final PacketCodec<RegistryByteBuf, EggGroupResponseS2C> CODEC = PacketCodec.tuple(
        PacketCodecs.STRING,
        EggGroupResponseS2C::speciesFormShowdownId,
        PacketCodecs.STRING.collect(PacketCodecs.toList()),
        EggGroupResponseS2C::eggGroups,
        EggGroupResponseS2C::new
    );

    public static EggGroupResponseS2C fromFormData (FormData form) {
        List<String> groups = form.getEggGroups().stream().map(EggGroup::name).toList();
        return new EggGroupResponseS2C(form.showdownId(), groups);
    }

    public Set<EggGroup> resolveGroups () {
        Set<EggGroup> groups = new HashSet<>();
        for (String name : this.eggGroups) {
            try {
                EggGroup group = EggGroup.valueOf(name);
                groups.add(group);
            }
            catch (Exception ignored) {}
        }
        return groups;
    }

    @Override
    public Id<? extends CustomPayload> getId () {
        return ID;
    }
}
