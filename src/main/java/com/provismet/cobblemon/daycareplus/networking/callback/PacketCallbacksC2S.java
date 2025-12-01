package com.provismet.cobblemon.daycareplus.networking.callback;

import com.cobblemon.mod.common.pokemon.FormData;
import com.provismet.cobblemon.daycareplus.networking.EggGroupRequestC2S;
import com.provismet.cobblemon.daycareplus.networking.EggGroupResponseS2C;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import java.util.List;

public abstract class PacketCallbacksC2S {
    public static void register () {
        ServerPlayNetworking.registerGlobalReceiver(EggGroupRequestC2S.ID, PacketCallbacksC2S::receiveEggRequest);
    }

    private static void receiveEggRequest (EggGroupRequestC2S packet, ServerPlayNetworking.Context context) {
        FormData formData = packet.getFormData();
        if (formData == null) {
            String id = packet.species().getPath() + packet.form();
            context.responseSender().sendPacket(new EggGroupResponseS2C(id, List.of()));
        }
        else {
            context.responseSender().sendPacket(EggGroupResponseS2C.fromFormData(formData));
        }
    }
}
