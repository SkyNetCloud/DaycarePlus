package com.provismet.cobblemon.daycareplus.config;

import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.provismet.cobblemon.daycareplus.DaycarePlusMain;
import com.provismet.lilylib.util.json.JsonBuilder;
import com.provismet.lilylib.util.json.JsonReader;
import net.fabricmc.loader.api.FabricLoader;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class DaycarePlusOptions {
    public static Path getConfigFolder () {
        Path directory = FabricLoader.getInstance().getConfigDir().resolve("daycareplus");
        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (IOException e) {
                throw new RuntimeException("Error creating Daycare+ config directory: ", e);
            }
        }
        return directory;
    }

    private static final Path FILE = getConfigFolder().resolve("server-config.json");
    private static final Path BLACKLIST_FILE = getConfigFolder().resolve("breeding_blacklist.json");

    // Egg Production
    private static long ticksPerEggAttempt = 12000;
    private static double successRatePerEggAttempt = 0.75;
    private static int pastureInventorySize = 128;
    private static int maxPasturesPerPlayer = 3;
    private static boolean showShinyChance = true;
    private static boolean allowHoppers = true;

    // Competitive Breeding
    private static boolean competitiveBreeding = false;
    private static boolean allowBreedingWithoutFertility = false;
    private static boolean consumeHeldItems = true;
    private static boolean eggsInheritFertility = true;
    private static int maxFertility = 8;

    // Egg Hatching
    private static int pointsPerEggCycle = 200;
    private static boolean showEggTooltip = true;

    // Shiny Chance
    private static float shinyChanceMultiplier = 1;
    private static float masudaMultiplier = 2;
    private static float crystalMultiplier = 1;
    private static boolean useShinyEvent = true;
    private static double shinyBoosterRate = 0.05;

    // Egg Moves
    private static boolean inheritEggMovesFromBothParents = true; // This is true in gen6+

    // Mod Compatibility
    private static boolean cobblemonSizeVariation = true;

    // Breeding Restrictions
    private static Set<PokemonProperties> breedingBlacklist = new HashSet<>();

    static {
        load();
        loadBreedingConfig();
    }

    public static long getTicksPerEggAttempt () {
        return ticksPerEggAttempt;
    }

    public static double getSuccessRatePerEggAttempt () {
        return successRatePerEggAttempt;
    }

    public static Set<PokemonProperties> getBreedingBlacklist () { return  breedingBlacklist; }

    public static int getPastureInventorySize () {
        return pastureInventorySize;
    }

    public static int getMaxPasturesPerPlayer () {
        return maxPasturesPerPlayer;
    }

    public static boolean shouldShowShinyChance () {
        return showShinyChance;
    }

    public static boolean shouldAllowHoppers () {
        return allowHoppers;
    }

    public static boolean doCompetitiveBreeding () {
        return competitiveBreeding;
    }

    public static boolean shouldAllowBreedingWithoutFertility () {
        return allowBreedingWithoutFertility;
    }

    public static boolean shouldConsumeHeldItems () {
        return consumeHeldItems;
    }

    public static boolean shouldEggsInheritFertility () {
        return eggsInheritFertility;
    }

    public static int getMaxFertility () {
        return maxFertility;
    }

    public static int getEggPoints (int eggCycles) {
        return pointsPerEggCycle * eggCycles;
    }

    public static boolean shouldShowEggTooltip () {
        return showEggTooltip;
    }

    public static boolean shouldUseShinyChanceEvent () {
        return useShinyEvent;
    }

    public static double getShinyBoosterRate () {
        return shinyBoosterRate;
    }

    public static float getShinyChanceMultiplier () {
        return shinyChanceMultiplier;
    }

    public static float getMasudaMultiplier () {
        return masudaMultiplier;
    }

    public static float getCrystalMultiplier () {
        return crystalMultiplier;
    }

    public static boolean doGen6EggMoves () {
        return inheritEggMovesFromBothParents;
    }

    public static boolean doCobblemonSizeVariationCompatibility () {
        return cobblemonSizeVariation;
    }

    public static void save () {
        JsonBuilder builder = new JsonBuilder()
            .append(
                "egg_production", new JsonBuilder()
                    .append("ticks_per_egg_attempt", ticksPerEggAttempt)
                    .append("success_rate_per_egg_attempt", successRatePerEggAttempt)
                    .append("pasture_inventory_size", pastureInventorySize)
                    .append("max_pastures_per_player", maxPasturesPerPlayer)
                    .append("show_shiny_chance", showShinyChance)
                    .append("allow_hoppers", allowHoppers))
            .append(
                "competitive_mode", new JsonBuilder()
                    .append("use_competitive_mode", competitiveBreeding)
                    .append("allow_breeding_without_fertility", allowBreedingWithoutFertility)
                    .append("consume_held_items", consumeHeldItems)
                    .append("eggs_inherit_fertility", eggsInheritFertility)
                    .append("max_fertility", maxFertility))
            .append(
                "shiny_chance", new JsonBuilder()
                    .append("use_event_trigger", useShinyEvent)
                    .append("standard_multiplier", shinyChanceMultiplier)
                    .append("masuda_multiplier", masudaMultiplier)
                    .append("crystal_multiplier", crystalMultiplier)
                    .append("shiny_booster_rate", shinyBoosterRate))
            .append(
                "breeding_rules", new JsonBuilder()
                    .append("inherit_moves_from_both_parents", inheritEggMovesFromBothParents)
                    .append("ticks_per_egg_cycle", pointsPerEggCycle)
                    .append("show_egg_tooltip", showEggTooltip))
            .append(
                "compatibility_features", new JsonBuilder()
                    .append("CobblemonSizeVariation", cobblemonSizeVariation));

        try (FileWriter writer = new FileWriter(FILE.toFile())) {
            writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(builder.getJson()));
        }
        catch (IOException e) {
            DaycarePlusMain.LOGGER.error("Error whilst saving Daycare+ server-config.json: ", e);
        }
    }

    public static void load () {
        if (!FILE.toFile().exists()) {
            save();
            return;
        }

        try {
            JsonReader reader = JsonReader.file(FILE.toFile());
            if (reader != null) {
                reader.getObjectAsReader("egg_production").ifPresent(eggProduction -> {
                    eggProduction.getInteger("ticks_per_egg_attempt").ifPresent(val -> ticksPerEggAttempt = val);
                    eggProduction.getDouble("success_rate_per_egg_attempt").ifPresent(val -> successRatePerEggAttempt = val);
                    eggProduction.getInteger("pasture_inventory_size").ifPresent(val -> pastureInventorySize = val);
                    eggProduction.getInteger("max_pastures_per_player").ifPresent(val -> maxPasturesPerPlayer = val);
                    eggProduction.getBoolean("show_shiny_chance").ifPresent(val -> showShinyChance = val);
                    eggProduction.getBoolean("allow_hoppers").ifPresent(val -> allowHoppers = val);
                });

                reader.getObjectAsReader("competitive_mode").ifPresent(competitiveMode -> {
                    competitiveMode.getBoolean("use_competitive_mode").ifPresent(val -> competitiveBreeding = val);
                    competitiveMode.getBoolean("allow_breeding_without_fertility").ifPresent(val -> allowBreedingWithoutFertility = val);
                    competitiveMode.getBoolean("consume_held_items").ifPresent(val -> consumeHeldItems = val);
                    competitiveMode.getBoolean("eggs_inherit_fertility").ifPresent(val -> eggsInheritFertility = val);
                    competitiveMode.getInteger("max_fertility").ifPresent(val -> maxFertility = val);
                });

                reader.getObjectAsReader("shiny_chance").ifPresent(shinyChance -> {
                    shinyChance.getBoolean("use_event_trigger").ifPresent(val -> useShinyEvent = val);
                    shinyChance.getFloat("standard_multiplier").ifPresent(val -> shinyChanceMultiplier = val);
                    shinyChance.getFloat("masuda_multiplier").ifPresent(val -> masudaMultiplier = val);
                    shinyChance.getFloat("crystal_multiplier").ifPresent(val -> crystalMultiplier = val);
                    shinyChance.getDouble("shiny_booster_rate").ifPresent(val -> shinyBoosterRate = val);
                });

                reader.getObjectAsReader("breeding_rules").ifPresent(breedingRules -> {
                    breedingRules.getBoolean("inherit_moves_from_both_parents").ifPresent(val -> inheritEggMovesFromBothParents = val);
                    breedingRules.getInteger("ticks_per_egg_cycle").ifPresent(val -> pointsPerEggCycle = val);
                    breedingRules.getBoolean("show_egg_tooltip").ifPresent(val -> showEggTooltip = val);
                });

                reader.getObjectAsReader("compatibility").ifPresent(compatibility -> {
                    compatibility.getBoolean("cobblemonsizevariation").ifPresent(val -> cobblemonSizeVariation = val);
                });
            }
        }
        catch (FileNotFoundException e) {
            DaycarePlusMain.LOGGER.info("No Daycare+ server config found, creating default.");
        }
        catch (Exception e) {
            DaycarePlusMain.LOGGER.error("Error reading Daycare+ server-config.json: ", e);
        }
        save();
    }

    private static void loadBreedingConfig() {
        if (!BLACKLIST_FILE.toFile().exists()) {
            saveBreedingConfig();
            return;
        }

        try {
            JsonReader reader = JsonReader.file(BLACKLIST_FILE.toFile());
            if (reader != null) {
                reader.getArray("breeding_blacklist").ifPresent(list -> {
                    breedingBlacklist = new HashSet<>();
                    for (JsonElement element : list){
                        if (element instanceof JsonPrimitive primitive && primitive.isString()){
                            breedingBlacklist.add(PokemonProperties.Companion.parse(primitive.getAsString()));
                            try {
                                breedingBlacklist.add(PokemonProperties.Companion.parse(primitive.getAsString()));
                            } catch (Exception e) {
                                DaycarePlusMain.LOGGER.error("Failed to parse breeding blacklist property: {}", primitive.getAsString(), e);
                            }
                        }
                    }
                });
            }
        }
        catch (FileNotFoundException e) {
            DaycarePlusMain.LOGGER.info("No breeding restrictions config found, creating default.");
        }
        catch (Exception e) {
            DaycarePlusMain.LOGGER.error("Error reading breeding restrictions config: ", e);
        }
        saveBreedingConfig();
    }

    public static void reloadBlacklist(){
        loadBreedingConfig();
    }

    private static void saveBreedingConfig() {
        JsonBuilder builder = new JsonBuilder();

        JsonArray blacklistArray = new JsonArray();
        for (PokemonProperties propertyString : breedingBlacklist) {
            blacklistArray.add(propertyString.asString(" "));
        }
        builder.append("breeding_blacklist", blacklistArray);

        try (FileWriter writer = new FileWriter(BLACKLIST_FILE.toFile())) {
            writer.write(new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(builder.getJson()));
        }
        catch (IOException e) {
            DaycarePlusMain.LOGGER.error("Error whilst saving breeding restrictions config: ", e);
        }
    }
}
