package com.provismet.cobblemon.daycareplus.breeding;

import com.cobblemon.mod.common.CobblemonItems;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.pokemon.CollectEggEvent;
import com.cobblemon.mod.common.api.moves.BenchedMove;
import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.pokemon.feature.IntSpeciesFeature;
import com.cobblemon.mod.common.block.entity.PokemonPastureBlockEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.provismet.cobblemon.daycareplus.DaycarePlusMain;
import com.provismet.cobblemon.daycareplus.api.DaycarePlusEvents;
import com.provismet.cobblemon.daycareplus.config.DaycarePlusOptions;
import com.provismet.cobblemon.daycareplus.feature.FertilityFeature;
import com.provismet.cobblemon.daycareplus.registries.DPItems;
import com.provismet.cobblemon.daycareplus.util.tag.DPItemTags;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.Optional;
import java.util.UUID;

public class PastureExtension {
    private final UUID uuid;
    private final PokemonPastureBlockEntity blockEntity;
    private long prevTime;
    private int twinBoosts;
    private int shinyBoosts;

    public PastureExtension (PokemonPastureBlockEntity blockEntity, long prevTime, UUID uuid, int twinBoosts, int shinyBoosts) {
        this.blockEntity = blockEntity;
        this.prevTime = prevTime;
        this.uuid = uuid;
        this.twinBoosts = twinBoosts;
        this.shinyBoosts = shinyBoosts;
    }

    public static PastureExtension fromNBT (PokemonPastureBlockEntity blockEntity, UUID uuid, NbtCompound nbt) {
        long prevTick = Long.MAX_VALUE;
        int boosts = 0;
        int shinyBoosts = 0;
        if (nbt.contains("prevTick")) prevTick = nbt.getLong("prevTick");
        if (nbt.contains("boosts")) boosts = nbt.getInt("boosts");
        if (nbt.contains("shinyBoosts")) shinyBoosts = nbt.getInt("shinyBoosts");

        return new PastureExtension(blockEntity, prevTick, uuid, boosts, shinyBoosts);
    }

    private void tryApplyMirrorHerb (Pokemon potentialHolder, Pokemon other) {
        if (!potentialHolder.heldItem().isOf(CobblemonItems.MIRROR_HERB)) return;
        PlayerEntity owner = null;
        if (this.blockEntity.getOwnerId() != null && this.blockEntity.getWorld() != null) {
            owner = this.blockEntity.getWorld().getPlayerByUuid(this.blockEntity.getOwnerId());
        }

        for (Move move : other.getMoveSet()) {
            if (potentialHolder.getForm().getMoves().getEggMoves().stream().anyMatch(moveTemplate -> moveTemplate.getName().equalsIgnoreCase(move.getName()))) {
                // Avoid relearning moves you already have.
                if (potentialHolder.getMoveSet().getMoves().stream().map(Move::getTemplate).anyMatch(moveTemplate -> moveTemplate.getName().equalsIgnoreCase(move.getName()))) {
                    continue;
                }
                boolean alreadyLearnt = false;
                for (BenchedMove benchedMove : potentialHolder.getBenchedMoves()) {
                    if (benchedMove.getMoveTemplate().getName().equalsIgnoreCase(move.getName())) {
                        alreadyLearnt = true;
                        break;
                    }
                }
                if (alreadyLearnt) continue;

                if (potentialHolder.getMoveSet().add(move.getTemplate().create()) && owner != null) {
                    owner.sendMessage(Text.translatable("message.chat.daycareplus.move_learnt", potentialHolder.getDisplayName(false), move.getDisplayName()));
                }
            }
        }
    }

    public long getPrevTime () {
        return this.prevTime;
    }

    public int getTwinBoosts () {
        return this.twinBoosts;
    }

    public void setTwinBoosts (int twinBoosts) {
        this.twinBoosts = twinBoosts;
    }

    public int getShinyBoosts () {
        return this.shinyBoosts;
    }

    public void setShinyBoosts (int shinyBoosts) {
        this.shinyBoosts = shinyBoosts;
    }

    public Optional<PotentialPokemonProperties> predictEgg () {
        if (this.blockEntity.getTetheredPokemon().size() != 2) return Optional.empty();
        Pokemon parent1 = this.blockEntity.getTetheredPokemon().getFirst().getPokemon();
        Pokemon parent2 = this.blockEntity.getTetheredPokemon().getLast().getPokemon();

        return BreedingUtils.getOffspring(parent1, parent2);
    }

