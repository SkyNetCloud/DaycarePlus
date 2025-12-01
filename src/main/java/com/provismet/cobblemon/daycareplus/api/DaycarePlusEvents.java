package com.provismet.cobblemon.daycareplus.api;

import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.item.ItemStack;

public interface DaycarePlusEvents {
    /**
     * This event is called on the PokemonProperties object right before it is serialised into an egg item.
     */
    Event<EggProduced.Pre> PRE_EGG_PRODUCED = EventFactory.createArrayBacked(EggProduced.Pre.class, listeners -> properties -> {
        for (EggProduced.Pre event : listeners) {
            event.beforeItemCreated(properties);
        }
    });

    /**
     * This event is called on the egg item right after it has been created.
     * <p>
     * Also executes whenever PokemonEgg data is serialised back into item form.
     */
    Event<EggProduced.Post> POST_EGG_PRODUCED = EventFactory.createArrayBacked(EggProduced.Post.class, listeners -> stack -> {
        for (EggProduced.Post event : listeners) {
            event.afterItemCreated(stack);
        }
    });

    Event<EggPropertiesCreated> EGG_PROPERTIES_CREATED = EventFactory.createArrayBacked(EggPropertiesCreated.class, listeners -> (primary, secondary, properties) -> {
        for (EggPropertiesCreated event : listeners) {
            event.modifyProperties(primary, secondary, properties);
        }
    });

    interface EggProduced {
        @FunctionalInterface
        interface Pre {
            void beforeItemCreated (PokemonProperties properties);
        }

        @FunctionalInterface
        interface Post {
            void afterItemCreated (ItemStack stack);
        }
    }

    @FunctionalInterface
    interface EggPropertiesCreated {
        void modifyProperties (Pokemon primary, Pokemon secondary, PokemonProperties properties);
    }
}
