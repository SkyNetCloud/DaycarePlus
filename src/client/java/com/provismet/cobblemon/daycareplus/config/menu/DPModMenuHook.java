package com.provismet.cobblemon.daycareplus.config.menu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.loader.api.FabricLoader;

public class DPModMenuHook implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory () {
        if (FabricLoader.getInstance().isModLoaded("cloth-config")) {
            return DPConfigMenu::build;
        }
        else return parent -> null;
    }
}
