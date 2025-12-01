package com.provismet.cobblemon.daycareplus.breeding;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public record FormPropertiesOverride (Map<String, String> assignments) {
    public static final Codec<FormPropertiesOverride> CODEC = Codec.unboundedMap(Codecs.NON_EMPTY_STRING, Codecs.NON_EMPTY_STRING)
        .comapFlatMap(map -> DataResult.success(new FormPropertiesOverride(map)), FormPropertiesOverride::assignments);

    public String toString (String assigner, String delimiter) {
        return String.join(delimiter, this.assignments.entrySet().stream().map(entry -> entry.getKey() + assigner + entry.getValue()).toList());
    }

    @NotNull
    @Override
    public String toString () {
        return this.toString("=", " ");
    }
}
