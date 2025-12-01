package com.provismet.cobblemon.daycareplus.storage;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.provismet.cobblemon.daycareplus.DaycarePlusMain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.WorldSavePath;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

public class IncubatorCollection implements Iterable<Map.Entry<String, EggStorage>> {
    public static final Codec<IncubatorCollection> CODEC = Codec.unboundedMap(Codec.STRING, EggStorage.CODEC).xmap(IncubatorCollection::new, IncubatorCollection::getStorageMap);
    private static final Map<String, IncubatorCollection> playerMap = new HashMap<>();
    private static final WorldSavePath INCUBATOR_SAVE_PATH = new WorldSavePath("incubators");

    private final Map<String, EggStorage> storageMap;

    public IncubatorCollection (Map<String, EggStorage> storages) {
        this.storageMap = new HashMap<>(storages); // Force this to always be mutable.
    }

    public static IncubatorCollection getOrCreate (PlayerEntity player) {
        return playerMap.computeIfAbsent(player.getUuidAsString(), string -> new IncubatorCollection(Map.of()));
    }

    public static IncubatorCollection getCollection (String uuidString) {
        return playerMap.get(uuidString);
    }

    public void put (String label, EggStorage storage) {
        this.storageMap.put(label, storage);
    }

    public Optional<EggStorage> get (String label) {
        return Optional.ofNullable(this.storageMap.get(label));
    }

    @NotNull
    @Override
    public Iterator<Map.Entry<String, EggStorage>> iterator () {
        return this.storageMap.entrySet().iterator();
    }

    public void saveToJson (MinecraftServer server, String ownerUUID) {
        Path savePath = server.getSavePath(INCUBATOR_SAVE_PATH).resolve(ownerUUID + ".json");
        DataResult<JsonElement> result = CODEC.encodeStart(JsonOps.INSTANCE, this);
        result.ifSuccess(json -> {
            String jsonString = new GsonBuilder().setPrettyPrinting().create().toJson(json);

            try {
                Files.createDirectories(savePath.getParent());
                try (FileWriter writer = new FileWriter(savePath.toFile())) {
                    writer.write(jsonString);
                }
            } catch (IOException e) {
                DaycarePlusMain.LOGGER.error("Failed to save incubator data for uuid: {}", ownerUUID);
                DaycarePlusMain.LOGGER.error("Incubator JSON: {}", jsonString);
                DaycarePlusMain.LOGGER.error("Stack Trace: ", e);
            }
        });
    }

    public static void loadFromJson (PlayerEntity owner) {
        if (!(owner.getWorld() instanceof ServerWorld world)) return;

        Path savePath = world.getServer().getSavePath(INCUBATOR_SAVE_PATH).resolve(owner.getUuidAsString() + ".json");
        try {
            JsonElement json = JsonParser.parseReader(new FileReader(savePath.toFile()));
            DataResult<Pair<IncubatorCollection, JsonElement>> result = CODEC.decode(JsonOps.INSTANCE, json);
            IncubatorCollection collection;

            if (result.isSuccess()) {
                collection = result.getOrThrow().getFirst();
            }
            else {
                collection = new IncubatorCollection(Map.of());
            }
            playerMap.putIfAbsent(owner.getUuidAsString(), collection);
        }
        catch (FileNotFoundException ignored) {

        }
        catch (Exception e) {
            DaycarePlusMain.LOGGER.error("Failed to read incubator file {}", savePath.toAbsolutePath());
            DaycarePlusMain.LOGGER.error("Stack Trace: ", e);
        }
    }

    public static void remove (MinecraftServer server, String ownerUUID) {
        IncubatorCollection collection = getCollection(ownerUUID);
        if (collection != null) {
            playerMap.remove(ownerUUID);
            collection.saveToJson(server, ownerUUID);
        }
    }

    public static void saveAll (MinecraftServer server) {
        for (Map.Entry<String, IncubatorCollection> entry : playerMap.entrySet()) {
            entry.getValue().saveToJson(server, entry.getKey());
        }
    }

    private Map<String, EggStorage> getStorageMap () {
        return this.storageMap;
    }
}
