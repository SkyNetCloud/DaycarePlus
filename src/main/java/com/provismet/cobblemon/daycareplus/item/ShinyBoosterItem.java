package com.provismet.cobblemon.daycareplus.item;

import com.cobblemon.mod.common.block.entity.PokemonPastureBlockEntity;
import com.provismet.cobblemon.daycareplus.breeding.PastureExtension;
import com.provismet.cobblemon.daycareplus.imixin.IMixinPastureBlockEntity;
import com.provismet.cobblemon.daycareplus.registries.DPItemDataComponents;
import com.provismet.cobblemon.daycareplus.util.Styles;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

import java.util.List;

public class ShinyBoosterItem extends AbstractDaycareModifierItem {
    public ShinyBoosterItem (Settings settings, Item baseVanillaItem, PolymerModelData modelData) {
        super(settings, baseVanillaItem, modelData);
    }

    @Override
    public void appendTooltip (ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable(this.getTranslationKey() + ".tooltip", this.getBoostAmount(stack)).styled(Styles.GRAY_NO_ITALICS));
    }

    @Override
    protected ActionResult applyToDaycare (ItemUsageContext context, PokemonPastureBlockEntity pasture, IMixinPastureBlockEntity daycare) {
        PastureExtension extension = daycare.getExtension();
        extension.setShinyBoosts(this.getBoostAmount(context.getStack()) + extension.getShinyBoosts());

        if (context.getPlayer() != null) {
            context.getPlayer().sendMessage(Text.translatable("message.overlay.daycareplus.egg_shiny_boosted", extension.getShinyBoosts()));
        }
        context.getStack().decrementUnlessCreative(1, context.getPlayer());
        return ActionResult.SUCCESS;
    }

    private int getBoostAmount (ItemStack stack) {
        return stack.getOrDefault(DPItemDataComponents.BOOST_AMOUNT, 0);
    }
}
