package com.provismet.cobblemon.daycareplus.datagen;

import com.provismet.cobblemon.daycareplus.api.PreEvolutionFormOverrideProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class CobblemonPreEvolutionGenerator extends PreEvolutionFormOverrideProvider {
    public CobblemonPreEvolutionGenerator (FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    protected void generate (PreEvolutionGenerator generator) {
        // Manaphy
        generator.add("manaphy", "phione", "normal");

        // Alola
        generator.add("exeggutor", "alola", "exeggcute", "alolabias");
        generator.add("marowak", "alola", "cubone", "alolabias");
        generator.add("raichu", "alola", "pikachu", "alolabias");

        // Galar
        generator.add("cursola", "corsola", "galar");
        generator.add("mrmime", "galar", "mimejr", "galarbias");
        generator.add("obstagoon", "linoone", "galar");
        generator.add("perrserker", "meowth", "galar");
        generator.add("runerigus", "yamask", "galar");
        generator.add("sirfetchd", "farfetchd", "galar");
        generator.add("weezing", "galar", "koffing", "galarbias");

        // Hisui
        generator.add("avalugg", "hisui", "bergmite", "hisuibias");
        generator.add("basculegion", "basculin", "whitestriped");
        generator.add("braviary", "hisui", "rufflet", "hisuibias");
        generator.add("decidueye", "hisui", "dartrix", "hisuibias");
        generator.add("lilligant", "hisui", "petilil", "hisuibias");
        generator.add("overqwil", "qwilfish", "hisui");
        generator.add("sneasler", "sneasel", "hisui");
        generator.add("samurott", "hisui", "dewott", "hisuibias");
        generator.add("sliggoo", "hisui", "goomy", "hisuibias");
        generator.add("typhlosion", "hisui", "quilava", "hisuibias");

        // Paldea
        generator.add("clodsire", "wooper", "paldea");
    }
}
