package com.golems.events;

import com.golems.entity.GolemBase;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

/**
 * This event exists for other mods or addons to handle and modify the Sponge Golem's behavior. It
 * is not handled in Extra Golems.
 */
@Event.HasResult
@Cancelable
public final class IceGolemFreezeEvent extends Event {

    protected List<BlockPos> affectedBlocks;
    protected Function<IBlockState, IBlockState> freezeFunction;

    public final GolemBase iceGolem;
    public final BlockPos iceGolemPos;

    /**
     * This percentage of Packed Ice placed will become regular ice instead.
     **/
    public static final int ICE_CHANCE = 52;
    /**
     * This percentage of Obsidian placed will become cobblestone instead.
     **/
    public static final int COBBLE_CHANCE = 29;

    /**
     * This should be passed in World#setBlockState when using this event.
     **/
    public int updateFlag;

    public IceGolemFreezeEvent(final GolemBase golem, final BlockPos center, final int radius) {
        this.setResult(Result.ALLOW);
        this.iceGolem = golem;
        this.iceGolemPos = center;
        this.updateFlag = 3;
        this.initAffectedBlockList(radius);
        this.setFunction(new DefaultFreezeFunction(golem.getRNG(), ICE_CHANCE, COBBLE_CHANCE));
    }

    public void initAffectedBlockList(final int range) {
        this.affectedBlocks = new ArrayList<>(range * range * 2 * 4);
        final int maxDis = range * range;
        // check 3-layer circle around this golem (disc, not sphere) to add positions to the map
        for (int i = -range; i <= range; i++) {
            for (int j = -1; j <= 1; j++) {
                for (int k = -range; k <= range; k++) {
                    final BlockPos currentPos = this.iceGolemPos.add(i, j, k);
                    if (iceGolemPos.distanceSq(currentPos) <= maxDis) {
                        this.affectedBlocks.add(currentPos);
                    }
                }
            }
        }
    }

    public Function<IBlockState, IBlockState> getFunction() {
        return this.freezeFunction;
    }

    public void setFunction(final Function<IBlockState, IBlockState> toSet) {
        this.freezeFunction = toSet;
    }

    public List<BlockPos> getAffectedPositions() {
        return this.affectedBlocks;
    }

    public boolean add(final BlockPos pos) {
        return this.affectedBlocks.add(pos);
    }

    public boolean remove(final BlockPos toRemove) {
        return this.affectedBlocks.remove(toRemove);
    }

    public static class DefaultFreezeFunction implements Function<IBlockState, IBlockState> {

        /**
         * Random instance.
         **/
        public final Random random;
        /**
         * This percentage of Packed Ice placed will become regular ice instead.
         **/
        public final int iceChance;
        /**
         * This percentage of Obsidian placed will become cobblestone instead.
         **/
        public final int cobbleChance;

        public DefaultFreezeFunction(final Random randomIn, final int iceChanceIn, final int cobbleChanceIn) {
            super();
            this.random = randomIn;
            this.iceChance = iceChanceIn;
            this.cobbleChance = cobbleChanceIn;
        }

        @Override
        public IBlockState apply(final IBlockState input) {
            final IBlockState cobbleState = Blocks.COBBLESTONE.getDefaultState();
            final IBlockState iceState = Blocks.ICE.getDefaultState();
            final Material material = input.getMaterial();
            if (material.isLiquid()) {
                final Block block = input.getBlock();

                if (block == Blocks.WATER) {
                    final boolean isNotPacked = this.random.nextInt(100) < this.iceChance;
                    return isNotPacked ? iceState : Blocks.PACKED_ICE.getDefaultState();
                } else if (block == Blocks.LAVA) {
                    final boolean isNotObsidian = this.random.nextInt(100) < this.cobbleChance;
                    return isNotObsidian ? cobbleState : Blocks.OBSIDIAN.getDefaultState();
                } else if (block == Blocks.FLOWING_WATER) {
                    return iceState;
                } else if (block == Blocks.FLOWING_LAVA) {
                    return cobbleState;
                }
            }

            return input;
        }
    }
}