    public void produceEgg (PotentialPokemonProperties potentialEgg) {
        PlayerEntity owner = null;
        if (this.blockEntity.getOwnerId() != null && this.blockEntity.getWorld() != null) {
            owner = this.blockEntity.getWorld().getPlayerByUuid(this.blockEntity.getOwnerId());
        }

        PokemonProperties properties = potentialEgg.createPokemonProperties();
        if (this.shinyBoosts > 0 && Boolean.FALSE.equals(properties.getShiny())) {
            --this.shinyBoosts;
            if (Math.random() < DaycarePlusOptions.getShinyBoosterRate()) properties.setShiny(true);
        }

        if (DaycarePlusOptions.doCompetitiveBreeding()) {
            FertilityFeature.decrement(potentialEgg.getPrimary());
            FertilityFeature.decrement(potentialEgg.getSecondary());

            if (DaycarePlusOptions.shouldConsumeHeldItems()) {
                if (potentialEgg.getPrimary().heldItem().isIn(DPItemTags.COMPETITIVE_BREEDING) && !potentialEgg.getPrimary().heldItem().isIn(DPItemTags.NO_CONSUME_BREEDING)) {
                    potentialEgg.getPrimary().swapHeldItem(ItemStack.EMPTY, true, true);
                }
                if (potentialEgg.getSecondary().heldItem().isIn(DPItemTags.COMPETITIVE_BREEDING) && !potentialEgg.getSecondary().heldItem().isIn(DPItemTags.NO_CONSUME_BREEDING)) {
                    potentialEgg.getSecondary().swapHeldItem(ItemStack.EMPTY, true, true);
                }
            }

            int eggFertility = DaycarePlusOptions.shouldEggsInheritFertility() ?
                    Math.min(FertilityFeature.get(potentialEgg.getPrimary()), FertilityFeature.get(potentialEgg.getSecondary())) :
                    FertilityFeature.getMax();

            properties.getCustomProperties().add(new IntSpeciesFeature(FertilityFeature.KEY, eggFertility));
        }

        if (owner instanceof ServerPlayerEntity serverPlayer) {
            CobblemonEvents.COLLECT_EGG.emit(new CollectEggEvent(properties, potentialEgg.getPrimary(), potentialEgg.getSecondary(), serverPlayer));
        }
        DaycarePlusEvents.PRE_EGG_PRODUCED.invoker().beforeItemCreated(properties);

        ItemStack egg = DPItems.POKEMON_EGG.createEggItem(properties);
        DaycarePlusEvents.POST_EGG_PRODUCED.invoker().afterItemCreated(egg);

        ((PastureContainer)(Object)this.blockEntity).add(egg);
    }

    public void tick () {
        if (this.blockEntity.getWorld() instanceof ServerWorld world) {

            if (world.getTime() % 20 == 0) {
                world.spawnParticles(
                    ParticleTypes.HEART,
                    this.blockEntity.getPos().getX() + 0.5,
                    this.blockEntity.getPos().getY() + 1.5,
                    this.blockEntity.getPos().getZ() + 0.5,
                    1,
                    0, 0, 0,
                    0
                );
            }

            long ticksToProcess = Math.max(0, world.getTime() - prevTime);
            this.prevTime = world.getTime();
            long eggAttempts = ticksToProcess / DaycarePlusOptions.getTicksPerEggAttempt();

            if ((world.getTime() + this.uuid.getLeastSignificantBits()) % DaycarePlusOptions.getTicksPerEggAttempt() == 0) ++eggAttempts;

            int calculatedEggs = 0;
            PlayerEntity owner = null;
            boolean applyMirrorHerb = false;
            if (this.blockEntity.getOwnerId() != null) {
                owner = this.blockEntity.getWorld().getPlayerByUuid(this.blockEntity.getOwnerId());
            }

            for (int i = 0; i < eggAttempts; ++i) {
                if (world.getRandom().nextDouble() > DaycarePlusOptions.getSuccessRatePerEggAttempt()) continue;
                applyMirrorHerb = true;

                int eggsToProduce = 1;
                if (this.twinBoosts > 0) {
                    eggsToProduce = 2;
                    --this.twinBoosts;
                }
                for (int j = 0; j < eggsToProduce; ++j) {
                    Optional<PotentialPokemonProperties> optionalEgg = this.predictEgg();
                    if (optionalEgg.isPresent()) {
                        if (owner != null) {
                            if (eggAttempts == 1) owner.sendMessage(Text.translatable("message.chat.daycareplus.egg_produced"));
                            else ++calculatedEggs;
                        }
                        this.produceEgg(optionalEgg.get());
                    }
                }
            }

            if (applyMirrorHerb && !this.blockEntity.getTetheredPokemon().isEmpty()) {
                Pokemon parent1 = this.blockEntity.getTetheredPokemon().getFirst().getPokemon();
                Pokemon parent2 = this.blockEntity.getTetheredPokemon().getLast().getPokemon();

                if (parent1 != null && parent2 != null) {
                    this.tryApplyMirrorHerb(parent1, parent2);
                    this.tryApplyMirrorHerb(parent2, parent1);
                }
            }

            calculatedEggs = MathHelper.clamp(calculatedEggs, 0, DaycarePlusOptions.getPastureInventorySize());
            if (calculatedEggs > 0 && owner != null) {
                if (calculatedEggs == 1) owner.sendMessage(Text.translatable("message.chat.daycareplus.single_egg_produced", calculatedEggs));
                else owner.sendMessage(Text.translatable("message.chat.daycareplus.multiple_egg_produced", calculatedEggs));
            }
        }
    }
}
