package com.provismet.cobblemon.daycareplus.gui;

import com.cobblemon.mod.common.CobblemonItems;
import com.cobblemon.mod.common.CobblemonSounds;
import com.cobblemon.mod.common.api.abilities.AbilityTemplate;
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.pokemon.stats.Stat;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.block.entity.PokemonPastureBlockEntity;
import com.cobblemon.mod.common.item.PokemonItem;
import com.cobblemon.mod.common.pokemon.Nature;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.google.common.collect.ImmutableList;
import com.provismet.cobblemon.daycareplus.breeding.BreedingUtils;
import com.provismet.cobblemon.daycareplus.feature.BreedableProperty;
import com.provismet.cobblemon.daycareplus.feature.FertilityFeature;
import com.provismet.cobblemon.daycareplus.breeding.PotentialPokemonProperties;
import com.provismet.cobblemon.daycareplus.config.DaycarePlusOptions;
import com.provismet.cobblemon.daycareplus.imixin.IMixinPastureBlockEntity;
import com.provismet.cobblemon.daycareplus.registries.DPIconItems;
import com.provismet.cobblemon.daycareplus.registries.DPItems;
import com.provismet.cobblemon.daycareplus.util.Styles;
import com.provismet.cobblemon.daycareplus.util.tag.DPItemTags;
import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.MathHelper;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;

