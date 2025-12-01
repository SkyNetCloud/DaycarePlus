package com.provismet.cobblemon.daycareplus.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record IncubatorOwner (String playerName, String uuid) {
    public static final Codec<IncubatorOwner> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.fieldOf("name").forGetter(IncubatorOwner::playerName),
        Codec.STRING.fieldOf("uuid").forGetter(IncubatorOwner::uuid)
    ).apply(instance, IncubatorOwner::new));

    public static final PacketCodec<RegistryByteBuf, IncubatorOwner> PACKET_CODEC = PacketCodec.tuple(
        PacketCodecs.STRING,
        IncubatorOwner::playerName,
        PacketCodecs.STRING,
        IncubatorOwner::uuid,
        IncubatorOwner::new
    );

    public static final IncubatorOwner DEFAULT = new IncubatorOwner("", "");

    public IncubatorOwner (PlayerEntity player) {
        this(player.getName().getString(), player.getUuidAsString());
    }

    public boolean matches (PlayerEntity player) {
        return this.uuid.equals(player.getUuidAsString());
    }
}
