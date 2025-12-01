package com.provismet.cobblemon.daycareplus.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record IncubatorType (String tier, String type) {
    public static final Codec<IncubatorType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.fieldOf("tier").forGetter(IncubatorType::tier),
        Codec.STRING.fieldOf("type").forGetter(IncubatorType::type)
    ).apply(instance, IncubatorType::new));

    public static final PacketCodec<RegistryByteBuf, IncubatorType> PACKET_CODEC = PacketCodec.tuple(
        PacketCodecs.STRING,
        IncubatorType::tier,
        PacketCodecs.STRING,
        IncubatorType::type,
        IncubatorType::new
    );

    public static final String MAIN_TYPE = "main";
    public static final IncubatorType DEFAULT = IncubatorType.ofMain("copper");

    public static IncubatorType ofMain (String tier) {
        return new IncubatorType(tier, MAIN_TYPE);
    }
}
