package com.provismet.cobblemon.daycareplus.cmixin;

import com.cobblemon.mod.common.api.gui.GuiUtilsKt;
import com.cobblemon.mod.common.client.CobblemonResources;
import com.cobblemon.mod.common.client.gui.summary.featurerenderers.SummarySpeciesFeatureRenderer;
import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget;
import com.cobblemon.mod.common.client.gui.summary.widgets.screens.stats.StatWidget;
import com.cobblemon.mod.common.client.render.RenderHelperKt;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.provismet.cobblemon.daycareplus.DaycarePlusMain;
import com.provismet.cobblemon.daycareplus.config.DaycarePlusOptions;
import com.provismet.cobblemon.daycareplus.feature.BreedableProperty;
import com.provismet.cobblemon.daycareplus.features.EggGroupFeatureRenderer;
import com.provismet.cobblemon.daycareplus.util.ClientEggGroup;
import com.provismet.cobblemon.daycareplus.util.DPResources;
import com.provismet.cobblemon.daycareplus.config.ClientOptions;
import kotlin.jvm.internal.DefaultConstructorMarker;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Locale;

@Mixin(StatWidget.class)
public abstract class StatWidgetMixin extends SoundlessWidget {
    public StatWidgetMixin (int pX, int pY, int pWidth, int pHeight, @NotNull Text component) {
        super(pX, pY, pWidth, pHeight, component);
    }

    @Shadow @Final
    private List<SummarySpeciesFeatureRenderer<?>> renderableFeatures;

    @Inject(
        method = "<init>(IILcom/cobblemon/mod/common/pokemon/Pokemon;I)V",
        at = @At(value = "INVOKE", target = "Ljava/util/List;size()I")
    )
    private void onInit (int pX, int pY, Pokemon pokemon, int tabIndex, CallbackInfo info) {
        if (ClientOptions.shouldShowEggGroupsFeature() && renderableFeatures.stream().noneMatch(renderer -> renderer instanceof EggGroupFeatureRenderer))
            this.renderableFeatures.addFirst(new EggGroupFeatureRenderer(pokemon));
    }
}
