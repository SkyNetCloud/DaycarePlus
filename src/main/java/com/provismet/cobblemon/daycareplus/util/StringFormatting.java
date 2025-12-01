package com.provismet.cobblemon.daycareplus.util;

import java.util.Locale;

public interface StringFormatting {
    static String titleCase (String input) {
        if (input == null) return "";
        return input.substring(0, 1).toUpperCase(Locale.ROOT) + input.substring(1).toLowerCase(Locale.ROOT);
    }
}
