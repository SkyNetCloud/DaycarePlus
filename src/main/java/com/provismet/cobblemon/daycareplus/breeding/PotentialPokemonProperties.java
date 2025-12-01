package com.provismet.cobblemon.daycareplus.breeding;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.CobblemonItems;
import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.abilities.Abilities;
import com.cobblemon.mod.common.api.abilities.Ability;
import com.cobblemon.mod.common.api.abilities.AbilityTemplate;
import com.cobblemon.mod.common.api.abilities.CommonAbilityType;
import com.cobblemon.mod.common.api.abilities.PotentialAbility;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.pokemon.ShinyChanceCalculationEvent;
import com.cobblemon.mod.common.api.moves.MoveTemplate;
import com.cobblemon.mod.common.api.pokeball.PokeBalls;
import com.cobblemon.mod.common.api.pokemon.Natures;
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.pokemon.stats.Stat;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.pokeball.PokeBall;
import com.cobblemon.mod.common.pokemon.FormData;
import com.cobblemon.mod.common.pokemon.Gender;
import com.cobblemon.mod.common.pokemon.IVs;
import com.cobblemon.mod.common.pokemon.Nature;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import com.cobblemon.mod.common.pokemon.abilities.HiddenAbilityType;
import com.provismet.cobblemon.daycareplus.api.DaycarePlusEvents;
import com.provismet.cobblemon.daycareplus.config.DaycarePlusOptions;
import com.provismet.cobblemon.daycareplus.util.MathExtras;
import kotlin.Pair;
import net.minecraft.item.Item;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class PotentialPokemonProperties {
    private final Pokemon primary;
    private final Pokemon secondary;

    private final FormData form;

    /**
     * @param primary The mother or non-ditto parent.
     * @param secondary The father or ditto parent.
     */
    public PotentialPokemonProperties (Pokemon primary, Pokemon secondary) {
        this.primary = primary;
        this.secondary = secondary;
        this.form = BreedingUtils.getBabyForm(this.primary);
    }

    public PokemonProperties createPokemonProperties () {
        PokemonProperties properties = PokemonProperties.Companion.parse(BreedingUtils.getFormProperties(this.form), " ", "=");
        properties.setSpecies(this.form.getSpecies().showdownId());
        properties.setForm(this.form.formOnlyShowdownId()); // This is borderline cosmetic because the form gets overridden by aspects anyway but do it to get the form listed in the properties.

        this.setAbility(properties);
        this.setGender(properties);
        this.setIVs(properties);
        this.setNature(properties);
        this.setEggMoves(properties);
        this.setPokeBall(properties);
        this.setShiny(properties);
        properties.setTeraType(this.form.getPrimaryType().getName());
        properties.setLevel(1);
        properties.setFriendship(120);
        properties.updateAspects();

        DaycarePlusEvents.EGG_PROPERTIES_CREATED.invoker().modifyProperties(this.primary, this.secondary, properties);
        return properties;
    }

    public Pokemon getPrimary () {
        return this.primary;
    }

    public Pokemon getSecondary () {
        return this.secondary;
    }

    public Species getSpecies () {
        return this.form.getSpecies();
    }

    public FormData getForm () {
        return this.form;
    }

    /**
     * @return Possible natures for the offspring, an empty list denotes all natures are valid.
     */
    public List<Nature> getPossibleNatures () {
        List<Nature> natures = new ArrayList<>();
        if (this.primary.getHeldItem$common().isOf(CobblemonItems.EVERSTONE)) natures.add(this.primary.getNature());
        if (this.secondary.getHeldItem$common().isOf(CobblemonItems.EVERSTONE)) natures.add(this.secondary.getNature());

        return natures;
    }

    public List<PokeBall> getPossiblePokeBalls () {
        List<PokeBall> balls = new ArrayList<>();
        Set<PokeBall> illegal = Set.of(PokeBalls.getMasterBall(), PokeBalls.getCherishBall());

        // Species match, 50/50 chance
        if (this.primary.getSpecies().getName().equalsIgnoreCase(this.secondary.getSpecies().getName())) {
            balls.add(illegal.contains(this.primary.getCaughtBall()) ? PokeBalls.getPokeBall() : this.primary.getCaughtBall());
            balls.add(illegal.contains(this.secondary.getCaughtBall()) ? PokeBalls.getPokeBall() : this.secondary.getCaughtBall());
        }
        // Take from primary parent
        else {
            balls.add(illegal.contains(this.primary.getCaughtBall()) ? PokeBalls.getPokeBall() : this.primary.getCaughtBall());
        }

        return balls;
    }

    public List<AbilityTemplate> getPossibleAbilities () {
        Ability parentAbility = this.primary.getAbility();
        Pair<Priority, Integer> parentAbilityData;

        if (parentAbility.getIndex() >= 0) {
            parentAbilityData = new Pair<>(parentAbility.getPriority(), parentAbility.getIndex());
        }
        else {
            Optional<Map.Entry<Priority, List<PotentialAbility>>> abilities = this.primary.getForm().getAbilities().getMapping().entrySet()
                .stream()
                .filter(entry -> entry.getValue()
                    .stream()
                    .map(PotentialAbility::getTemplate)
                    .anyMatch(template -> template == parentAbility.getTemplate()))
                .findFirst();

            parentAbilityData = abilities.map(priorityListEntry -> new Pair<>(
                priorityListEntry.getKey(),
                priorityListEntry.getValue().stream().map(PotentialAbility::getTemplate).toList().indexOf(parentAbility.getTemplate())
            )).orElseGet(
                () -> new Pair<>(Priority.LOWEST, 0)
            );
        }

        AbilityTemplate inheritedAbility = null;
        // An ability with this priority does exist.
        if (this.form.getAbilities().getMapping().containsKey(parentAbilityData.getFirst())) {
            List<PotentialAbility> abilities = this.form.getAbilities().getMapping().get(parentAbilityData.getFirst());

            if (!abilities.isEmpty()) {
                if (abilities.size() > parentAbilityData.getSecond()) inheritedAbility = abilities.get(parentAbilityData.getSecond()).getTemplate();
                else inheritedAbility = abilities.getFirst().getTemplate();
            }
        }

        if (inheritedAbility == null) {
            List<PotentialAbility> abilities = this.form.getAbilities().getMapping().get(Priority.LOWEST);
            if (abilities == null || abilities.isEmpty()) inheritedAbility = Abilities.INSTANCE.getDUMMY();
            else inheritedAbility = abilities.getFirst().getTemplate();
        }

        AbilityTemplate finalAbility = inheritedAbility; // Done purely so the stream plays nice
        List<AbilityTemplate> otherPossibleAbilities = this.form.getAbilities().getMapping().values().stream()
            .flatMap(Collection::stream)
            .filter(other -> other.getType() instanceof CommonAbilityType || (other.getType() instanceof HiddenAbilityType && parentAbility.getPriority() == Priority.LOW))
            .map(PotentialAbility::getTemplate)
            .filter(other -> other != finalAbility)
            .toList();

        List<AbilityTemplate> potentials = new ArrayList<>();
        potentials.add(inheritedAbility);
        potentials.addAll(otherPossibleAbilities);

        return potentials;
    }

    public Map<Stat, PotentialIV> getPossibleIVs () {
        return Map.of(
            Stats.HP, PotentialIV.fromParents(this.primary, this.secondary, Stats.HP),
            Stats.ATTACK, PotentialIV.fromParents(this.primary, this.secondary, Stats.ATTACK),
            Stats.DEFENCE, PotentialIV.fromParents(this.primary, this.secondary, Stats.DEFENCE),
            Stats.SPECIAL_ATTACK, PotentialIV.fromParents(this.primary, this.secondary, Stats.SPECIAL_ATTACK),
            Stats.SPECIAL_DEFENCE, PotentialIV.fromParents(this.primary, this.secondary, Stats.SPECIAL_DEFENCE),
            Stats.SPEED, PotentialIV.fromParents(this.primary, this.secondary, Stats.SPEED)
        );
    }

    public List<String> getEggMoves () {
        List<MoveTemplate> validEggMoves = this.form.getMoves().getEggMoves();
        List<String> eggMoves = new ArrayList<>();

        this.secondary.getBenchedMoves().forEach(benchedMove -> {
            if (validEggMoves.stream().anyMatch(valid -> valid.getName().equalsIgnoreCase(benchedMove.getMoveTemplate().getName()))) {
                eggMoves.add(benchedMove.getMoveTemplate().getName());
            }
        });

        this.secondary.getMoveSet().forEach(move -> {
            if (validEggMoves.stream().anyMatch(valid -> valid.getName().equalsIgnoreCase(move.getName()))) {
                eggMoves.add(move.getName());
            }
        });

        if (DaycarePlusOptions.doGen6EggMoves()) {
            this.primary.getBenchedMoves().forEach(benchedMove -> {
                if (validEggMoves.stream().anyMatch(valid -> valid.getName().equalsIgnoreCase(benchedMove.getMoveTemplate().getName()))) {
                    eggMoves.add(benchedMove.getMoveTemplate().getName());
                }
            });

            this.primary.getMoveSet().forEach(move -> {
                if (validEggMoves.stream().anyMatch(valid -> valid.getName().equalsIgnoreCase(move.getName()))) {
                    eggMoves.add(move.getName());
                }
            });
        }

        // TODO: These workarounds are dumb, data-driven overrides needs to be expanded to include stuff like this!
        if ((this.primary.getSpecies().showdownId().equals("pikachu") && this.primary.heldItem().isOf(CobblemonItems.LIGHT_BALL))
        || (this.secondary.getSpecies().showdownId().equals("pikachu") && this.secondary.heldItem().isOf(CobblemonItems.LIGHT_BALL))) {
            eggMoves.add("volttackle");
        }

        return eggMoves;
    }

    public double getShinyRate () {
        float shinyRate;

        if (DaycarePlusOptions.shouldUseShinyChanceEvent()) {
            ShinyChanceCalculationEvent event = new ShinyChanceCalculationEvent(Cobblemon.config.getShinyRate(), this.primary);
            CobblemonEvents.SHINY_CHANCE_CALCULATION.emit(event);
            shinyRate = event.calculate(null);
        }
        else {
            shinyRate = Cobblemon.config.getShinyRate();
        }

        shinyRate /= DaycarePlusOptions.getShinyChanceMultiplier();
        if (!Objects.equals(this.primary.getOriginalTrainer(), this.secondary.getOriginalTrainer())) {
            shinyRate /= DaycarePlusOptions.getMasudaMultiplier();
        }
        if (this.primary.getShiny()) shinyRate /= DaycarePlusOptions.getCrystalMultiplier();
        if (this.secondary.getShiny()) shinyRate /= DaycarePlusOptions.getCrystalMultiplier();

        return shinyRate == 0 ? 1 : 1 / shinyRate;
    }

    private void setPokeBall (PokemonProperties properties) {
        List<PokeBall> balls = this.getPossiblePokeBalls();
        if (Math.random() < 0.5) properties.setPokeball(balls.getFirst().getName().toString());
        else properties.setPokeball(balls.getLast().getName().toString());
    }

    private void setGender (PokemonProperties properties) {
        if (this.form.getMaleRatio() < 0) properties.setGender(Gender.GENDERLESS);
        else if (Math.random() < this.form.getMaleRatio()) properties.setGender(Gender.MALE);
        else properties.setGender(Gender.FEMALE);
    }

    private void setAbility (PokemonProperties properties) {
        Ability parentAbility = this.primary.getAbility();
        Pair<Priority, Integer> parentAbilityData;

        if (parentAbility.getIndex() >= 0) { // Should pass under normal circumstances (valid ability)
            parentAbilityData = new Pair<>(parentAbility.getPriority(), parentAbility.getIndex());
        }
        else { // Into the weird territory we go...
            Optional<Map.Entry<Priority, List<PotentialAbility>>> abilities = this.primary.getForm().getAbilities().getMapping().entrySet()
                .stream()
                .filter(entry -> entry.getValue()
                    .stream()
                    .map(PotentialAbility::getTemplate)
                    .anyMatch(template -> template == parentAbility.getTemplate()))
                .findFirst();

            parentAbilityData = abilities.map(priorityListEntry -> new Pair<>(
                priorityListEntry.getKey(),
                priorityListEntry.getValue().stream().map(PotentialAbility::getTemplate).toList().indexOf(parentAbility.getTemplate())
            )).orElseGet(
                () -> new Pair<>(Priority.LOWEST, 0) // Nothing matched, just go with a default value.
            );
        }

        // The inherited ability will always the first index of this list
        List<AbilityTemplate> potentials = this.getPossibleAbilities();

        double keepCurrentAbility = parentAbilityData.getFirst() == Priority.LOW ? 0.6 : 0.8;
        if (potentials.size() == 1 || Math.random() < keepCurrentAbility) {
            properties.setAbility(potentials.getFirst().getName());
        }
        else if (!potentials.isEmpty()) {
            potentials.removeFirst();
            int randomIndex = Math.clamp((int)(Math.random() * potentials.size()), 0, potentials.size() - 1);
            properties.setAbility(potentials.get(randomIndex).getName());
        }
    }

    private void setNature (PokemonProperties properties) {
        List<Nature> natures = this.getPossibleNatures();
        if (!natures.isEmpty() && !(DaycarePlusOptions.doCompetitiveBreeding() && !BreedingUtils.parentsHaveFertility(this.primary, this.secondary))) {
            // Technically there can be 2 possible natures if both parents hold an everstone.
            if (Math.random() < 0.5) properties.setNature(natures.getFirst().getName().toString());
            else properties.setNature(natures.getLast().getName().toString());
        }
        else {
            properties.setNature(MathExtras.randomChoice(Natures.all().stream().toList()).getName().toString());
        }
    }

    private void setShiny (PokemonProperties properties) {
        properties.setShiny(Math.random() < this.getShinyRate());
    }

    private void setEggMoves (PokemonProperties properties) {
        List<String> eggMoves = this.getEggMoves();

        if (properties.getMoves() != null) eggMoves.addAll(properties.getMoves());
        properties.setMoves(eggMoves.stream().distinct().toList());
    }

    private void setIVs (PokemonProperties properties) {
        int forcedIVs = 3;
        if (DaycarePlusOptions.doCompetitiveBreeding() && !BreedingUtils.parentsHaveFertility(this.primary, this.secondary)) {
            forcedIVs = 0;
        }
        else if (this.primary.heldItem().isOf(CobblemonItems.DESTINY_KNOT) || this.secondary.heldItem().isOf(CobblemonItems.DESTINY_KNOT)) {
            forcedIVs = 5;
        }

        Map<Stat, PotentialIV> potentials = this.getPossibleIVs();
        List<Stat> remaining = new ArrayList<>();
        remaining.add(Stats.HP);
        remaining.add(Stats.ATTACK);
        remaining.add(Stats.DEFENCE);
        remaining.add(Stats.SPECIAL_ATTACK);
        remaining.add(Stats.SPECIAL_DEFENCE);
        remaining.add(Stats.SPEED);

        IVs iv = new IVs();
        for (Map.Entry<Stat, PotentialIV> potent : potentials.entrySet()) {
            if (potent.getValue().isForced()) {
                --forcedIVs;
                iv.set(potent.getKey(), MathExtras.randomChoice(potent.getValue().values().stream().toList()));
                remaining.remove(potent.getKey());
            }
        }

        for (int i = 0; i < forcedIVs && !remaining.isEmpty(); ++i) {
            Stat inheritThis = MathExtras.randomChoice(remaining);
            iv.set(inheritThis, MathExtras.randomChoice(potentials.get(inheritThis).values().stream().filter(value -> value != PotentialIV.WILDCARD).toList()));
            remaining.remove(inheritThis);
        }

        for (Stat stat : remaining) {
            iv.set(stat, MathHelper.clamp((int)(Math.random() * 32), 0, 31));
        }

        properties.setIvs(iv);
    }

    public record PotentialIV (boolean isForced, Set<Integer> values) {
        public static final int WILDCARD = -1;

        public static PotentialIV fromParents (Pokemon parent1, Pokemon parent2, Stat stat) {
            Item powerItem = switch (stat) {
                case Stats.HP -> CobblemonItems.POWER_WEIGHT;
                case Stats.ATTACK -> CobblemonItems.POWER_BRACER;
                case Stats.DEFENCE -> CobblemonItems.POWER_BELT;
                case Stats.SPECIAL_ATTACK -> CobblemonItems.POWER_LENS;
                case Stats.SPECIAL_DEFENCE -> CobblemonItems.POWER_BAND;
                case Stats.SPEED -> CobblemonItems.POWER_ANKLET;
                default -> null;
            };

            Set<Integer> possibleIVs = new HashSet<>();
            boolean forced = false;
            if (powerItem != null) {
                if (parent1.getHeldItem$common().isOf(powerItem)) {
                    forced = true;
                    possibleIVs.add(parent1.getIvs().getOrDefault(stat));
                }
                if (parent2.getHeldItem$common().isOf(powerItem)) {
                    forced = true;
                    possibleIVs.add(parent2.getIvs().getOrDefault(stat));
                }
            }

            if (!forced) {
                possibleIVs.add(WILDCARD);
                possibleIVs.add(parent1.getIvs().getOrDefault(stat));
                possibleIVs.add(parent2.getIvs().getOrDefault(stat));
            }

            if (DaycarePlusOptions.doCompetitiveBreeding()) {
                if (BreedingUtils.parentsHaveFertility(parent1, parent2)) {
                    possibleIVs.remove(WILDCARD);
                    forced = true;
                    int mean = 0;
                    for (int value : possibleIVs) {
                        mean += value;
                    }
                    mean /= possibleIVs.size();
                    possibleIVs.clear();
                    possibleIVs.add(mean);
                }
                else { // Inherit nothing without fertility.
                    possibleIVs.clear();
                    possibleIVs.add(WILDCARD);
                }
            }

            return new PotentialIV(forced, possibleIVs);
        }

        @NotNull
        @Override
        public String toString () {
            return String.join(" | ", this.values.stream().map(val -> val == WILDCARD ? "?" : String.valueOf(val)).toList());
        }
    }
}
