package com.provismet.cobblemon.daycareplus.cmixin;

import com.cobblemon.mod.common.api.gui.GuiUtilsKt;
import com.cobblemon.mod.common.api.pokemon.egg.EggGroup;
import com.cobblemon.mod.common.client.gui.summary.Summary;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.provismet.cobblemon.daycareplus.config.ClientOptions;
import com.provismet.cobblemon.daycareplus.feature.BreedableProperty;
import com.provismet.cobblemon.daycareplus.util.ClientEggGroup;
import com.provismet.cobblemon.daycareplus.util.DPResources;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Mixin(Summary.class)
public abstract class SummaryMixin extends Screen {
    @Unique
    private static final int LENGTH = 24;

    protected SummaryMixin (Text title) {
        super(title);
    }

    @Shadow
    private Pokemon selectedPokemon;

    @Shadow @Final
    private static float SCALE;

    @Inject(method = "render", at = @At("TAIL"))
    private void renderEggIcon (DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo info) {
        if (!ClientOptions.shouldShowEggGroupsTooltip()) return;

        int x = super.width - 325;
        int y = super.height + 132;

        GuiUtilsKt.blitk(
            context.getMatrices(),
            DPResources.SUMMARY_EGG_ICON,
            x, y,
            LENGTH, LENGTH,
            0, 0,
            LENGTH, LENGTH,
            0,
            1, 1, 1,
            1,
            true,
            SCALE
        );

        if (mouseX / SCALE >= x && mouseX / SCALE <= x + LENGTH && mouseY / SCALE >= y && mouseY / SCALE <= y + LENGTH) {
            List<Text> tooltip = new ArrayList<>();
            tooltip.add(Text.translatable("daycareplus.ui.egg_group").styled(style -> style.withBold(true)));
            if (BreedableProperty.get(this.selectedPokemon)) {
                for (EggGroup group : ClientEggGroup.getGroups(this.selectedPokemon)) {
                    tooltip.add(Text.translatable("daycareplus.group." + group.name().toLowerCase(Locale.ROOT)).styled(style -> style.withBold(false)));
                }
            }
            else {
                tooltip.add(Text.translatable("property.daycareplus.unbreedable"));
            }
            context.getMatrices().push();
            context.getMatrices().translate(0, 0, 1001);
            context.drawTooltip(MinecraftClient.getInstance().textRenderer, tooltip, mouseX, mouseY);
            context.getMatrices().pop();
        }
    }
}
