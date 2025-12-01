package com.provismet.cobblemon.daycareplus.breeding;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.api.pokemon.evolution.PreEvolution;
import com.cobblemon.mod.common.pokemon.FormData;
import com.cobblemon.mod.common.pokemon.Species;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Cobblemon does not read the form data at all for pre-evolutions.
 * This provides a data-driven way to create overrides so pre-evolutions are found correctly for edge-case species like Overqwil.
 */
public record PreEvoFormOverride (Identifier species, Map<String, SimpleForm> evoMap) {
    public static final Codec<PreEvoFormOverride> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Identifier.CODEC.fieldOf("species").forGetter(PreEvoFormOverride::species),
            Codec.unboundedMap(Codecs.NON_EMPTY_STRING, SimpleForm.CODEC).fieldOf("form_pre_evolutions").forGetter(PreEvoFormOverride::evoMap)
        ).apply(instance, PreEvoFormOverride::new)
    );

    public static PreEvoFormOverride simple (Identifier speciesId, Identifier preEvoId, Map<String, String> formMap) {
        return new PreEvoFormOverride(
            speciesId,
            formMap.entrySet()
                .stream()
                .map(entry -> Map.entry(entry.getKey(), new SimpleForm(preEvoId, entry.getValue())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
        );
    }

    @Nullable
    public PreEvolution getPreEvolution (String currentFormId) {
        Species species = PokemonSpecies.getByIdentifier(this.species);
        if (species == null) return null;


        if (!this.evoMap.containsKey(currentFormId)) {
            return species.getPreEvolution();
        }

        Species childSpecies = this.evoMap.get(currentFormId).getSpecies();
        FormData form = this.evoMap.get(currentFormId).getForm();
        if (childSpecies == null || form == null) return species.getPreEvolution();

        return PreEvolution.Companion.of(childSpecies, form);
    }

    @Nullable
    public PreEvolution getPreEvolution (FormData form) {
        return this.getPreEvolution(form.formOnlyShowdownId());
    }

    public boolean hasForm (String formId) {
        return this.evoMap.containsKey(formId);
    }

    @NotNull
    @Override
    public String toString () {
        String evoMapString = String.join(
            ", ",
            this.evoMap.entrySet().stream()
                .map(entry -> entry.getKey() + " -> (" + entry.getValue() + ")")
                .toList()
        );
        return this.species + "[" + evoMapString + "]";
    }

    public record SimpleForm (Identifier species, String formId) {
        private static final Codec<SimpleForm> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Identifier.CODEC.fieldOf("species").forGetter(SimpleForm::species),
                Codecs.NON_EMPTY_STRING.fieldOf("form_id").forGetter(SimpleForm::formId)
            ).apply(instance, SimpleForm::new)
        );

        public Species getSpecies () {
            return PokemonSpecies.getByIdentifier(this.species);
        }

        public FormData getForm () {
            Species pokemon = this.getSpecies();
            if (pokemon != null) return pokemon.getFormByShowdownId(this.formId);
            return null;
        }

        @NotNull
        @Override
        public String toString () {
            return this.species + " " + this.formId;
        }
    }
}
