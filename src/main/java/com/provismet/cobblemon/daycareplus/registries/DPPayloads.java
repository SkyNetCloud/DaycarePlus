package com.provismet.cobblemon.daycareplus.registries;

import com.provismet.cobblemon.daycareplus.networking.EggGroupRequestC2S;
import com.provismet.cobblemon.daycareplus.networking.EggGroupResponseS2C;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public abstract class DPPayloads {
    public static void register () {
        PayloadTypeRegistry.playC2S().register(EggGroupRequestC2S.ID, EggGroupRequestC2S.CODEC);
        PayloadTypeRegistry.playS2C().register(EggGroupResponseS2C.ID, EggGroupResponseS2C.CODEC);
    }
}
