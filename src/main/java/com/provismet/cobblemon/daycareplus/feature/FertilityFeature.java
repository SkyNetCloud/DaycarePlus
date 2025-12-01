package com.provismet.cobblemon.daycareplus.feature;

import com.cobblemon.mod.common.api.pokemon.feature.GlobalSpeciesFeatures;
import com.cobblemon.mod.common.api.pokemon.feature.IntSpeciesFeature;
import com.cobblemon.mod.common.api.pokemon.feature.IntSpeciesFeatureProvider;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.util.MiscUtilsKt;
import com.provismet.cobblemon.daycareplus.config.DaycarePlusOptions;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class FertilityFeature {
    public static final String KEY = "fertility";
    private static final IntSpeciesFeatureProvider fertility;

    static {
        fertility = new IntSpeciesFeatureProvider();
        fertility.setKeys(List.of(KEY));
        fertility.setMin(0);
        fertility.setMax(DaycarePlusOptions.getMaxFertility());
        fertility.setDefault(DaycarePlusOptions.getMaxFertility());
        fertility.setVisible(DaycarePlusOptions.doCompetitiveBreeding());

        if (DaycarePlusOptions.doCompetitiveBreeding()) {
            IntSpeciesFeatureProvider.DisplayData display = new IntSpeciesFeatureProvider.DisplayData();
            display.setColour(new Vec3d(85, 175, 43));
            display.setName("daycareplus.ui.fertility.level");
            display.setUnderlay(MiscUtilsKt.cobblemonResource("textures/gui/summary/summary_stats_other_bar.png"));
            display.setOverlay(MiscUtilsKt.cobblemonResource("textures/gui/summary/summary_stats_friendship_overlay.png"));
            fertility.setDisplay(display);
        }
    }

    public static void register () {
        GlobalSpeciesFeatures.register(KEY, fertility);
    }

    public static int get (Pokemon pokemon) {
        if (!DaycarePlusOptions.doCompetitiveBreeding()) return DaycarePlusOptions.getMaxFertility();

        IntSpeciesFeature fertilityFeature = fertility.get(pokemon);
        if (fertilityFeature == null) {
            return DaycarePlusOptions.getMaxFertility();
        }

        return fertilityFeature.getValue();
    }

    public static int getMax () {
        return DaycarePlusOptions.getMaxFertility();
    }

    public static void increment (Pokemon pokemon) {
        IntSpeciesFeature feature = fertility.get(pokemon);
        if (feature == null) return;

        if (feature.getValue() < DaycarePlusOptions.getMaxFertility()) {
            feature.setValue(feature.getValue() + 1);
            feature.apply(pokemon);
        }
    }

    public static void decrement (Pokemon pokemon) {
        IntSpeciesFeature feature = fertility.get(pokemon);
        if (feature == null) return;

        if (feature.getValue() > 0) {
            feature.setValue(feature.getValue() - 1);
            feature.apply(pokemon);
        }
    }
}
