package com.provismet.cobblemon.daycareplus.util;

import net.minecraft.text.Style;
import net.minecraft.util.Formatting;

import java.util.function.UnaryOperator;

public interface Styles {
    static UnaryOperator<Style> formattedNoItalics (Formatting formatting) {
        return style -> style.withItalic(false).withFormatting(formatting);
    }

    static UnaryOperator<Style> colouredNoItalics (int rgb) {
        return style -> style.withItalic(false).withColor(rgb);
    }

    UnaryOperator<Style> WHITE_NO_ITALICS = style -> style.withItalic(false).withFormatting(Formatting.WHITE);
    UnaryOperator<Style> GRAY_NO_ITALICS = style -> style.withItalic(false).withFormatting(Formatting.GRAY);

    int HP = 0x9AA8D5;
    int ATTACK = 0xECA34A;
    int DEFENCE = 0x50B54F;
    int SPECIAL_ATTACK = 0xF64708;
    int SPECIAL_DEFENCE = 0x8EC31E;
    int SPEED = 0x00B0EC;
}