public interface DaycareGUI {
    static SimpleGui create (PokemonPastureBlockEntity pastureBlockEntity, IMixinPastureBlockEntity mixinPasture, ServerPlayerEntity player, BlockState state, BlockHitResult hit) {
        GuiElement filler = GuiElementBuilder.from(Items.GRAY_STAINED_GLASS_PANE.getDefaultStack())
            .hideDefaultTooltip()
            .hideTooltip()
            .build();

        GuiElement fillerHeldItem = GuiElementBuilder.from(Items.WHITE_STAINED_GLASS_PANE.getDefaultStack())
            .hideDefaultTooltip()
            .setName(Text.translatable("gui.button.daycareplus.no_item").styled(Styles.WHITE_NO_ITALICS))
            .build();

        GuiElement infoButton = GuiElementBuilder.from(DPIconItems.INFO.getDefaultStack())
            .setName(Text.translatable("gui.button.daycareplus.info").styled(Styles.WHITE_NO_ITALICS))
            .addLoreLine(Text.translatable("gui.button.daycareplus.info.tooltip.1").styled(Styles.GRAY_NO_ITALICS))
            .addLoreLine(Text.translatable("gui.button.daycareplus.info.tooltip.2").styled(Styles.GRAY_NO_ITALICS))
            .build();

        GuiElement openPasture = GuiElementBuilder.from(CobblemonItems.PASTURE.getDefaultStack())
            .setName(Text.translatable("gui.button.daycareplus.open_pasture").styled(Styles.WHITE_NO_ITALICS))
            .setCallback((index, type, action, gui) -> {
                gui.getPlayer().closeHandledScreen();
                mixinPasture.setShouldSkipDaycareGUI(true);
                state.onUse(gui.getPlayer().getWorld(), gui.getPlayer(), hit);
            })
            .build();

        GuiElement eggCounter = mixinPasture.getEggCounterButton();
        GuiElement twinBoostCounter = mixinPasture.getTwinBoostCounterButton();
        GuiElement shinyBoostCounter = mixinPasture.getShinyBoostCounterButton();

        Pokemon parent1 = null;
        Pokemon parent2 = null;
        if (!pastureBlockEntity.getTetheredPokemon().isEmpty()) parent1 = pastureBlockEntity.getTetheredPokemon().getFirst().getPokemon();
        if (pastureBlockEntity.getTetheredPokemon().size() == 2) parent2 = pastureBlockEntity.getTetheredPokemon().getLast().getPokemon();

        GuiElement missingParent = GuiElementBuilder.from(Items.BARRIER.getDefaultStack())
            .setName(Text.translatable("gui.button.daycareplus.no_parent").styled(Styles.WHITE_NO_ITALICS))
            .addLoreLine(Text.translatable("gui.button.daycareplus.no_parent.tooltip").styled(Styles.GRAY_NO_ITALICS))
            .build();

        GuiElement parent1Info;
        GuiElement parent1Item = fillerHeldItem;
        if (parent1 != null) {
            parent1Info = createButtonForPokemon(parent1);

            if (DaycarePlusOptions.doCompetitiveBreeding() ? parent1.heldItem().isIn(DPItemTags.COMPETITIVE_BREEDING) : parent1.heldItem().isIn(DPItemTags.NONCOMPETITIVE_BREEDING)) {
                parent1Item = GuiElementBuilder.from(parent1.heldItem())
                    .hideDefaultTooltip()
                    .addLoreLine(Text.translatable(parent1.heldItem().getTranslationKey() + ".breeding").styled(Styles.GRAY_NO_ITALICS))
                    .build();
            }
        }
        else {
            parent1Info = missingParent;
        }

        GuiElement parent2Info;
        GuiElement parent2Item = fillerHeldItem;
        if (parent2 != null) {
            parent2Info = createButtonForPokemon(parent2);

            if (DaycarePlusOptions.doCompetitiveBreeding() ? parent2.heldItem().isIn(DPItemTags.COMPETITIVE_BREEDING) : parent2.heldItem().isIn(DPItemTags.NONCOMPETITIVE_BREEDING)) {
                parent2Item = GuiElementBuilder.from(parent2.heldItem())
                    .hideDefaultTooltip()
                    .addLoreLine(Text.translatable(parent2.heldItem().getTranslationKey() + ".breeding").styled(Styles.GRAY_NO_ITALICS))
                    .build();
            }
        }
        else {
            parent2Info = missingParent;
        }

        GuiElement offspringInfo;
        Optional<PotentialPokemonProperties> offspring = BreedingUtils.getOffspring(parent1, parent2);
        if (offspring.isPresent()) {
            Map<Stat, PotentialPokemonProperties.PotentialIV> ivs = offspring.get().getPossibleIVs();
            PokemonProperties props = offspring.get().createPokemonProperties();
            props.setShiny(false);
            Pokemon tile = props.create();

            ImmutableList.Builder<Text> eggData = ImmutableList.builder();
            eggData.add(
                Text.translatable("property.daycareplus.species").styled(Styles.formattedNoItalics(Formatting.YELLOW)).append(tile.getSpecies().getTranslatedName().styled(Styles.WHITE_NO_ITALICS)),
                Text.translatable("property.daycareplus.form").styled(Styles.formattedNoItalics(Formatting.YELLOW)).append(Text.literal(tile.getForm().getName()).styled(Styles.WHITE_NO_ITALICS)),
                Text.translatable("property.daycareplus.ability").styled(Styles.formattedNoItalics(Formatting.YELLOW)).append(listOfTranslatable(Text.literal(", ").styled(Styles.WHITE_NO_ITALICS), Styles.WHITE_NO_ITALICS, offspring.get().getPossibleAbilities().stream().map(AbilityTemplate::getDisplayName).toArray(String[]::new))),
                Text.translatable("property.daycareplus.nature").styled(Styles.formattedNoItalics(Formatting.YELLOW)).append((offspring.get().getPossibleNatures().isEmpty() ? Text.literal("?").styled(Styles.WHITE_NO_ITALICS) : listOfTranslatable(Text.literal(",").styled(Styles.WHITE_NO_ITALICS), Styles.WHITE_NO_ITALICS, offspring.get().getPossibleNatures().stream().map(Nature::getDisplayName).toArray(String[]::new))))
            );
            if (props.getMoves() != null && !props.getMoves().isEmpty()) {
                eggData.add(Text.translatable("property.daycareplus.moves").styled(Styles.formattedNoItalics(Formatting.YELLOW))
                    .append(listOfTranslatable(Text.literal(", ").styled(Styles.WHITE_NO_ITALICS), Styles.WHITE_NO_ITALICS, props.getMoves().stream().map(move -> "cobblemon.move." + move).toArray(String[]::new))));
            }
            eggData.add(
                Text.empty(),
                Text.translatable("property.daycareplus.ivs").styled(Styles.WHITE_NO_ITALICS),
                Text.translatable("property.daycareplus.hp").styled(Styles.colouredNoItalics(Styles.HP)).append(Text.literal(ivs.get(Stats.HP).toString()).styled(Styles.WHITE_NO_ITALICS)),
                Text.translatable("property.daycareplus.attack").styled(Styles.colouredNoItalics(Styles.ATTACK)).append(Text.literal(ivs.get(Stats.ATTACK).toString()).styled(Styles.WHITE_NO_ITALICS)),
                Text.translatable("property.daycareplus.defence").styled(Styles.colouredNoItalics(Styles.DEFENCE)).append(Text.literal(ivs.get(Stats.DEFENCE).toString()).styled(Styles.WHITE_NO_ITALICS)),
                Text.translatable("property.daycareplus.special_attack").styled(Styles.colouredNoItalics(Styles.SPECIAL_ATTACK)).append(Text.literal(ivs.get(Stats.SPECIAL_ATTACK).toString()).styled(Styles.WHITE_NO_ITALICS)),
                Text.translatable("property.daycareplus.special_defence").styled(Styles.colouredNoItalics(Styles.SPECIAL_DEFENCE)).append(Text.literal(ivs.get(Stats.SPECIAL_DEFENCE).toString()).styled(Styles.WHITE_NO_ITALICS)),
                Text.translatable("property.daycareplus.speed").styled(Styles.colouredNoItalics(Styles.SPEED)).append(Text.literal(ivs.get(Stats.SPEED).toString()).styled(Styles.WHITE_NO_ITALICS))
            );

            if (DaycarePlusOptions.doCompetitiveBreeding()) {
                int newFertility = DaycarePlusOptions.shouldEggsInheritFertility() ?
                        MathHelper.clamp(Math.min(FertilityFeature.get(parent1), FertilityFeature.get(parent2)) - 1, 0, FertilityFeature.getMax()) :
                        FertilityFeature.getMax();

                eggData.add(
                    Text.empty(),
                    Text.translatable("property.daycareplus.fertility").styled(Styles.formattedNoItalics(Formatting.DARK_GREEN)).append(Text.literal(String.valueOf(newFertility)).styled(Styles.WHITE_NO_ITALICS))
                );
            }

            if (DaycarePlusOptions.shouldShowShinyChance()) eggData.add(
                Text.empty(),
                Text.translatable("property.daycareplus.shiny").styled(Styles.formattedNoItalics(Formatting.GOLD)).append(Text.literal("1/" + Math.max(1, (int)(1 / offspring.get().getShinyRate()))).styled(Styles.WHITE_NO_ITALICS))
            );

            if (FabricLoader.getInstance().isDevelopmentEnvironment()) eggData.add(
                Text.empty(),
                Text.literal("(Debug) Aspects: " + String.join(", ", tile.getAspects()))
            );

            offspringInfo = GuiElementBuilder.from(PokemonItem.from(props))
                .setName(Text.translatable("gui.button.daycareplus.offspring").styled(Styles.WHITE_NO_ITALICS))
                .setLore(eggData.build())
                .build();
        }
        else {
            offspringInfo = GuiElementBuilder.from(Items.BARRIER.getDefaultStack())
                .setName(Text.translatable("gui.button.daycareplus.offspring.empty").styled(style -> style.withItalic(false).withColor(Formatting.WHITE)))
                .addLoreLine(Text.translatable("gui.button.daycareplus.offspring.empty.tooltip").styled(style -> style.withItalic(false).withColor(Formatting.GRAY)))
                .build();
        }

        SimpleGui gui = new SimpleGui(ScreenHandlerType.GENERIC_9X5, player, false) {
            @Override
            public void onOpen () {
                this.player.playSoundToPlayer(CobblemonSounds.PC_ON, SoundCategory.BLOCKS, 1f, 1f);
            }

            @Override
            public void onClose () {
                this.player.playSoundToPlayer(CobblemonSounds.PC_OFF, SoundCategory.BLOCKS, 1f, 1f);
            }
        };
        gui.setTitle(Text.translatable("gui.title.daycareplus.daycare"));

        for (int i = 0; i < gui.getVirtualSize(); ++i) {
            gui.setSlot(i, filler);
        }

        if (mixinPasture.getExtension() != null) {
            if (mixinPasture.getExtension().getTwinBoosts() > 0) {
                gui.setSlot(0, twinBoostCounter);
                if (mixinPasture.getExtension().getShinyBoosts() > 0) {
                    gui.setSlot(1, shinyBoostCounter);
                }
            }
            else if (mixinPasture.getExtension().getShinyBoosts() > 0) {
                gui.setSlot(0, shinyBoostCounter);
            }
        }
        gui.setSlot(6, eggCounter);
        gui.setSlot(7, openPasture);
        gui.setSlot(8, infoButton);
        gui.setSlot(20, parent1Info);
        gui.setSlot(22, offspringInfo);
        gui.setSlot(24, parent2Info);
        gui.setSlot(29, parent1Item);
        gui.setSlot(33, parent2Item);
        return gui;
    }

