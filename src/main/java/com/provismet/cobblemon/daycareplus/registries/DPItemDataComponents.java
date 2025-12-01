package com.provismet.cobblemon.daycareplus.registries;

import com.mojang.serialization.Codec;
import com.provismet.cobblemon.daycareplus.DaycarePlusMain;
import com.provismet.cobblemon.daycareplus.item.component.IncubatorOwner;
import com.provismet.cobblemon.daycareplus.item.component.IncubatorType;
import eu.pb4.polymer.core.api.other.PolymerComponent;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.dynamic.Codecs;

import java.util.function.UnaryOperator;

public abstract class DPItemDataComponents {
    public static final ComponentType<String> POKEMON_PROPERTIES = register("pokemon_properties", builder -> builder.codec(Codec.STRING).packetCodec(PacketCodecs.STRING));
    public static final ComponentType<Integer> EGG_STEPS = register("egg_steps", builder -> builder.codec(Codecs.NONNEGATIVE_INT).packetCodec(PacketCodecs.INTEGER));
    public static final ComponentType<Integer> MAX_EGG_STEPS = register("max_egg_steps", builder -> builder.codec(Codecs.NONNEGATIVE_INT).packetCodec(PacketCodecs.INTEGER));
    public static final ComponentType<IncubatorOwner> INCUBATOR_OWNER = register("incubator_owner", builder -> builder.codec(IncubatorOwner.CODEC).packetCodec(IncubatorOwner.PACKET_CODEC));
    public static final ComponentType<IncubatorType> INCUBATOR_TYPE = register("incubator_type", builder -> builder.codec(IncubatorType.CODEC).packetCodec(IncubatorType.PACKET_CODEC));
    public static final ComponentType<Integer> EGGS_HELD = register("eggs_held", builder -> builder.codec(Codecs.NONNEGATIVE_INT).packetCodec(PacketCodecs.INTEGER));
    public static final ComponentType<Integer> BOOST_AMOUNT = register("boost_amount", builder -> builder.codec(Codecs.NONNEGATIVE_INT).packetCodec(PacketCodecs.INTEGER));

    private static <T> ComponentType<T> register (String name, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        ComponentType<T> component = Registry.register(Registries.DATA_COMPONENT_TYPE, DaycarePlusMain.identifier(name), builderOperator.apply(ComponentType.builder()).build());
        PolymerComponent.registerDataComponent(component);
        return component;
    }

    public static void init () {}
}
