package com.provismet.cobblemon.daycareplus.gui;

import com.cobblemon.mod.common.CobblemonSounds;
import com.cobblemon.mod.common.api.gui.GuiUtilsKt;
import com.cobblemon.mod.common.api.pokemon.egg.EggGroup;
import com.cobblemon.mod.common.client.CobblemonResources;
import com.cobblemon.mod.common.client.render.RenderHelperKt;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.provismet.cobblemon.daycareplus.DaycarePlusMain;
import com.provismet.cobblemon.daycareplus.config.ClientOptions;
import com.provismet.cobblemon.daycareplus.feature.BreedableProperty;
import com.provismet.cobblemon.daycareplus.util.ClientEggGroup;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;

import java.util.Locale;
import java.util.Set;

public class EggGroupWidget extends ClickableWidget {
    public static final int WIDTH = 55;
    public static final int HEIGHT = 30;

    public static final Identifier TEXTURE = DaycarePlusMain.identifier("textures/gui/pc/egg_group_panel.png");
    public static final Identifier TEXTURE_HOVERED = DaycarePlusMain.identifier("textures/gui/pc/egg_group_panel_hovered.png");
    public static final Identifier TEXTURE_COLLAPSED = DaycarePlusMain.identifier("textures/gui/pc/egg_group_panel_collapsed.png");
    public static final Identifier TEXTURE_COLLAPSED_HOVERED = DaycarePlusMain.identifier("textures/gui/pc/egg_group_panel_collapsed_hovered.png");

    private static boolean collapsed = false;

    private Set<EggGroup> eggGroups = Set.of();
    private boolean breedable = true;

    public EggGroupWidget (int x, int y) {
        super(x, y, WIDTH, HEIGHT, Text.translatable("daycareplus.ui.egg_group"));
    }

    @Override
    protected void renderWidget (DrawContext context, int mouseX, int mouseY, float delta) {
        if ((this.eggGroups.isEmpty() && this.breedable) || !ClientOptions.shouldShowEggGroupsInPC()) return;

        Identifier texture;
        if (this.isHovered()) texture = collapsed ? TEXTURE_COLLAPSED_HOVERED : TEXTURE_HOVERED;
        else texture = collapsed ? TEXTURE_COLLAPSED : TEXTURE;

        // Render background
        GuiUtilsKt.blitk(
            context.getMatrices(),
            texture,
            this.getX(), this.getY(),
            HEIGHT, WIDTH
        );

        if (collapsed) return;

        // Render title
        RenderHelperKt.drawScaledText(
            context,
            CobblemonResources.INSTANCE.getDEFAULT_LARGE(),
            Text.translatable("daycareplus.ui.egg_group").styled(style -> style.withBold(true)),
            this.getX() + 34, this.getY() + 2,
            0.8f,
            1f,
            Integer.MAX_VALUE,
            Colors.WHITE,
            true,
            true,
            mouseX, mouseY
        );

        // Render egg groups
        if (!this.breedable) {
            RenderHelperKt.drawScaledText(
                context,
                CobblemonResources.INSTANCE.getDEFAULT_LARGE(),
                Text.translatable("property.daycareplus.unbreedable"),
                this.getX() + 34, this.getY() + 15,
                0.8f,
                1f,
                Integer.MAX_VALUE,
                Colors.WHITE,
                true,
                true,
                mouseX, mouseY
            );
        }
        else if (this.eggGroups.size() == 1) {
            String eggGroup = this.eggGroups
                .stream()
                .reduce(EggGroup.UNDISCOVERED, (group1, group2) -> group2).name().toLowerCase(Locale.ROOT);

            RenderHelperKt.drawScaledText(
                context,
                CobblemonResources.INSTANCE.getDEFAULT_LARGE(),
                Text.translatable("daycareplus.group." + eggGroup),
                this.getX() + 34, this.getY() + 15,
                0.8f,
                1f,
                Integer.MAX_VALUE,
                Colors.WHITE,
                true,
                true,
                mouseX, mouseY
            );
        }
        else {
            int offset = 11;
            for (EggGroup group : this.eggGroups) {
                RenderHelperKt.drawScaledText(
                    context,
                    CobblemonResources.INSTANCE.getDEFAULT_LARGE(),
                    Text.translatable("daycareplus.group." + group.name().toLowerCase(Locale.ROOT)),
                    this.getX() + 34, this.getY() + offset,
                    0.8f,
                    1f,
                    Integer.MAX_VALUE,
                    Colors.WHITE,
                    true,
                    true,
                    mouseX, mouseY
                );
                offset += 8;
            }
        }
    }

    @Override
    protected void appendClickableNarrations (NarrationMessageBuilder builder) {

    }

    @Override
    public void onClick (double mouseX, double mouseY) {
        collapsed = !collapsed;
    }

    public void setPokemon (Pokemon pokemon) {
        this.eggGroups = ClientEggGroup.getGroups(pokemon);
        this.breedable = BreedableProperty.get(pokemon);
    }

    @Override
    public void playDownSound (SoundManager soundManager) {
        soundManager.play(PositionedSoundInstance.master(CobblemonSounds.PC_CLICK, 1f));
    }

    @Override
    public boolean isNarratable () {
        return false;
    }

    @Override
    public boolean isHovered () {
        return super.isHovered() && ClientOptions.shouldShowEggGroupsInPC();
    }
}