    static GuiElement createEggButton (IMixinPastureBlockEntity mixinPasture) {
        return GuiElementBuilder.from(DPItems.POKEMON_EGG.getDefaultStack())
            .hideDefaultTooltip()
            .setName(Text.translatable("gui.button.daycareplus.eggs_held", mixinPasture.count(), mixinPasture.size()).styled(Styles.WHITE_NO_ITALICS))
            .build();
    }

    static GuiElement createTwinBoostButton (IMixinPastureBlockEntity mixinPasture) {
        return GuiElementBuilder.from(DPItems.DAYCARE_BOOSTER.getDefaultStack())
            .hideDefaultTooltip()
            .setName(Text.translatable("gui.button.daycareplus.twin_boosts_remaining", mixinPasture.getExtension() != null ? mixinPasture.getExtension().getTwinBoosts() : 0).styled(Styles.WHITE_NO_ITALICS))
            .setLore(List.of(Text.translatable("gui.button.daycareplus.twin_boosts_info").styled(Styles.GRAY_NO_ITALICS)))
            .build();
    }

    static GuiElement createShinyBoostButton (IMixinPastureBlockEntity mixinPasture) {
        return GuiElementBuilder.from(DPItems.SHINY_BOOSTER.getDefaultStack())
            .hideDefaultTooltip()
            .setName(Text.translatable("gui.button.daycareplus.shiny_boosts_remaining", mixinPasture.getExtension() != null ? mixinPasture.getExtension().getTwinBoosts() : 0).styled(Styles.WHITE_NO_ITALICS))
            .setLore(List.of(Text.translatable("gui.button.daycareplus.shiny_boost_rate", DaycarePlusOptions.getShinyBoosterRate() * 100).styled(Styles.GRAY_NO_ITALICS)))
            .build();
    }

