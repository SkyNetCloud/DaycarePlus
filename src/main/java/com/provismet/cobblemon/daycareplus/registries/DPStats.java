package com.provismet.cobblemon.daycareplus.registries;

import com.provismet.cobblemon.daycareplus.DaycarePlusMain;
import eu.pb4.polymer.core.api.other.PolymerStat;
import net.minecraft.stat.StatFormatter;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public abstract class DPStats {
    public static final Identifier EGGS_HATCHED = register("eggs_hatched", Text.translatable("stat.daycareplus.eggs_hatched"));
    public static final Identifier EGGS_COLLECTED = register("eggs_collected", Text.translatable("stat.daycareplus.eggs_collected"));

    private static Identifier register (String name, Text displayName) {
        return PolymerStat.registerStat(DaycarePlusMain.identifier(name), displayName, StatFormatter.DEFAULT);
    }

    public static void init () {}
}
