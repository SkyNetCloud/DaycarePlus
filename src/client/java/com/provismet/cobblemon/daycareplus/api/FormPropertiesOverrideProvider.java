package com.provismet.cobblemon.daycareplus.api;

import com.provismet.cobblemon.daycareplus.DaycarePlusMain;
import com.provismet.cobblemon.daycareplus.breeding.FormPropertiesOverride;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.registry.RegistryWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class FormPropertiesOverrideProvider implements DataProvider {
    protected final FabricDataOutput output;
    protected final CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup;

    public FormPropertiesOverrideProvider (FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        this.output = output;
        this.registryLookup = registryLookup;
    }

    @Override
    public CompletableFuture<?> run (DataWriter writer) {
        return this.registryLookup.thenCompose(wrapperLookup -> {
            FormPropertyGenerator generator = new FormPropertyGenerator();
            this.generate(generator);

            List<CompletableFuture<?>> futures = new ArrayList<>();

            for (Map.Entry<String, Map<String, String>> entry : generator.properties.entrySet()) {
                futures.add(DataProvider.writeCodecToPath(
                    writer,
                    wrapperLookup,
                    FormPropertiesOverride.CODEC,
                    new FormPropertiesOverride(entry.getValue()),
                    this.output
                        .resolvePath(DataOutput.OutputType.DATA_PACK)
                        .resolve(DaycarePlusMain.MODID)
                        .resolve("overrides")
                        .resolve("forms")
                        .resolve(entry.getKey() + ".json")
                ));
            }

            return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
        });
    }

    protected abstract void generate (FormPropertyGenerator generator);

    @Override
    public String getName () {
        return "Daycare+ Form Property Overrides";
    }

    protected static class FormPropertyGenerator {
        private final Map<String, Map<String, String>> properties = new HashMap<>();

        public void add (String formId, String feature, String featureValue) {
            Map<String, String> featureMap = this.properties.getOrDefault(formId, new HashMap<>());
            if (!this.properties.containsKey(formId)) this.properties.put(formId, featureMap);

            featureMap.put(feature, featureValue);
        }

        public void add (String formId, Map<String, String> featureMapping) {
            this.properties.put(formId, featureMapping);
        }

        public void add (String formId, FormPropertiesOverride override) {
            this.properties.put(formId, override.assignments());
        }
    }
}
