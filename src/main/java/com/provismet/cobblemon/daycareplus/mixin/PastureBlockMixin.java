package com.provismet.cobblemon.daycareplus.mixin;

import com.cobblemon.mod.common.block.PastureBlock;
import com.cobblemon.mod.common.block.entity.PokemonPastureBlockEntity;
import com.cobblemon.mod.common.util.PlayerExtensionsKt;
import com.provismet.cobblemon.daycareplus.gui.DaycareGUI;
import com.provismet.cobblemon.daycareplus.gui.IntroGUI;
import com.provismet.cobblemon.daycareplus.imixin.IMixinPastureBlockEntity;
import com.provismet.cobblemon.daycareplus.util.tag.DPItemTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PastureBlock.class)
public abstract class PastureBlockMixin extends BlockWithEntity {
    protected PastureBlockMixin (Settings settings) {
        super(settings);
    }

    @Shadow public abstract BlockPos getBasePosition (BlockState state, BlockPos pos);

    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    private void openGui (BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if (player instanceof ServerPlayerEntity serverPlayer && !PlayerExtensionsKt.isInBattle(serverPlayer)) {
            BlockPos basePos = this.getBasePosition(state, pos);
            if (!(world.getBlockEntity(basePos) instanceof PokemonPastureBlockEntity pastureBlockEntity)) return;

            boolean isOwner = pastureBlockEntity.getOwnerId() != null && pastureBlockEntity.getOwnerId().toString().equals(player.getUuid().toString());
            if (serverPlayer.getMainHandStack().isIn(DPItemTags.BYPASS_DAYCARE_OPEN) && isOwner) {
                cir.setReturnValue(ActionResult.PASS);
                return;
            }

            IMixinPastureBlockEntity mixinPasture = (IMixinPastureBlockEntity)(Object)pastureBlockEntity;
            if (mixinPasture.shouldSkipDaycareGUI()) {
                mixinPasture.setShouldSkipDaycareGUI(false);
                return; // Allow the rest of the original function to play out.
            }

            if (mixinPasture.shouldBreed()) {
                if (!isOwner) {
                    player.sendMessage(Text.translatable("message.overlay.daycareplus.not_owner").formatted(Formatting.RED), true);
                    cir.setReturnValue(ActionResult.SUCCESS_NO_ITEM_USED);
                    return;
                }

                DaycareGUI.create(pastureBlockEntity, mixinPasture, serverPlayer, state, hit).open();
                cir.setReturnValue(ActionResult.SUCCESS_NO_ITEM_USED);
            }
            else if (!mixinPasture.shouldSkipIntro()) {
                if (isOwner) {
                    if (pastureBlockEntity.getTetheredPokemon().isEmpty()) {
                        IntroGUI.create(mixinPasture, serverPlayer).open();
                        cir.setReturnValue(ActionResult.SUCCESS_NO_ITEM_USED);
                    }
                    else {
                        mixinPasture.setSkipIntroDialogue(true);
                    }
                }
                else {
                    player.sendMessage(Text.translatable("message.overlay.daycareplus.not_owner").formatted(Formatting.RED), true);
                    cir.setReturnValue(ActionResult.SUCCESS_NO_ITEM_USED);
                }
            }
        }
    }
}
