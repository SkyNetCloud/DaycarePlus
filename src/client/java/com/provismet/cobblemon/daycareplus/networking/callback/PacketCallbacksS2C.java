package com.provismet.cobblemon.daycareplus.networking.callback;

import com.provismet.cobblemon.daycareplus.networking.EggGroupResponseS2C;
import com.provismet.cobblemon.daycareplus.util.ClientEggGroup;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public abstract class PacketCallbacksS2C {
    public static void register () {
        ClientPlayNetworking.registerGlobalReceiver(EggGroupResponseS2C.ID, PacketCallbacksS2C::receiveEggGroupResponse);
    }

    private static void receiveEggGroupResponse (EggGroupResponseS2C packet, ClientPlayNetworking.Context context) {
        ClientEggGroup.saveGroup(packet.speciesFormShowdownId(), packet.resolveGroups());
    }
}
