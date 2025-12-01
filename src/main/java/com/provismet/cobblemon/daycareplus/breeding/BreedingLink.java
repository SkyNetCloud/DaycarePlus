package com.provismet.cobblemon.daycareplus.breeding;

import com.provismet.cobblemon.daycareplus.config.DaycarePlusOptions;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class BreedingLink {
    private static final Map<String, Set<String>> link = new HashMap<>();

    public static int count (ServerPlayerEntity player) {
        return count(player.getUuid());
    }

    public static int count (UUID playerUuid) {
        return link.getOrDefault(playerUuid.toString(), Set.of()).size();
    }

    public static boolean isAtLimit (UUID player) {
        return count(player) >= DaycarePlusOptions.getMaxPasturesPerPlayer();
    }

    public static boolean isAtLimit (ServerPlayerEntity player) {
        return isAtLimit(player.getUuid());
    }

    public static boolean add (UUID player, UUID daycareId) {
        if (isAtLimit(player)) return false;

        link.computeIfAbsent(player.toString(), playerUuid -> new HashSet<>()).add(daycareId.toString());
        return true;
    }

    public static boolean add (ServerPlayerEntity player, UUID daycareId) {
        return add(player.getUuid(), daycareId);
    }

    public static void remove (UUID player, UUID daycareId) {
        link.computeIfAbsent(player.toString(), playerUuid -> new HashSet<>()).remove(daycareId.toString());
    }

    public static void remove (ServerPlayerEntity player, UUID daycareId) {
        remove(player.getUuid(), daycareId);
    }

    public static void remove (ServerPlayerEntity player) {
        link.remove(player.getUuid().toString());
    }

    public static boolean has (UUID player, UUID daycareId) {
        return link.getOrDefault(player.toString(), Set.of()).contains(daycareId.toString());
    }

    public static boolean has (ServerPlayerEntity player, UUID daycareId) {
        return has(player.getUuid(), daycareId);
    }
}
