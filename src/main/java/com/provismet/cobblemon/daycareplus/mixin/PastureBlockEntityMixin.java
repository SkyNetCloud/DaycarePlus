package com.provismet.cobblemon.daycareplus.mixin;

import com.cobblemon.mod.common.block.entity.PokemonPastureBlockEntity;
import com.provismet.cobblemon.daycareplus.breeding.BreedingLink;
import com.provismet.cobblemon.daycareplus.breeding.PastureExtension;
import com.provismet.cobblemon.daycareplus.config.DaycarePlusOptions;
import com.provismet.cobblemon.daycareplus.gui.DaycareGUI;
import com.provismet.cobblemon.daycareplus.imixin.IMixinPastureBlockEntity;
import com.provismet.cobblemon.daycareplus.util.Styles;
import eu.pb4.sgui.api.elements.GuiElement;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Mixin(PokemonPastureBlockEntity.class)
public abstract class PastureBlockEntityMixin extends BlockEntity implements IMixinPastureBlockEntity {
    public PastureBlockEntityMixin (BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Shadow public abstract UUID getOwnerId ();

    @Unique private boolean isBreeder = false;
    @Unique private UUID breederUuid = null;
    @Unique private boolean skipIntroDialogue = false;
    @Unique private boolean skipDaycareGUI = false;
    @Unique private PastureExtension extension;
    @Unique private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(DaycarePlusOptions.getPastureInventorySize(), ItemStack.EMPTY);
    @Unique private GuiElement eggCounter = DaycareGUI.createEggButton(this);
    @Unique private GuiElement twinBoostCounter = DaycareGUI.createTwinBoostButton(this);
    @Unique private GuiElement shinyBoostCounter = DaycareGUI.createShinyBoostButton(this);

    @Override
    public PastureExtension getExtension () {
        return this.extension;
    }

    @Override
    public void setExtension (PastureExtension extension) {
        this.extension = extension;
    }

    @Override
    public void setShouldBreed (boolean shouldBreed) {
        this.isBreeder = shouldBreed;
    }

    @Override
    public boolean shouldBreed () {
        return this.isBreeder;
    }

    @Override
    public void setSkipIntroDialogue (boolean skipIntroDialogue) {
        this.skipIntroDialogue = skipIntroDialogue;
    }

    @Override
    public boolean shouldSkipIntro () {
        return this.skipIntroDialogue;
    }

    @Override
    public void setShouldSkipDaycareGUI (boolean skipGUI) {
        this.skipDaycareGUI = skipGUI;
    }

    @Override
    public boolean shouldSkipDaycareGUI () {
        return this.skipDaycareGUI;
    }

    @Override
    public GuiElement getEggCounterButton () {
        this.updateEggCounter();
        return this.eggCounter;
    }

    @Override
    public GuiElement getTwinBoostCounterButton () {
        this.updateBoostCounter();
        return this.twinBoostCounter;
    }

    @Override
    public GuiElement getShinyBoostCounterButton () {
        this.updateBoostCounter();
        return this.shinyBoostCounter;
    }

    @Override
    public UUID getBreederUUID () {
        return this.breederUuid;
    }

    @Override
    public void setBreederUUID (UUID uuid) {
        this.breederUuid = uuid;
    }

    @Override
    public void add (ItemStack stack) {
        for (int i = 0; i < this.inventory.size(); ++i) {
            if (this.inventory.get(i).isEmpty()) {
                this.inventory.set(i, stack.copyAndEmpty());
                this.markDirty();
                this.updateEggCounter();
                this.updateBoostCounter();
                break;
            }
        }
    }

    @Override
    public List<ItemStack> withdraw (int amount) {
        if (this.isEmpty()) return List.of();

        List<ItemStack> withdrawn = new ArrayList<>();
        for (int i = 0; i < this.inventory.size() && withdrawn.size() < amount; ++i) {
            ItemStack egg = this.getStack(i);
            if (!egg.isEmpty()) {
                withdrawn.add(egg.copy());
                this.setStack(i, ItemStack.EMPTY);
            }
        }
        return withdrawn;
    }

    @Override
    public int count () {
        int heldEggs = 0;
        for (ItemStack stack : this.inventory) {
            if (!stack.isEmpty()) ++heldEggs;
        }
        return heldEggs;
    }

    @Override
    public int size () {
        return this.inventory.size();
    }

    @Override
    public boolean isEmpty () {
        for (ItemStack stack : this.inventory) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getStack (int slot) {
        return this.inventory.get(slot);
    }

    @Override
    public ItemStack removeStack (int slot, int amount) {
        ItemStack stack = Inventories.splitStack(this.inventory, slot, amount);
        if (!stack.isEmpty()) {
            this.markDirty();
            this.updateEggCounter();
        }

        return stack;
    }

    @Override
    public ItemStack removeStack (int slot) {
        ItemStack stack = this.inventory.get(slot);
        this.inventory.set(slot, ItemStack.EMPTY);
        if (!stack.isEmpty()) {
            this.markDirty();
            this.updateEggCounter();
        }

        return stack;
    }

    @Override
    public void setStack (int slot, ItemStack stack) {
        this.inventory.set(slot, stack);
        this.markDirty();
        this.updateEggCounter();
        this.updateBoostCounter();
    }

    @Override
    public boolean canPlayerUse (PlayerEntity player) {
        return true;
    }

    @Override
    public void clear () {
        this.inventory.clear();
        this.markDirty();
        this.updateEggCounter();
    }

    @Unique
    private void updateEggCounter () {
        this.eggCounter.getItemStack().set(DataComponentTypes.CUSTOM_NAME, Text.translatable("gui.button.daycareplus.eggs_held", this.count(), this.size()).styled(Styles.WHITE_NO_ITALICS));
    }

    @Unique
    private void updateBoostCounter () {
        this.twinBoostCounter.getItemStack().set(DataComponentTypes.CUSTOM_NAME, Text.translatable("gui.button.daycareplus.twin_boosts_remaining", this.getExtension() != null ? this.getExtension().getTwinBoosts() : 0).styled(Styles.WHITE_NO_ITALICS));
        this.shinyBoostCounter.getItemStack().set(DataComponentTypes.CUSTOM_NAME, Text.translatable("gui.button.daycareplus.shiny_boosts_remaining", this.getExtension() != null ? this.getExtension().getShinyBoosts() : 0).styled(Styles.WHITE_NO_ITALICS));
    }

    @Inject(method = "TICKER$lambda$0", at = @At("HEAD"))
    private static void tick (World world, BlockPos pos, BlockState blockState, PokemonPastureBlockEntity pasture, CallbackInfo info) {
        if (world.isClient()) return;

        IMixinPastureBlockEntity imixin = (IMixinPastureBlockEntity)(Object)pasture;
        if (imixin.shouldBreed()) {
            if (imixin.getBreederUUID() == null) {
                imixin.setBreederUUID(UUID.randomUUID());
            }

            if (pasture.getOwnerId() != null && !BreedingLink.has(pasture.getOwnerId(), imixin.getBreederUUID())) {
                if (!BreedingLink.add(pasture.getOwnerId(), imixin.getBreederUUID())) {
                    imixin.setShouldBreed(false);
                    imixin.setExtension(null);
                    return;
                }
            }

            if (pasture.getOwnerId() != null && BreedingLink.count(pasture.getOwnerId()) > DaycarePlusOptions.getMaxPasturesPerPlayer()) {
                imixin.setShouldBreed(false);
                imixin.setExtension(null);
                BreedingLink.remove(pasture.getOwnerId(), imixin.getBreederUUID());
                return;
            }

            if (imixin.getExtension() == null) {
                imixin.setExtension(new PastureExtension(pasture, Long.MAX_VALUE, imixin.getBreederUUID(), 0, 0));
            }
            imixin.getExtension().tick();
        }
        else {
            imixin.setExtension(null);
        }
    }

    @Inject(method = "writeNbt", at = @At("TAIL"))
    private void addNbt (NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup, CallbackInfo info) {
        NbtCompound breederNbt = new NbtCompound();
        breederNbt.putBoolean("isBreeder", this.isBreeder);
        breederNbt.putUuid("uuid", Objects.requireNonNullElseGet(this.breederUuid, UUID::randomUUID));

        if (this.extension != null) breederNbt.putLong("prevTick", this.extension.getPrevTime());
        else if (this.world != null) breederNbt.putLong("prevTick", this.world.getTime());
        else breederNbt.putLong("prevTick", Long.MAX_VALUE);

        if (this.extension != null) breederNbt.putInt("boosts", this.extension.getTwinBoosts());
        else breederNbt.putInt("boosts", 0);

        Inventories.writeNbt(breederNbt, this.inventory, registryLookup);

        nbt.put("daycarePlus", breederNbt);
    }

    @Inject(method = "readNbt", at = @At("TAIL"))
    private void getNbt (NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup, CallbackInfo info) {
        if (nbt.contains("daycarePlus") && nbt.get("daycarePlus") instanceof NbtCompound daycareNbt) {
            if (daycareNbt.contains("isBreeder")) this.isBreeder = daycareNbt.getBoolean("isBreeder");
            if (daycareNbt.contains("uuid")) this.breederUuid = daycareNbt.getUuid("uuid");
            else this.breederUuid = UUID.randomUUID();

            if (this.isBreeder) {
                this.extension = PastureExtension.fromNBT((PokemonPastureBlockEntity)(Object)this, this.breederUuid, daycareNbt);
            }

            this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
            Inventories.readNbt(daycareNbt, this.inventory, registryLookup);
        }
    }

    @Inject(method = "onBroken", at = @At("HEAD"), remap = false)
    private void spillInventory (CallbackInfo info) {
        if (this.world == null) return;

        for (ItemStack stack : this.inventory) {
            Block.dropStack(this.world, this.pos, stack.copyAndEmpty());
        }

        if (this.getOwnerId() != null && this.breederUuid != null) {
            BreedingLink.remove(this.getOwnerId(), this.breederUuid);
        }
    }

    @Inject(method = "getMaxTethered", at = @At("HEAD"), cancellable = true, remap = false)
    private void restrictDaycare (CallbackInfoReturnable<Integer> cir) {
        if (this.isBreeder) cir.setReturnValue(2);
    }
}
