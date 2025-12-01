package com.provismet.cobblemon.daycareplus.item;

import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.pokemon.stats.Stat;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.pokemon.Gender;
import com.cobblemon.mod.common.pokemon.IVs;
import com.provismet.cobblemon.daycareplus.api.PokemonEgg;
import com.provismet.cobblemon.daycareplus.api.PokemonEggProviderItem;
import com.provismet.cobblemon.daycareplus.config.DaycarePlusOptions;
import com.provismet.cobblemon.daycareplus.registries.DPItemDataComponents;
import com.provismet.cobblemon.daycareplus.util.StringFormatting;
import com.provismet.cobblemon.daycareplus.util.Styles;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class PokemonEggItem extends PolymerItem implements PokemonEggProviderItem {
    private static final int TICKS_PER_MINUTE = 60 * 20;

    private final PolymerModelData shiny;

    public PokemonEggItem (Settings settings, Item baseVanillaItem, PolymerModelData modelData, PolymerModelData shinyModel) {
        super(settings, baseVanillaItem, modelData);
        this.shiny = shinyModel;
    }

    public ItemStack createEggItem (PokemonProperties properties) {
        return new PokemonEgg(properties).getItem();
    }

    @Override
    public Optional<PokemonEgg> getEgg (ItemStack stack) {
        String properties = stack.get(DPItemDataComponents.POKEMON_PROPERTIES);
        if (properties == null) return Optional.empty();

        PokemonEgg egg = new PokemonEgg(
            properties,
            this.getMaxSteps(stack),
            this.getRemainingSteps(stack),
            false
        );
        return Optional.of(egg);
    }

    @Override
    public void appendTooltip (ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        if (stack.contains(DataComponentTypes.HIDE_ADDITIONAL_TOOLTIP)) return;

        Integer steps = stack.get(DPItemDataComponents.EGG_STEPS);
        if (steps != null) {
            String minutes = "" + (steps / TICKS_PER_MINUTE);
            String seconds = "" + ((steps % TICKS_PER_MINUTE) / 20);

            if (minutes.length() < 2) minutes = "0" + minutes;
            if (seconds.length() < 2) seconds = "0" + seconds;

            tooltip.add(Text.translatable("tooltip.daycareplus.egg.ticks", minutes + ":" + seconds));
        }
        if (!DaycarePlusOptions.shouldShowEggTooltip()) return;

        tooltip.add(Text.empty());
        String properties = stack.get(DPItemDataComponents.POKEMON_PROPERTIES);
        if (properties == null) {
            tooltip.add(Text.translatable("tooltip.daycareplus.egg.no_data"));
        }
        else {
            PokemonProperties pokemonProperties = PokemonProperties.Companion.parse(properties);
            if (pokemonProperties.getSpecies() != null) {
                MutableText species = Text.translatable("property.daycareplus.species").formatted(Formatting.YELLOW)
                    .append(this.getTooltipSpeciesName(pokemonProperties));
                tooltip.add(species);
            }
            if (pokemonProperties.getForm() != null) tooltip.add(Text.translatable("property.daycareplus.form").formatted(Formatting.YELLOW).append(this.getTooltipFormName(pokemonProperties)));
            if (pokemonProperties.getNature() != null) tooltip.add(Text.translatable("property.daycareplus.nature").formatted(Formatting.YELLOW).append(this.getTooltipNatureName(pokemonProperties)));
            if (pokemonProperties.getAbility() != null) tooltip.add(Text.translatable("property.daycareplus.ability").formatted(Formatting.YELLOW).append(this.getTooltipAbilityName(pokemonProperties)));
            if (pokemonProperties.getGender() != null && pokemonProperties.getGender() != Gender.GENDERLESS) {
                Text gender = switch (pokemonProperties.getGender()) {
                    case MALE -> Text.literal("M").formatted(Formatting.BLUE);
                    case FEMALE -> Text.literal("F").formatted(Formatting.RED);
                    default -> Text.literal("");
                };

                tooltip.add(Text.translatable("property.daycareplus.gender").formatted(Formatting.YELLOW).append(gender));
            }

            IVs iv = pokemonProperties.getIvs();
            if (iv != null) {
                tooltip.add(Text.empty());
                tooltip.add(Text.translatable("property.daycareplus.hp").styled(Styles.colouredNoItalics(Styles.HP))
                    .append(Text.literal(this.formatIV(iv, Stats.HP)).styled(Styles.WHITE_NO_ITALICS)));
                tooltip.add(Text.translatable("property.daycareplus.attack").styled(Styles.colouredNoItalics(Styles.ATTACK))
                    .append(Text.literal(this.formatIV(iv, Stats.ATTACK)).styled(Styles.WHITE_NO_ITALICS)));
                tooltip.add(Text.translatable("property.daycareplus.defence").styled(Styles.colouredNoItalics(Styles.DEFENCE))
                    .append(Text.literal(this.formatIV(iv, Stats.DEFENCE)).styled(Styles.WHITE_NO_ITALICS)));
                tooltip.add(Text.translatable("property.daycareplus.special_attack").styled(Styles.colouredNoItalics(Styles.SPECIAL_ATTACK))
                    .append(Text.literal(this.formatIV(iv, Stats.SPECIAL_ATTACK)).styled(Styles.WHITE_NO_ITALICS)));
                tooltip.add(Text.translatable("property.daycareplus.special_defence").styled(Styles.colouredNoItalics(Styles.SPECIAL_DEFENCE))
                    .append(Text.literal(this.formatIV(iv, Stats.SPECIAL_DEFENCE)).styled(Styles.WHITE_NO_ITALICS)));
                tooltip.add(Text.translatable("property.daycareplus.speed").styled(Styles.colouredNoItalics(Styles.SPEED))
                    .append(Text.literal(this.formatIV(iv, Stats.SPEED)).styled(Styles.WHITE_NO_ITALICS)));
            }
        }
    }

    // Exists for mixin convenience.
    private MutableText getTooltipSpeciesName (PokemonProperties properties) {
        MutableText text = Text.literal(StringFormatting.titleCase(properties.getSpecies())).styled(Styles.WHITE_NO_ITALICS);
        if (Objects.requireNonNullElse(properties.getShiny(), false)) text.append(Text.literal(" ★").formatted(Formatting.GOLD));
        return text;
    }

    // Exists for mixin convenience.
    private MutableText getTooltipFormName (PokemonProperties properties) {
        return Text.literal(StringFormatting.titleCase(properties.getForm())).styled(Styles.WHITE_NO_ITALICS);
    }

    // Exists for mixin convenience.
    private MutableText getTooltipNatureName (PokemonProperties properties) {
        assert properties.getNature() != null;
        return Text.literal(StringFormatting.titleCase(Identifier.of(properties.getNature()).getPath())).styled(Styles.WHITE_NO_ITALICS);
    }

    // Exists for mixin convenience.
    private MutableText getTooltipAbilityName (PokemonProperties properties) {
        return Text.translatable("cobblemon.ability." + properties.getAbility()).styled(Styles.WHITE_NO_ITALICS);
    }

    public int getRemainingSteps (ItemStack stack) {
        return stack.getOrDefault(DPItemDataComponents.EGG_STEPS, PokemonEgg.DEFAULT_STEPS);
    }

    public int getMaxSteps (ItemStack stack) {
        return stack.getOrDefault(DPItemDataComponents.MAX_EGG_STEPS, PokemonEgg.DEFAULT_STEPS);
    }

    @Override
    public int getPolymerCustomModelData (ItemStack stack, @Nullable ServerPlayerEntity player) {
        if (stack.get(DataComponentTypes.CUSTOM_MODEL_DATA) != null) return -1;
        if (stack.getOrDefault(DPItemDataComponents.POKEMON_PROPERTIES, "").contains("shiny=true")) return this.shiny.value();
        return super.getPolymerCustomModelData(stack, player);
    }

    private String formatIV (IVs ivs, Stat stat) {
        Integer iv = ivs.get(stat);
        if (iv == null) return "?";
        return String.valueOf(iv);
    }
}
