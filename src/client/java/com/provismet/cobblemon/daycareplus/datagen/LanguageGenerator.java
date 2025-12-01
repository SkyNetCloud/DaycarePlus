package com.provismet.cobblemon.daycareplus.datagen;

import com.cobblemon.mod.common.CobblemonItems;
import com.cobblemon.mod.common.api.pokemon.egg.EggGroup;
import com.provismet.cobblemon.daycareplus.registries.DPIconItems;
import com.provismet.cobblemon.daycareplus.registries.DPItems;
import com.provismet.lilylib.datagen.provider.LilyLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.registry.RegistryWrapper;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class LanguageGenerator extends LilyLanguageProvider {
    protected LanguageGenerator (FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generateTranslations (RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder translationBuilder) {
        translationBuilder.add("title.daycareplus.item_group", "Daycare+");

        translationBuilder.add(DPItems.POKEMON_EGG, "Pokémon Egg");
        
        translationBuilder.add(DPItems.COPPER_INCUBATOR, "Copper Incubator");
        translationBuilder.add(DPItems.IRON_INCUBATOR, "Iron Incubator");
        translationBuilder.add(DPItems.GOLD_INCUBATOR, "Gold Incubator");
        translationBuilder.add(DPItems.DIAMOND_INCUBATOR, "Diamond Incubator");
        translationBuilder.add(DPItems.NETHERITE_INCUBATOR, "Netherite Incubator");

        translationBuilder.add(DPIconItems.INFO, "Info");
        translationBuilder.add(DPIconItems.LEFT, "Left");
        translationBuilder.add(DPIconItems.RIGHT, "Right");
        translationBuilder.add(DPIconItems.TAKE_ALL, "Deposit All");

        translationBuilder.add(DPItems.FERTILITY_CANDY, "Fertility Candy");
        translationBuilder.add(DPItems.FERTILITY_CANDY.getTranslationKey() + ".tooltip", "Increases a Pokémon's fertility by 1");
        translationBuilder.add(DPItems.DAYCARE_SPARK, "Daycare Spark");
        translationBuilder.add(DPItems.DAYCARE_SPARK.getTranslationKey() + ".tooltip", "Forces a daycare to immediately produce an egg");
        translationBuilder.add("message.overlay.daycareplus.spark_failure", "This daycare lacks compatible parents!");
        translationBuilder.add(DPItems.DAYCARE_BOOSTER, "Dizygotic Booster");
        translationBuilder.add(DPItems.DAYCARE_BOOSTER.getTranslationKey() + ".tooltip", "Adds an extra egg to the next %1$s daycare cycles");
        translationBuilder.add("message.overlay.daycareplus.fertility_boosted", "Your %1$s now has %2$s fertility.");
        translationBuilder.add("message.overlay.daycareplus.egg_boosted", "Your daycare now has %1$s twin boosts.");
        translationBuilder.add(DPItems.SHINY_BOOSTER, "Sparkling Booster");
        translationBuilder.add(DPItems.SHINY_BOOSTER.getTranslationKey() + ".tooltip", "Adds extra shiny odds to the next %1$s daycare cycles");
        translationBuilder.add("message.overlay.daycareplus.egg_shiny_boosted", "Your daycare now has %1$s shiny boosts.");

        // Egg Bag
        translationBuilder.add("message.overlay.daycareplus.incubator.collection.singular", "Collected %1$s egg.");
        translationBuilder.add("message.overlay.daycareplus.incubator.collection.plural", "Collected %1$s eggs.");
        translationBuilder.add("message.overlay.daycareplus.incubator.creative", "Incubators cannot be used in creative mode!");
        translationBuilder.add("message.overlay.daycareplus.incubator.stolen", "This is not your incubator!");
        translationBuilder.add("message.overlay.daycareplus.incubator.typeless", "This incubator has no tier data!");
        translationBuilder.add("message.overlay.daycareplus.incubator.claimed", "This is now your incubator.");
        translationBuilder.add("message.overlay.daycareplus.incubator.no_storage", "No egg storage found, open your incubator to bind it!");
        translationBuilder.add("tooltip.daycareplus.incubator.eggs_held", "Eggs Held: %1$s/%2$s");
        translationBuilder.add("gui.title.daycareplus.incubator", "Incubator");
        translationBuilder.add("gui.button.daycareplus.prev", "Previous");
        translationBuilder.add("gui.button.daycareplus.next", "Next");
        translationBuilder.add("gui.button.daycareplus.take", "Deposit All");

        // Egg Item
        translationBuilder.add("message.overlay.daycareplus.egg.hatch", "Your egg hatched.");
        translationBuilder.add("tooltip.daycareplus.egg.no_data", "No data found.");
        translationBuilder.add("tooltip.daycareplus.egg.ticks", "§eTime:§f %1$s");

        // Daycare
        translationBuilder.add("gui.title.daycareplus.daycare", "Daycare");
        translationBuilder.add("message.chat.daycareplus.not_daycare", "This daycare is inactive.");
        translationBuilder.add("message.chat.daycareplus.egg_produced", "Your daycare has produced an egg.");
        translationBuilder.add("message.chat.daycareplus.single_egg_produced", "Your daycare produced %1$s egg while you were away.");
        translationBuilder.add("message.chat.daycareplus.multiple_egg_produced", "Your daycare produced %1$s eggs while you were away.");
        translationBuilder.add("message.chat.daycareplus.move_learnt", "Your %1$s learnt %2$s while in the daycare.");
        translationBuilder.add("message.overlay.daycareplus.not_owner", "This is not your daycare.");
        translationBuilder.add("gui.button.daycareplus.twin_boosts_remaining", "Twin Boosts Remaining: %1$s");
        translationBuilder.add("gui.button.daycareplus.twin_boosts_info", "Each cycle produces an extra egg!");
        translationBuilder.add("gui.button.daycareplus.shiny_boosts_remaining", "Shiny Boosts Remaining: %1$s");
        translationBuilder.add("gui.button.daycareplus.shiny_boost_rate", "Non-shiny eggs have a %1$s%% chance to become shiny!");
        translationBuilder.add("gui.button.daycareplus.open_pasture", "Open Pasture");
        translationBuilder.add("gui.button.daycareplus.info", "Info");
        translationBuilder.add("gui.button.daycareplus.info.tooltip.1", "The daycare attempt to produce an egg periodically.");
        translationBuilder.add("gui.button.daycareplus.info.tooltip.2", "Eggs will still be produced when the pasture is unloaded or the owner is offline.");
        translationBuilder.add("gui.button.daycareplus.offspring", "Offspring");
        translationBuilder.add("gui.button.daycareplus.eggs_held", "%1$s/%2$s eggs held");
        translationBuilder.add("gui.button.daycareplus.no_parent", "No parent selected.");
        translationBuilder.add("gui.button.daycareplus.no_parent.tooltip", "Add a Pokémon to the pasture.");
        translationBuilder.add("gui.button.daycareplus.offspring.empty", "No preview available.");
        translationBuilder.add("gui.button.daycareplus.offspring.empty.tooltip", "Select two compatible Pokémon to view the preview.");
        translationBuilder.add("gui.button.daycareplus.parent", "Parent");
        translationBuilder.add("gui.button.daycareplus.no_item", "No breeding item held.");
        translationBuilder.add(CobblemonItems.EVERSTONE.getTranslationKey() + ".breeding", "This parent will pass on its nature to the child.");
        translationBuilder.add(CobblemonItems.DESTINY_KNOT.getTranslationKey() + ".breeding", "5 IVs are passed down from either parent instead of 3.");
        translationBuilder.add(CobblemonItems.POWER_LENS.getTranslationKey() + ".breeding", "This parent will pass on its Sp.Attack IV to the child.");
        translationBuilder.add(CobblemonItems.POWER_ANKLET.getTranslationKey() + ".breeding", "This parent will pass on its Speed IV to the child.");
        translationBuilder.add(CobblemonItems.POWER_BELT.getTranslationKey() + ".breeding", "This parent will pass on its Defence IV to the child.");
        translationBuilder.add(CobblemonItems.POWER_WEIGHT.getTranslationKey() + ".breeding", "This parent will pass on its HP IV to the child.");
        translationBuilder.add(CobblemonItems.POWER_BRACER.getTranslationKey() + ".breeding", "This parent will pass on its Attack IV to the child.");
        translationBuilder.add(CobblemonItems.POWER_BAND.getTranslationKey() + ".breeding", "This parent will pass on its Sp.Defence IV to the child.");
        translationBuilder.add(CobblemonItems.MIRROR_HERB.getTranslationKey() + ".breeding", "This Pokemon may learn egg moves from its partner.");

        // Intro GUI
        translationBuilder.add("gui.title.daycareplus.intro", "Daycare Setup");
        translationBuilder.add("gui.button.daycareplus.intro.daycare", "Daycare");
        translationBuilder.add("gui.button.daycareplus.intro.daycare.tooltip.1", "Use this pasture to breed Pokémon.");
        translationBuilder.add("gui.button.daycareplus.intro.daycare.tooltip.2", "Daycares Active: %1$s/%2$s");
        translationBuilder.add("gui.button.daycareplus.intro.pasture", "Pasture");
        translationBuilder.add("gui.button.daycareplus.intro.pasture.tooltip", "Use this pasture cosmetically without breeding.");
        translationBuilder.add("message.overlay.daycareplus.limit_reached", "You cannot activate anymore daycares, limit reached.");

        // Properties
        translationBuilder.add("property.daycareplus.species", "Species: ");
        translationBuilder.add("property.daycareplus.form", "Form: ");
        translationBuilder.add("property.daycareplus.ability", "Ability: ");
        translationBuilder.add("property.daycareplus.nature", "Nature: ");
        translationBuilder.add("property.daycareplus.gender", "Gender: ");
        translationBuilder.add("property.daycareplus.moves", "Egg Moves: ");
        translationBuilder.add("property.daycareplus.ivs", "IVs ");
        translationBuilder.add("property.daycareplus.hp", "HP: ");
        translationBuilder.add("property.daycareplus.attack", "Attack: ");
        translationBuilder.add("property.daycareplus.defence", "Defence: ");
        translationBuilder.add("property.daycareplus.special_attack", "Sp.Attack: ");
        translationBuilder.add("property.daycareplus.special_defence", "Sp.Defence: ");
        translationBuilder.add("property.daycareplus.speed", "Speed: ");
        translationBuilder.add("property.daycareplus.shiny", "Shiny Chance: ");
        translationBuilder.add("property.daycareplus.fertility", "Fertility: ");
        translationBuilder.add("property.daycareplus.unbreedable", "Unbreedable");

        // Stat Summary
        translationBuilder.add("daycareplus.ui.fertility.level", "Fertility");
        translationBuilder.add("daycareplus.ui.egg_group", "Egg Groups");

        // Stats
        translationBuilder.add("stat.daycareplus.eggs_hatched", "Pokémon Eggs Hatched");
        translationBuilder.add("stat.daycareplus.eggs_collected", "Pokémon Eggs Produced");

        // Egg Groups
        this.eggGroup(translationBuilder, EggGroup.AMORPHOUS, "Amorphous");
        this.eggGroup(translationBuilder, EggGroup.BUG, "Bug");
        this.eggGroup(translationBuilder, EggGroup.DRAGON, "Dragon");
        this.eggGroup(translationBuilder, EggGroup.FAIRY, "Fairy");
        this.eggGroup(translationBuilder, EggGroup.FIELD, "Field");
        this.eggGroup(translationBuilder, EggGroup.FLYING, "Flying");
        this.eggGroup(translationBuilder, EggGroup.GRASS, "Grass");
        this.eggGroup(translationBuilder, EggGroup.HUMAN_LIKE, "Human-Like");
        this.eggGroup(translationBuilder, EggGroup.MINERAL, "Mineral");
        this.eggGroup(translationBuilder, EggGroup.MONSTER, "Monster");
        this.eggGroup(translationBuilder, EggGroup.WATER_1, "Water 1");
        this.eggGroup(translationBuilder, EggGroup.WATER_2, "Water 2");
        this.eggGroup(translationBuilder, EggGroup.WATER_3, "Water 3");
        this.eggGroup(translationBuilder, EggGroup.UNDISCOVERED, "Undiscovered");
        this.eggGroup(translationBuilder, EggGroup.DITTO, "Ditto");

        // Menu
        translationBuilder.add("title.daycareplus.config", "Daycare+");
        translationBuilder.add("category.daycareplus.general", "General");
        translationBuilder.add("entry.daycareplus.display.feature", "Display Egg Groups In Summary");
        translationBuilder.add("entry.daycareplus.display.tooltip", "Show Egg Group Icon In Summary");
        translationBuilder.add("entry.daycareplus.display.pc", "Display Egg Group Panel In PC");
        translationBuilder.add("entry.daycareplus.display.pc.offset", "PC Egg Group Panel Y-Offset");
    }

    private void eggGroup (TranslationBuilder builder, EggGroup group, String name) {
        builder.add("daycareplus.group." + group.name().toLowerCase(Locale.ROOT), name);
    }
}
