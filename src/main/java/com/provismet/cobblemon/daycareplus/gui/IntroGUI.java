package com.provismet.cobblemon.daycareplus.gui;

import com.cobblemon.mod.common.CobblemonItems;
import com.cobblemon.mod.common.CobblemonSounds;
import com.provismet.cobblemon.daycareplus.breeding.BreedingLink;
import com.provismet.cobblemon.daycareplus.config.DaycarePlusOptions;
import com.provismet.cobblemon.daycareplus.imixin.IMixinPastureBlockEntity;
import com.provismet.cobblemon.daycareplus.registries.DPItems;
import com.provismet.cobblemon.daycareplus.util.Styles;
import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.UUID;

public interface IntroGUI {
    static SimpleGui create (IMixinPastureBlockEntity pastureMixin, ServerPlayerEntity serverPlayer) {
        GuiElement filler = GuiElementBuilder.from(Items.GRAY_STAINED_GLASS_PANE.getDefaultStack())
            .hideTooltip()
            .hideDefaultTooltip()
            .build();

        GuiElement activateBreeding = GuiElementBuilder.from(DPItems.POKEMON_EGG.getDefaultStack())
            .hideDefaultTooltip()
            .setName(Text.translatable("gui.button.daycareplus.intro.daycare").styled(Styles.WHITE_NO_ITALICS))
            .addLoreLine(Text.translatable("gui.button.daycareplus.intro.daycare.tooltip.1").styled(Styles.GRAY_NO_ITALICS))
            .addLoreLine(Text.translatable("gui.button.daycareplus.intro.daycare.tooltip.2", BreedingLink.count(serverPlayer), DaycarePlusOptions.getMaxPasturesPerPlayer()).styled(Styles.GRAY_NO_ITALICS))
            .setCallback((index, type, action, gui) -> {
                if (BreedingLink.isAtLimit(gui.getPlayer())) {
                    gui.getPlayer().playSoundToPlayer(SoundEvents.ENTITY_VILLAGER_NO, SoundCategory.PLAYERS, 1f, 1f);
                    gui.getPlayer().sendMessage(Text.translatable("message.overlay.daycareplus.limit_reached").formatted(Formatting.RED));
                } else {
                    if (pastureMixin.getBreederUUID() == null) {
                        pastureMixin.setBreederUUID(UUID.randomUUID());
                    }
                    BreedingLink.add(serverPlayer, pastureMixin.getBreederUUID());
                    pastureMixin.setShouldBreed(true);
                    gui.close();
                }
            })
            .build();

        GuiElement noBreeding = GuiElementBuilder.from(CobblemonItems.PASTURE.getDefaultStack())
            .hideDefaultTooltip()
            .setName(Text.translatable("gui.button.daycareplus.intro.pasture").styled(Styles.WHITE_NO_ITALICS))
            .addLoreLine(Text.translatable("gui.button.daycareplus.intro.pasture.tooltip").styled(Styles.GRAY_NO_ITALICS))
            .setCallback((index, type, action, gui) -> {
                pastureMixin.setShouldBreed(false);
                pastureMixin.setSkipIntroDialogue(true);
                gui.close();
            })
            .build();

        SimpleGui gui = new SimpleGui(ScreenHandlerType.GENERIC_9X3, serverPlayer, false) {
            @Override
            public void onOpen () {
                this.player.playSoundToPlayer(CobblemonSounds.PC_ON, SoundCategory.BLOCKS, 1f, 1f);
            }

            @Override
            public void onClose () {
                this.player.playSoundToPlayer(CobblemonSounds.PC_OFF, SoundCategory.BLOCKS, 1f, 1f);
            }
        };
        gui.setTitle(Text.translatable("gui.title.daycareplus.intro"));

        for (int i = 0; i < gui.getVirtualSize(); ++i) {
            gui.setSlot(i, filler);
        }
        gui.setSlot(11, activateBreeding);
        gui.setSlot(15, noBreeding);
        return gui;
    }
}
