package com.provismet.cobblemon.daycareplus.config.menu;

import com.provismet.cobblemon.daycareplus.config.ClientOptions;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class DPConfigMenu {
    public static Screen build (Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create();
        builder.setParentScreen(parent);
        builder.setTitle(Text.translatable("title.daycareplus.config"));
        builder.setSavingRunnable(ClientOptions::save);

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory general = builder.getOrCreateCategory(Text.translatable("category.daycareplus.general"));

        general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("entry.daycareplus.display.feature"), ClientOptions.shouldShowEggGroupsFeature())
            .setDefaultValue(true)
            .setSaveConsumer(ClientOptions::setShowEggGroupFeature)
            .build()
        );

        general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("entry.daycareplus.display.tooltip"), ClientOptions.shouldShowEggGroupsTooltip())
            .setDefaultValue(false)
            .setSaveConsumer(ClientOptions::setShowEggGroupsTooltip)
            .build()
        );

        general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("entry.daycareplus.display.pc"), ClientOptions.shouldShowEggGroupsInPC())
            .setDefaultValue(true)
            .setSaveConsumer(ClientOptions::setShowEggGroupsInPC)
            .build()
        );

        general.addEntry(entryBuilder.startIntSlider(Text.translatable("entry.daycareplus.display.pc.offset"), ClientOptions.getPcEggGroupPanelYOffset(), -100, 100)
            .setDefaultValue(68)
            .setSaveConsumer(ClientOptions::setPcEggGroupPanelYOffset)
            .build()
        );

        return builder.build();
    }
}
