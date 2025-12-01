package com.provismet.cobblemon.daycareplus.imixin;

import com.provismet.cobblemon.daycareplus.breeding.PastureContainer;
import com.provismet.cobblemon.daycareplus.breeding.PastureExtension;
import eu.pb4.sgui.api.elements.GuiElement;

import java.util.UUID;

public interface IMixinPastureBlockEntity extends PastureContainer {
    PastureExtension getExtension ();
    void setExtension (PastureExtension extension);

    void setShouldBreed (boolean shouldBreed);
    boolean shouldBreed ();

    void setSkipIntroDialogue (boolean skipIntro);
    boolean shouldSkipIntro ();

    void setShouldSkipDaycareGUI (boolean skipGUI);
    boolean shouldSkipDaycareGUI ();

    UUID getBreederUUID ();
    void setBreederUUID (UUID uuid);

    GuiElement getEggCounterButton ();
    GuiElement getTwinBoostCounterButton ();
    GuiElement getShinyBoostCounterButton ();
}
