package com.provismet.cobblemon.daycareplus.compatibility;

import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.provismet.cobblemon.daycareplus.api.DaycarePlusEvents;
import net.minecraft.util.math.MathHelper;

public class CobblemonSizeVariationCompatibility implements DaycarePlusEvents.EggPropertiesCreated {
    @Override
    public void modifyProperties (Pokemon primary, Pokemon secondary, PokemonProperties properties) {
        float scale1 = primary.getScaleModifier();
        float scale2 = secondary.getScaleModifier();

        float scale = (float)MathHelper.lerp(Math.random(), scale1, scale2);
        properties.setScaleModifier(scale);
    }
}
