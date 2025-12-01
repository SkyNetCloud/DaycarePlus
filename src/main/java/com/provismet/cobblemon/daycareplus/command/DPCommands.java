package com.provismet.cobblemon.daycareplus.command;

import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.command.argument.PokemonPropertiesArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.provismet.cobblemon.daycareplus.api.DaycarePlusEvents;
import com.provismet.cobblemon.daycareplus.breeding.BreedingLink;
import com.provismet.cobblemon.daycareplus.config.DaycarePlusOptions;
import com.provismet.cobblemon.daycareplus.config.IncubatorTiers;
import com.provismet.cobblemon.daycareplus.registries.DPItems;
import com.provismet.cobblemon.daycareplus.storage.EggStorage;
import com.provismet.cobblemon.daycareplus.storage.IncubatorCollection;
import net.minecraft.command.argument.EntityArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.Map;
import java.util.Optional;

public class DPCommands {
    public static void register () {
        CommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess, registrationEnvironment) -> commandDispatcher.register(
                CommandManager.literal("daycareplus")
                        .then(CommandManager.literal("clear")
                                .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
                                .then(CommandManager.argument("player", EntityArgumentType.player())
                                        .executes(context -> {
                                            ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
                                            BreedingLink.remove(player);
                                            context.getSource().sendFeedback(() -> Text.of("Cleared daycare memory of " + player.getName().getString()), false);
                                            return 1;
                                        })
                                )
                        )
                        .then(CommandManager.literal("reload")
                                .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
                                .executes(context -> {
                                    DaycarePlusOptions.reloadBlacklist();
                                    context.getSource().sendFeedback(() -> Text.of("Reload Blacklist file"), false);
                                    return 1;
                                })
                        )
                        .then(CommandManager.literal("incubator")
                                .then(CommandManager.literal("data")
                                        .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
                                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                                .executes(DPCommands::getIncubators)
                                        )
                                )
                                .then(CommandManager.literal("modify")
                                        .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
                                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                                .then(CommandManager.argument("type", StringArgumentType.word())
                                                        .suggests((context, builder) -> {
                                                            ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
                                                            IncubatorCollection collection = IncubatorCollection.getOrCreate(player);
                                                            for (Map.Entry<String, EggStorage> entry : collection) {
                                                                builder.suggest(entry.getKey());
                                                            }
                                                            return builder.buildFuture();
                                                        })
                                                        .then(CommandManager.argument("tier", StringArgumentType.word())
                                                                .suggests((context, builder) -> {
                                                                    for (String tier : IncubatorTiers.getTiers()) {
                                                                        builder.suggest(tier);
                                                                    }
                                                                    return builder.buildFuture();
                                                                })
                                                                .executes(DPCommands::modifyIncubator)
                                                        )
                                                )
                                        )
                                )
                        )
                        .then(CommandManager.literal("give")
                                .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
                                .then(CommandManager.argument("player", EntityArgumentType.player())
                                        .then(CommandManager.argument("properties", PokemonPropertiesArgumentType.Companion.properties())
                                                .executes(context -> {
                                                    ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
                                                    PokemonProperties properties = PokemonPropertiesArgumentType.Companion.getPokemonProperties(context, "properties");
                                                    ItemStack egg = DPItems.POKEMON_EGG.createEggItem(properties);
                                                    DaycarePlusEvents.POST_EGG_PRODUCED.invoker().afterItemCreated(egg); // Skip the pre event for the command. Don't let the property get edited.
                                                    player.giveItemStack(egg);
                                                    context.getSource().sendFeedback(() -> Text.of("Gave 1 Pokemon Egg to " + player.getName().getString()), false);
                                                    return 1;
                                                })
                                        )
                                )
                        )
        ));
    }

    private static int modifyIncubator (CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
        String type = StringArgumentType.getString(context, "type");
        String tier = StringArgumentType.getString(context, "tier");

        IncubatorCollection collection = IncubatorCollection.getOrCreate(player);
        Optional<EggStorage> storage = collection.get(type);
        Optional<IncubatorTiers.IncubatorSettings> newSettings = IncubatorTiers.get(tier);

        if (newSettings.isEmpty()) {
            context.getSource().sendFeedback(() -> Text.of("Invalid tier provided: " + tier), false);
            return 0;
        }

        if (storage.isPresent()) {
            storage.get().setTier(tier);
            storage.get().setCapacity(newSettings.get().capacity());
            storage.get().setEggsToTick(newSettings.get().eggsToTick());
        }
        else {
            collection.put(type, EggStorage.fromSettings(tier));
        }
        context.getSource().sendFeedback(() -> Text.literal("Modified incubator for ").append(player.getName()), true);
        return 1;
    }

    private static int getIncubators (CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");

        IncubatorCollection collection = IncubatorCollection.getCollection(player.getUuidAsString());
        if (collection == null) {
            context.getSource().sendFeedback(() -> Text.literal("No incubators found for ").append(player.getName()), false);
            return 0;
        }

        MutableText text = Text.literal("Found incubators for ").append(player.getName()).append(": ");
        for (Map.Entry<String, EggStorage> entry : collection) {
            text = text.append("{" + entry.getKey() + ", eggs: " + entry.getValue().size() + ", capacity: " + entry.getValue().getCapacity() + ", ticking eggs: " + entry.getValue().getEggsToTick() + "} ");
        }

        context.getSource().sendFeedback(text::copy, false);
        return 1;
    }

}
