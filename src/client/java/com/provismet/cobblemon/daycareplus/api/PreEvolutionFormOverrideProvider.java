package com.provismet.cobblemon.daycareplus.api;

import com.cobblemon.mod.common.util.MiscUtilsKt;
import com.provismet.cobblemon.daycareplus.DaycarePlusMain;
import com.provismet.cobblemon.daycareplus.breeding.PreEvoFormOverride;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class PreEvolutionFormOverrideProvider implements DataProvider {
    protected final FabricDataOutput output;
    protected final CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup;

    public PreEvolutionFormOverrideProvider (FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        this.output = output;
        this.registryLookup = registryLookup;
    }

    @Override
    public CompletableFuture<?> run (DataWriter writer) {
        return this.registryLookup.thenCompose(wrapperLookup -> {
            PreEvolutionGenerator generator = new PreEvolutionGenerator();
            this.generate(generator);

            List<CompletableFuture<?>> futures = new ArrayList<>();

            for (Map.Entry<Identifier, PreEvoFormOverride> entry : generator.map.entrySet()) {
                futures.add(DataProvider.writeCodecToPath(
                    writer,
                    wrapperLookup,
                    PreEvoFormOverride.CODEC,
                    entry.getValue(),
                    this.output
                        .resolvePath(DataOutput.OutputType.DATA_PACK)
                        .resolve(DaycarePlusMain.MODID)
                        .resolve("overrides")
                        .resolve("preevolutions")
                        .resolve(entry.getKey().getNamespace())
                        .resolve(entry.getKey().getPath() + ".json")
                ));
            }

            return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
        });
    }

    protected abstract void generate (PreEvolutionGenerator generator);

    @Override
    public String getName () {
        return "Daycare+ Offspring Overrides";
    }

    public static final class PreEvolutionGenerator {
        private final Map<Identifier, PreEvoFormOverride> map = new HashMap<>();

        public void add (String species, String preEvoSpecies, String preEvoForm) {
            this.add(MiscUtilsKt.cobblemonResource(species), MiscUtilsKt.cobblemonResource(preEvoSpecies), preEvoForm);
        }

        public void add (String species, String parentForm, String preEvoSpecies, String preEvoForm) {
            this.add(MiscUtilsKt.cobblemonResource(species), parentForm, MiscUtilsKt.cobblemonResource(preEvoSpecies), preEvoForm);
        }

        public void add (Identifier speciesId, String parentForm, Identifier preEvoId, String preEvoForm) {
            this.add(speciesId, preEvoId, Map.of(parentForm, preEvoForm));
        }

        public void add (Identifier speciesId, Identifier preEvoId, String preEvoForm) {
            this.add(speciesId, "normal", preEvoId, preEvoForm);
        }

        public void add (Identifier speciesId, Identifier preEvoId, Map<String, String> formMapping) {
            this.add(speciesId, PreEvoFormOverride.simple(speciesId, preEvoId, formMapping));
        }

        public void add (Identifier speciesId, PreEvoFormOverride override) {
            this.map.put(speciesId, override);
        }
    }
}
