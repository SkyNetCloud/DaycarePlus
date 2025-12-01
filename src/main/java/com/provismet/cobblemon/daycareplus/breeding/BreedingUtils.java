package com.provismet.cobblemon.daycareplus.breeding;

import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.api.pokemon.egg.EggGroup;
import com.cobblemon.mod.common.api.pokemon.evolution.PreEvolution;
import com.cobblemon.mod.common.pokemon.FormData;
import com.cobblemon.mod.common.pokemon.Gender;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import com.cobblemon.mod.common.util.MiscUtilsKt;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.provismet.cobblemon.daycareplus.DaycarePlusMain;
import com.provismet.cobblemon.daycareplus.config.DaycarePlusOptions;
import com.provismet.cobblemon.daycareplus.feature.BreedableProperty;
import com.provismet.cobblemon.daycareplus.feature.FertilityFeature;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class BreedingUtils implements SimpleSynchronousResourceReloadListener {
    private static final String DITTO = "Ditto";
    private static final Map<Identifier, PreEvoFormOverride> PRE_EVO_OVERRIDES = new HashMap<>();
    private static final Map<String, FormPropertiesOverride> FORM_PROPERTY_OVERRIDES = new HashMap<>();

    public static boolean isAllowedToBreed(Pokemon pokemon) {

        Set<PokemonProperties> blacklist = DaycarePlusOptions.getBreedingBlacklist();
        for (PokemonProperties props : blacklist) {
            if (props.matches(pokemon)) {
                return false;
            }
        }

        return BreedableProperty.get(pokemon)
                && (!DaycarePlusOptions.doCompetitiveBreeding() || DaycarePlusOptions.shouldAllowBreedingWithoutFertility() || FertilityFeature.get(pokemon) > 0);
    }

    public static boolean canBreed (Pokemon parent1, Pokemon parent2) {
        if (!isAllowedToBreed(parent1) || !isAllowedToBreed(parent2)) return false;

        Set<EggGroup> eggGroups1 = parent1.getSpecies().getEggGroups();
        Set<EggGroup> eggGroups2 = parent2.getSpecies().getEggGroups();

        if (eggGroups1.contains(EggGroup.UNDISCOVERED) || eggGroups2.contains(EggGroup.UNDISCOVERED)) return false;
        if (eggGroups1.contains(EggGroup.DITTO) ^ eggGroups2.contains(EggGroup.DITTO)) return true;
        if (parent1.getGender() == Gender.GENDERLESS || parent2.getGender() == Gender.GENDERLESS) return false;
        if (parent1.getGender() == parent2.getGender()) return false;

        return eggGroups1.stream().anyMatch(eggGroups2::contains);
    }

    public static Pokemon getMotherOrNonDitto (Pokemon parent1, Pokemon parent2) {
        if (parent1.getGender() == Gender.FEMALE || parent2.getSpecies().getName().equalsIgnoreCase(DITTO)) return parent1;
        if (parent2.getGender() == Gender.FEMALE || parent1.getSpecies().getName().equalsIgnoreCase(DITTO)) return parent2;

        // Fallback value, this should not be reached.
        return parent1;
    }

    public static Optional<PotentialPokemonProperties> getOffspring (@Nullable Pokemon parent1, @Nullable Pokemon parent2) {
        if (parent1 == null || parent2 == null || !canBreed(parent1, parent2)) return Optional.empty();

        Pokemon primary = getMotherOrNonDitto(parent1, parent2);
        Pokemon secondary = primary == parent1 ? parent2 : parent1;
        return Optional.of(new PotentialPokemonProperties(primary, secondary));
    }

    public static boolean parentsHaveFertility (Pokemon parent1, Pokemon parent2) {
        return FertilityFeature.get(parent1) > 0 && FertilityFeature.get(parent2) > 0;
    }

    public static FormData getBabyForm (Pokemon parent) {
        PreEvolution preevo = PreEvolution.Companion.of(parent.getSpecies(), parent.getForm());
        PreEvolution temp;
        while ((temp = getPreEvolution(preevo)) != null) {
            preevo = temp;
        }

        // Special edge case for GameFreak not doing the intelligent thing and merging these back when they had the chance.
        // This would probably be better data-driven, but honestly if you're making a gender-split species then you've already dug your own grave, mate.
        // TODO: These workarounds are dumb, data-driven overrides needs to be expanded to include stuff like this!
        if (preevo.getSpecies().showdownId().equals("nidoranf") || preevo.getSpecies().showdownId().equals("nidoranm")) {
            Species species = getRandomGenderSpeciesSplit(MiscUtilsKt.cobblemonResource("nidoranm"), MiscUtilsKt.cobblemonResource("nidoranf"), 0.5);
            if (species != null) preevo = PreEvolution.Companion.of(species, species.getFormByShowdownId(preevo.getForm().formOnlyShowdownId()));
        }
        else if (preevo.getSpecies().showdownId().equals("volbeat") || preevo.getSpecies().showdownId().equals("illumise")) {
            Species species = getRandomGenderSpeciesSplit(MiscUtilsKt.cobblemonResource("volbeat"), MiscUtilsKt.cobblemonResource("illumise"), 0.5);
            if (species != null) preevo = PreEvolution.Companion.of(species, species.getFormByShowdownId(preevo.getForm().formOnlyShowdownId()));
        }
        else if (preevo.getSpecies().showdownId().equals("indeedee")) {
            preevo = Math.random() > 0.5 ? PreEvolution.Companion.of(preevo.getSpecies(), preevo.getSpecies().getForm(Set.of("female"))) : PreEvolution.Companion.of(preevo.getSpecies(), preevo.getSpecies().getForm(Set.of("male")));
        }

        return preevo.getForm();
    }

    // Necessary because for some reason the "region-bias-{{choice}}" aspects don't parse directly.
    public static String getFormProperties (FormData form) {
        if (FORM_PROPERTY_OVERRIDES.containsKey(form.formOnlyShowdownId())) {
            return FORM_PROPERTY_OVERRIDES.get(form.formOnlyShowdownId()).toString();
        }
        return String.join(" ", form.getAspects());
    }

    @Nullable
    private static PreEvolution getPreEvolution (PreEvolution pokemon) {
        if (pokemon == null) return null;

        // Try to get the pre-evolution from the overrides.
        Identifier speciesId = pokemon.getSpecies().getResourceIdentifier();
        if (PRE_EVO_OVERRIDES.containsKey(speciesId)) {
            PreEvoFormOverride override = PRE_EVO_OVERRIDES.get(speciesId);
            if (override.hasForm(pokemon.getForm().formOnlyShowdownId())) {
                return override.getPreEvolution(pokemon.getForm());
            }
        }

        PreEvolution preEvolution = pokemon.getForm().getPreEvolution();
        if (preEvolution == null) return null; // There is no pre-evolution, return null.

        // Pre-evolution exists, try to match the forms.
        return PreEvolution.Companion.of(preEvolution.getSpecies(), preEvolution.getSpecies().getFormByShowdownId(pokemon.getForm().formOnlyShowdownId()));
    }

    @Nullable
    private static Species getRandomGenderSpeciesSplit (Identifier male, Identifier female, double maleRatio) {
        Species maleSpecies = PokemonSpecies.getByIdentifier(male);
        Species femaleSpecies = PokemonSpecies.getByIdentifier(female);

        if (Math.random() < maleRatio) return maleSpecies;
        else return femaleSpecies;
    }

    @Override
    public Identifier getFabricId () {
        return DaycarePlusMain.identifier("reload_listener");
    }

    @Override
    public void reload (ResourceManager manager) {
        // Dynamic Registries probably make more sense in practice, but that would require passing Dynamic Registry managers all over this class.

        PRE_EVO_OVERRIDES.clear();
        FORM_PROPERTY_OVERRIDES.clear();

        Map<Identifier, Resource> overrides = manager.findResources("overrides/preevolutions", identifier -> Objects.equals(identifier.getNamespace(), DaycarePlusMain.MODID) && identifier.getPath().endsWith(".json"));
        for (Map.Entry<Identifier, Resource> entry : overrides.entrySet()) {
            try (InputStream stream = entry.getValue().getInputStream()) {
                String text = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
                DataResult<Pair<PreEvoFormOverride, JsonElement>> dataResult = PreEvoFormOverride.CODEC.decode(JsonOps.INSTANCE, JsonParser.parseString(text));
                PreEvoFormOverride resolved = dataResult.getOrThrow().getFirst();

                PRE_EVO_OVERRIDES.put(resolved.species(), resolved);
                DaycarePlusMain.LOGGER.info("Registered evolution override: {}", resolved);
            }
            catch (Throwable e) {
                DaycarePlusMain.LOGGER.error("DaycarePlus encountered an error whilst parsing override file {}: ", entry.getKey(), e);
            }
        }

        Map<Identifier, Resource> forms = manager.findResources("overrides/forms", identifier -> Objects.equals(identifier.getNamespace(), DaycarePlusMain.MODID) && identifier.getPath().endsWith(".json"));
        for (Map.Entry<Identifier, Resource> entry : forms.entrySet()) {
            try (InputStream stream = entry.getValue().getInputStream()) {
                String text = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
                DataResult<Pair<FormPropertiesOverride, JsonElement>> dataResult = FormPropertiesOverride.CODEC.decode(JsonOps.INSTANCE, JsonParser.parseString(text));
                FormPropertiesOverride resolved = dataResult.getOrThrow().getFirst();

                String formId = entry.getKey().getPath().replace("overrides/forms", "").replace("/", "").replace(".json", "");
                FORM_PROPERTY_OVERRIDES.put(formId, resolved);
                DaycarePlusMain.LOGGER.info("Registered form property override: {} -> {}", formId, resolved);
            }
            catch (Throwable e) {
                DaycarePlusMain.LOGGER.error("DaycarePlus encountered an error whilst parsing form property file file {}: ", entry.getKey(), e);
            }
        }
    }


}
