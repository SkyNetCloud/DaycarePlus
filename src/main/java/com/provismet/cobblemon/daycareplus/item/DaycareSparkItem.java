package com.provismet.cobblemon.daycareplus.item;

import com.cobblemon.mod.common.block.entity.PokemonPastureBlockEntity;
import com.provismet.cobblemon.daycareplus.breeding.PotentialPokemonProperties;
import com.provismet.cobblemon.daycareplus.imixin.IMixinPastureBlockEntity;
import com.provismet.cobblemon.daycareplus.util.Styles;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.Optional;

public class DaycareSparkItem extends AbstractDaycareModifierItem {
    public DaycareSparkItem (Settings settings, Item baseVanillaItem, PolymerModelData modelData) {
        super(settings, baseVanillaItem, modelData);
    }

    @Override
    public void appendTooltip (ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable(this.getTranslationKey() + ".tooltip").styled(Styles.GRAY_NO_ITALICS));
    }

    @Override
    protected ActionResult applyToDaycare (ItemUsageContext context, PokemonPastureBlockEntity pasture, IMixinPastureBlockEntity daycare) {
        Optional<PotentialPokemonProperties> potentialEgg = daycare.getExtension().predictEgg();
        if (potentialEgg.isPresent()) {
            daycare.getExtension().produceEgg(potentialEgg.get());

            if (context.getPlayer() != null) {
                context.getPlayer().sendMessage(Text.translatable("message.chat.daycareplus.egg_produced"));
            }
            context.getStack().decrementUnlessCreative(1, context.getPlayer());
            return ActionResult.SUCCESS;
        }
        else {
            if (context.getPlayer() != null) {
                context.getPlayer().sendMessage(Text.translatable("message.overlay.daycareplus.spark_failure").formatted(Formatting.RED), true);
            }
            return ActionResult.FAIL;
        }
    }
}
