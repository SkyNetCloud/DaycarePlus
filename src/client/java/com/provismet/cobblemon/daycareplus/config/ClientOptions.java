package com.provismet.cobblemon.daycareplus.config;

import com.google.gson.GsonBuilder;
import com.provismet.cobblemon.daycareplus.DaycarePlusMain;
import com.provismet.lilylib.util.json.JsonBuilder;
import com.provismet.lilylib.util.json.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public abstract class ClientOptions {
    private static final Path FILE = DaycarePlusOptions.getConfigFolder().resolve("client-config.json");

    private static boolean showEggGroupFeature = true;
    private static boolean showEggGroupsInPC = true;
    private static int pcEggGroupPanelYOffset = 68;
    private static boolean showEggGroupsTooltip = false;

    public static boolean shouldShowEggGroupsFeature () {
        return showEggGroupFeature;
    }

    public static boolean shouldShowEggGroupsInPC () {
        return showEggGroupsInPC;
    }

    public static boolean shouldShowEggGroupsTooltip () {
        return showEggGroupsTooltip;
    }

    public static int getPcEggGroupPanelYOffset () {
        return pcEggGroupPanelYOffset;
    }

    public static void setShowEggGroupFeature (boolean value) {
        showEggGroupFeature = value;
    }

    public static void setShowEggGroupsInPC (boolean value) {
        showEggGroupsInPC = value;
    }

    public static void setShowEggGroupsTooltip (boolean value) {
        showEggGroupsTooltip = value;
    }

    public static void setPcEggGroupPanelYOffset (int offset) {
        pcEggGroupPanelYOffset = offset;
    }

    public static void load () {
        if (!FILE.toFile().canRead()) {
            save();
            return;
        }

        try {
            JsonReader reader = JsonReader.file(FILE.toFile());
            if (reader == null) {
                save();
                return;
            }

            reader.getBoolean("show_egg_groups_in_summary").ifPresent(val -> showEggGroupFeature = val);
            reader.getBoolean("show_egg_groups_in_pc").ifPresent(val -> showEggGroupsInPC = val);
            reader.getBoolean("show_egg_group_tooltip_button").ifPresent(val -> showEggGroupsTooltip = val);
            reader.getInteger("pc_egg_group_y-offset").ifPresent(val -> pcEggGroupPanelYOffset = val);
        }
        catch (FileNotFoundException e) {
            DaycarePlusMain.LOGGER.info("No Daycare+ client config found, creating default.");
        }
        catch (Exception e) {
            DaycarePlusMain.LOGGER.error("Error reading Daycare+ client-config.json: ", e);
        }

        save();
    }

    public static void save () {
        JsonBuilder builder = new JsonBuilder()
            .append("show_egg_groups_in_summary", showEggGroupFeature)
            .append("show_egg_groups_in_pc", showEggGroupsInPC)
            .append("show_egg_group_tooltip_button", showEggGroupsTooltip)
            .append("pc_egg_group_y-offset", pcEggGroupPanelYOffset);

        try (FileWriter writer = new FileWriter(FILE.toFile())) {
            writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(builder.getJson()));
        }
        catch (IOException e) {
            DaycarePlusMain.LOGGER.error("Error whilst saving Daycare+ client-config.json: ", e);
        }
    }
}
