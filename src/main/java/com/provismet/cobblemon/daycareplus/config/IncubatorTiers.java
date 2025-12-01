package com.provismet.cobblemon.daycareplus.config;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.provismet.cobblemon.daycareplus.DaycarePlusMain;
import com.provismet.lilylib.util.json.JsonBuilder;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class IncubatorTiers {
    private static final Path FILE = DaycarePlusOptions.getConfigFolder().resolve("incubators.json");
    private static final Map<String, IncubatorSettings> settings = new HashMap<>();

    // Incubator defaults
    private static final IncubatorSettings COPPER = new IncubatorSettings(8, 1);
    private static final IncubatorSettings IRON = new IncubatorSettings(64, 2);
    private static final IncubatorSettings GOLD = new IncubatorSettings(32, 32);
    private static final IncubatorSettings DIAMOND = new IncubatorSettings(96, 4);
    private static final IncubatorSettings NETHERITE = new IncubatorSettings(128, 8);

    public static Optional<IncubatorSettings> get (String tier) {
        return Optional.ofNullable(settings.get(tier));
    }

    public static Set<String> getTiers () {
        return settings.keySet();
    }

    public static void load () {
        settings.putIfAbsent("copper", COPPER);
        settings.putIfAbsent("iron", IRON);
        settings.putIfAbsent("gold", GOLD);
        settings.putIfAbsent("diamond", DIAMOND);
        settings.putIfAbsent("netherite", NETHERITE);

        if (!FILE.toFile().exists()) {
            save();
        }

        try {
            JsonElement element = JsonParser.parseReader(new FileReader(FILE.toFile()));
            if (!(element instanceof JsonObject json)) {
                save();
                return;
            }

            for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                if (entry.getValue() instanceof JsonObject jsonObject) {
                    settings.put(entry.getKey(), IncubatorSettings.fromJson(jsonObject));
                }
            }
        }
        catch (FileNotFoundException e) {
            DaycarePlusMain.LOGGER.info("No Daycare+ incubator config found, creating default.");
        }
        catch (Exception e) {
            DaycarePlusMain.LOGGER.error("Error reading Daycare+ incubator config: ", e);
        }
        save();
    }

    public static void save () {
        JsonBuilder builder = new JsonBuilder();

        for (Map.Entry<String, IncubatorSettings> entry : settings.entrySet()) {
            builder.append(entry.getKey(), entry.getValue().toJson());
        }

        try (FileWriter writer = new FileWriter(FILE.toFile())) {
            writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(builder.getJson()));
        }
        catch (IOException e) {
            DaycarePlusMain.LOGGER.error("Error whilst saving config: ", e);
        }
    }

    public record IncubatorSettings (int capacity, int eggsToTick) {
        public static IncubatorSettings fromJson (JsonObject json) {
            int capacity = 1;
            int eggs = 1;
            if (json.has("capacity")) {
                capacity = json.getAsJsonPrimitive("capacity").getAsInt();
            }
            if (json.has("eggs_to_tick_simultaneously")) {
                eggs = json.getAsJsonPrimitive("eggs_to_tick_simultaneously").getAsInt();
            }
            return new IncubatorSettings(capacity, eggs);
        }

        public JsonObject toJson () {
            JsonObject json = new JsonObject();
            json.addProperty("capacity", this.capacity);
            json.addProperty("eggs_to_tick_simultaneously", this.eggsToTick);
            return json;
        }
    }
}