    static GuiElement createButtonForPokemon (Pokemon pokemon) {
        GuiElementBuilder builder = GuiElementBuilder.from(PokemonItem.from(pokemon))
            .hideDefaultTooltip()
            .setName(Text.translatable("gui.button.daycareplus.parent").styled(Styles.WHITE_NO_ITALICS))
            .addLoreLine(formatProperty("property.daycareplus.species", Styles.formattedNoItalics(Formatting.YELLOW), pokemon.getSpecies().getTranslatedName()))
            .addLoreLine(formatProperty("property.daycareplus.form", Styles.formattedNoItalics(Formatting.YELLOW), pokemon.getForm().getName()))
            .addLoreLine(formatProperty("property.daycareplus.ability", Styles.formattedNoItalics(Formatting.YELLOW), Text.translatable(pokemon.getAbility().getDisplayName())))
            .addLoreLine(formatProperty("property.daycareplus.nature", Styles.formattedNoItalics(Formatting.YELLOW), Text.translatable(pokemon.getNature().getDisplayName())))
            .addLoreLine(Text.empty())
            .addLoreLine(Text.translatable("property.daycareplus.ivs").styled(Styles.WHITE_NO_ITALICS))
            .addLoreLine(formatProperty("property.daycareplus.hp", Styles.colouredNoItalics(Styles.HP), String.valueOf(pokemon.getIvs().getOrDefault(Stats.HP))))
            .addLoreLine(formatProperty("property.daycareplus.attack", Styles.colouredNoItalics(Styles.ATTACK), String.valueOf(pokemon.getIvs().getOrDefault(Stats.ATTACK))))
            .addLoreLine(formatProperty("property.daycareplus.defence", Styles.colouredNoItalics(Styles.DEFENCE), String.valueOf(pokemon.getIvs().getOrDefault(Stats.DEFENCE))))
            .addLoreLine(formatProperty("property.daycareplus.special_attack", Styles.colouredNoItalics(Styles.SPECIAL_ATTACK), String.valueOf(pokemon.getIvs().getOrDefault(Stats.SPECIAL_ATTACK))))
            .addLoreLine(formatProperty("property.daycareplus.special_defence", Styles.colouredNoItalics(Styles.SPECIAL_DEFENCE), String.valueOf(pokemon.getIvs().getOrDefault(Stats.SPECIAL_DEFENCE))))
            .addLoreLine(formatProperty("property.daycareplus.speed", Styles.colouredNoItalics(Styles.SPEED), String.valueOf(pokemon.getIvs().getOrDefault(Stats.SPEED))));


        if (!BreedableProperty.get(pokemon)) {
            builder.addLoreLine(Text.empty())
                .addLoreLine(Text.translatable("property.daycareplus.unbreedable").styled(Styles.formattedNoItalics(Formatting.DARK_RED)));
        }
        else if (DaycarePlusOptions.doCompetitiveBreeding()) {
            builder.addLoreLine(Text.empty())
                .addLoreLine(formatProperty("property.daycareplus.fertility", Styles.formattedNoItalics(Formatting.DARK_GREEN), String.valueOf(FertilityFeature.get(pokemon))));
        }

        return builder.build();
    }

    private static Text formatProperty (String translationKey, UnaryOperator<Style> style, String value) {
        return formatProperty(translationKey, style, Text.literal(value));
    }

    private static Text formatProperty (String translationKey, UnaryOperator<Style> style, MutableText value) {
        return Text.translatable(translationKey).styled(style)
            .append(value.styled(Styles.WHITE_NO_ITALICS));
    }

    private static Text listOfTranslatable (Text separator, UnaryOperator<Style> style, String... translatables) {
        MutableText text = Text.empty();
        for (int i = 0; i < translatables.length; ++i) {
            text = text.append(Text.translatable(translatables[i]).styled(style));
            if (i < translatables.length - 1) text = text.append(separator);
        }
        return text;
    }
}
