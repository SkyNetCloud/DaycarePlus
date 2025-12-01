package com.provismet.cobblemon.daycareplus.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.registry.RegistryBuilder;

public class DaycarePlusDatagen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator (FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(LanguageGenerator::new);
        pack.addProvider(ModelGenerator::new);
        pack.addProvider(ItemTagGenerator::new);
        pack.addProvider(RecipeGenerator::new);
        pack.addProvider(CobblemonPreEvolutionGenerator::new);
        pack.addProvider(CobblemonFormPropertyGenerator::new);
    }

    @Override
    public void buildRegistry (RegistryBuilder registryBuilder) {

    }
}
