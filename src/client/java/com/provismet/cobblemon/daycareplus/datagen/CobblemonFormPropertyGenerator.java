package com.provismet.cobblemon.daycareplus.datagen;

import com.provismet.cobblemon.daycareplus.api.FormPropertiesOverrideProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class CobblemonFormPropertyGenerator extends FormPropertiesOverrideProvider {
    public CobblemonFormPropertyGenerator (FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    protected void generate (FormPropertyGenerator generator) {
        generator.add("alolabias", "region_bias", "alola");
        generator.add("galarbias", "region_bias", "galar");
        generator.add("hisuibias", "region_bias", "hisui");
        generator.add("whitestriped", "fish_stripes", "white");
        generator.add("bluestriped", "fish_stripes", "blue");

        // Do not allow mega forms to be inherited.
        generator.add("mega", "mega_evolution", "none");
        generator.add("megax", "mega_evolution", "none");
        generator.add("megay", "mega_evolution", "none");
    }
}
