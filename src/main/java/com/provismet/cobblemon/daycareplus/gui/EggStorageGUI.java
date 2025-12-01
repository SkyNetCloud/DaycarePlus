package com.provismet.cobblemon.daycareplus.gui;

import com.provismet.cobblemon.daycareplus.registries.DPIconItems;
import com.provismet.cobblemon.daycareplus.storage.EggStorage;
import com.provismet.cobblemon.daycareplus.storage.IncubatorCollection;
import com.provismet.cobblemon.daycareplus.util.Styles;
import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.util.Optional;

public class EggStorageGUI extends SimpleGui {
    private static final int ITEMS_PER_ROW = 9;
    private static final int ROWS_PER_PAGE = 5;
    private static final int ITEMS_PER_PAGE = ITEMS_PER_ROW * ROWS_PER_PAGE;

    private final EggStorage storage;
    private int minSlotDisplayed;
    private boolean isAtEnd;

    protected EggStorageGUI (ServerPlayerEntity player, EggStorage storage) {
        super(ScreenHandlerType.GENERIC_9X6, player, false);
        this.storage = storage;
    }

    public static EggStorageGUI create (ServerPlayerEntity player, String storageName) {
        Optional<EggStorage> storage = IncubatorCollection.getOrCreate(player).get(storageName);
        if (storage.isPresent()) {
            EggStorageGUI gui = new EggStorageGUI(player, storage.get());
            gui.updateBorder();
            gui.loadPage();
            gui.setTitle(Text.translatable("gui.title.daycareplus.incubator"));
            return gui;
        }

        return null;
    }

    private void updateBorder () {
        GuiElement borderFiller = GuiElementBuilder.from(Items.BLACK_STAINED_GLASS_PANE.getDefaultStack())
            .hideDefaultTooltip()
            .hideTooltip()
            .build();

        for (int i = 0; i < ITEMS_PER_ROW; ++i) {
            this.setSlot(i, borderFiller);
        }

        GuiElement prev = GuiElementBuilder.from(DPIconItems.LEFT.getDefaultStack())
            .setName(Text.translatable("gui.button.daycareplus.prev").styled(Styles.WHITE_NO_ITALICS))
            .setCallback((index, clickType, action, gui) -> this.previousPage())
            .build();

        GuiElement next = GuiElementBuilder.from(DPIconItems.RIGHT.getDefaultStack())
            .setName(Text.translatable("gui.button.daycareplus.next").styled(Styles.WHITE_NO_ITALICS))
            .setCallback((index, clickType, action, gui) -> this.nextPage())
            .build();

        GuiElement take = GuiElementBuilder.from(DPIconItems.TAKE_ALL.getDefaultStack())
            .setName(Text.translatable("gui.button.daycareplus.take").styled(Styles.WHITE_NO_ITALICS))
            .setCallback((index, type1, action, gui) -> {
                this.player.getInventory().main.forEach(this.storage::addCopyAndEmpty);
                this.player.playSoundToPlayer(SoundEvents.ITEM_BUNDLE_INSERT, SoundCategory.PLAYERS, 1f, 1f);
                this.loadPage();
            })
            .build();

        this.setSlot(0, prev);
        this.setSlot(7, take);
        this.setSlot(8, next);
    }

    private void loadPage () {
        for (int i = 0; i + 9 < this.getVirtualSize(); ++i) {
            int storageIndex = this.minSlotDisplayed + i;
            GuiElement eggButton;

            if (storageIndex < this.storage.size()) {
                eggButton = GuiElementBuilder.from(this.storage.getItem(this.minSlotDisplayed + i))
                    .setCallback((index, clickType, action, gui) -> {
                        ItemStack stack = this.storage.getItem(storageIndex);
                        if (!stack.isEmpty() && this.player.giveItemStack(stack)) {
                            this.storage.remove(storageIndex);
                            this.player.playSoundToPlayer(SoundEvents.ITEM_BUNDLE_REMOVE_ONE, SoundCategory.PLAYERS, 1f, 1f);
                            this.loadPage();
                        }
                    })
                    .build();
            }
            else {
                eggButton = GuiElementBuilder.from(ItemStack.EMPTY).build();
            }

            this.setSlot(i + 9, eggButton);
        }

        this.isAtEnd = this.minSlotDisplayed + ITEMS_PER_PAGE > this.storage.size();
    }

    private void nextPage () {
        if (!isAtEnd) {
            minSlotDisplayed += ITEMS_PER_PAGE;
            this.loadPage();
        }
    }

    private void previousPage () {
        if (this.minSlotDisplayed > 0) {
            this.minSlotDisplayed = Math.max(0, this.minSlotDisplayed - ITEMS_PER_PAGE);
            this.loadPage();
        }
    }

    @Override
    public boolean onAnyClick (int index, ClickType type, SlotActionType action) {
        if (index >= 54) {
            int inventoryIndex;
            if (index >= 81) inventoryIndex = index - 81; // Hotbar
            else inventoryIndex = index - 45; // Main - Hotbar is first in the list (54 - 9).

            if (inventoryIndex < this.player.getInventory().main.size()) {
                this.storage.addCopyAndEmpty(this.player.getInventory().main.get(inventoryIndex));
            }
            this.loadPage();
        }

        return super.onAnyClick(index, type, action);
    }

    @Override
    public void onTick () {
        if (this.player.age % 20 == 1) { // Reload one tick after the eggs have ticked.
            this.loadPage();
        }
    }
}
