package com.provismet.cobblemon.daycareplus.item;

import com.cobblemon.mod.common.block.PastureBlock;
import com.cobblemon.mod.common.block.entity.PokemonPastureBlockEntity;
import com.provismet.cobblemon.daycareplus.imixin.IMixinPastureBlockEntity;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;

public abstract class AbstractDaycareModifierItem extends PolymerItem {
    public AbstractDaycareModifierItem (Settings settings, Item baseVanillaItem, PolymerModelData modelData) {
        super(settings, baseVanillaItem, modelData);
    }

    @Override
    public ActionResult useOnBlock (ItemUsageContext context) {
        Block block = context.getWorld().getBlockState(context.getBlockPos()).getBlock();

        if (block instanceof PastureBlock pastureBlock) {
            BlockPos pasturePos = pastureBlock.getBasePosition(context.getWorld().getBlockState(context.getBlockPos()), context.getBlockPos());
            BlockEntity entity = context.getWorld().getBlockEntity(pasturePos);

            if (entity instanceof PokemonPastureBlockEntity pasture && entity instanceof IMixinPastureBlockEntity daycare && daycare.shouldBreed()) {
                return this.applyToDaycare(context, pasture, daycare);
            }
            if (context.getPlayer() != null) {
                context.getPlayer().sendMessage(Text.translatable("message.chat.daycareplus.not_daycare"));
                return ActionResult.FAIL;
            }
        }
        return super.useOnBlock(context);
    }

    protected abstract ActionResult applyToDaycare (ItemUsageContext context, PokemonPastureBlockEntity pasture, IMixinPastureBlockEntity daycare);
}
