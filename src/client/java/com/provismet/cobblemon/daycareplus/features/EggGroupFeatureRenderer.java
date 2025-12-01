package com.provismet.cobblemon.daycareplus.features;

import com.cobblemon.mod.common.api.gui.GuiUtilsKt;
import com.cobblemon.mod.common.api.pokemon.feature.IntSpeciesFeature;
import com.cobblemon.mod.common.client.CobblemonResources;
import com.cobblemon.mod.common.client.gui.summary.featurerenderers.BarSummarySpeciesFeatureRenderer;
import com.cobblemon.mod.common.client.render.RenderHelperKt;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.util.MiscUtilsKt;
import com.provismet.cobblemon.daycareplus.DaycarePlusMain;
import com.provismet.cobblemon.daycareplus.config.ClientOptions;
import com.provismet.cobblemon.daycareplus.feature.BreedableProperty;
import com.provismet.cobblemon.daycareplus.util.ClientEggGroup;
import com.provismet.cobblemon.daycareplus.util.DPResources;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class EggGroupFeatureRenderer extends BarSummarySpeciesFeatureRenderer {
    private static final String NAME = "egg group";
    private static final String TRANSLATION = "daycareplus.ui.egg_group";
    private static final Identifier UNDERLAY = DaycarePlusMain.identifier("textures/gui/summary/stat_summary_egg_group_base.png");

    public EggGroupFeatureRenderer (Pokemon pokemon) {
        super(NAME, Text.translatable(TRANSLATION), UNDERLAY, UNDERLAY, pokemon, 0, 0, 0, Vec3d.ZERO);
    }

    @Override
    public void render (@NotNull DrawContext guiGraphics, float x, float y, @NotNull Pokemon pokemon, @NotNull IntSpeciesFeature feature) {

    }

    @Override
    public void renderBar (@NotNull DrawContext guiGraphics, float x, float y, int barValue, float barRatio, int barWidth) {

    }

    @Override
    public void renderElement (@NotNull DrawContext guiGraphics, float x, float y, @NotNull Pokemon pokemon, int barValue) {

    }

    @Override
    public boolean render (@NotNull DrawContext context, float x, float y, @NotNull Pokemon pokemon) {
        if (!ClientOptions.shouldShowEggGroupsFeature()) return false;

        GuiUtilsKt.blitk(
            context.getMatrices(),
            UNDERLAY,
            x,
            y,
            24,
            116
        );

        RenderHelperKt.drawScaledText(
            context,
            CobblemonResources.INSTANCE.getDEFAULT_LARGE(),
            Text.translatable("daycareplus.ui.egg_group").styled(style -> style.withBold(true)),
            x + 58,
            y + 2.5f,
            1f,
            1f,
            Integer.MAX_VALUE,
            Colors.WHITE,
            true,
            true,
            null,
            null
        );

        MutableText eggGroups;
        if (BreedableProperty.get(pokemon)) {
            eggGroups = ClientEggGroup.getGroups(pokemon)
                .stream()
                .map(group -> Text.translatable("daycareplus.group." + group.name().toLowerCase(Locale.ROOT)))
                .reduce(Text.empty(), (existingText, groupName) -> {
                    if (existingText.getString().isEmpty()) return groupName;
                    return existingText.append(" - ").append(groupName);
                });
        }
        else {
            eggGroups = Text.translatable("property.daycareplus.unbreedable");
        }

        RenderHelperKt.drawScaledText(
            context,
            CobblemonResources.INSTANCE.getDEFAULT_LARGE(),
            eggGroups,
            x + 58,
            y + 14,
            1f,
            1f,
            Integer.MAX_VALUE,
            Colors.WHITE,
            true,
            true,
            null,
            null
        );

        return true;
    }
}
