package com.provismet.cobblemon.daycareplus.util;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

import java.util.List;

public class MathExtras {
    public static <T> T randomChoice (List<T> elements, Random random) {
        if (elements == null || elements.isEmpty()) return null;
        if (elements.size() == 1) return elements.getFirst();

        int index = random.nextBetween(0, elements.size() - 1);
        return elements.get(index);
    }

    public static <T> T randomChoice (List<T> elements) {
        if (elements == null || elements.isEmpty()) return null;
        if (elements.size() == 1) return elements.getFirst();

        double random = Math.random();
        int index = MathHelper.clamp((int)(random * elements.size()), 0, elements.size() - 1);
        return elements.get(index);
    }
}
