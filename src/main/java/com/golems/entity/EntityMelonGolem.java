package com.golems.entity;

import com.golems.entity.ai.EntityAIPlaceRandomBlocksStrictly;
import com.golems.util.GolemConfigSet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockFlower.EnumFlowerType;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public final class EntityMelonGolem extends GolemBase {

    public static final String ALLOW_SPECIAL = "Allow Special: Plant Flowers";
    public static final String FREQUENCY = "Flower Frequency";

    public EntityMelonGolem(final World world) {
        super(world);
        this.setCanSwim(true);
        this.tasks.addTask(2, this.makeFlowerAI());
        this.setLootTableLoc("golem_melon");
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.26D);
    }

    @Override
    protected ResourceLocation applyTexture() {
        return makeGolemTexture("melon");
    }

    @Override
    public SoundEvent getGolemSound() {
        return SoundEvents.BLOCK_STONE_STEP;
    }

    /**
     * Create an EntityAIPlaceRandomBlocks.
     **/
    protected EntityAIBase makeFlowerAI() {
        GolemConfigSet cfg = getConfig(this);
        final Block[] soils = {Blocks.DIRT, Blocks.GRASS, Blocks.MYCELIUM, Blocks.FARMLAND};
        // init list and AI for planting flowers
        final List<IBlockState> lFlowers = new ArrayList<>();
        for (final EnumFlowerType e : BlockFlower.EnumFlowerType.values()) {
            lFlowers.add(e.getBlockType().getBlock().getStateFromMeta(e.getMeta()));
        }
        for (BlockTallGrass.EnumType e : BlockTallGrass.EnumType.values()) {
            lFlowers.add(Blocks.TALLGRASS.getDefaultState().withProperty(BlockTallGrass.TYPE, e));
        }
        final IBlockState[] flowers = lFlowers.toArray(new IBlockState[lFlowers.size()]);
        // get other parameters for the AI
        final int freq = cfg != null ? cfg.getInt(FREQUENCY) : 1000;
        //TODO: Fix possible NPE
        final boolean allowed = cfg.getBoolean(ALLOW_SPECIAL);
        return new EntityAIPlaceRandomBlocksStrictly(this, freq, flowers, soils, allowed);
    }

    @Override
    public List<String> addSpecialDesc(final List<String> list) {
        if (getConfig(this).getBoolean(EntityMelonGolem.ALLOW_SPECIAL))
            list.add(TextFormatting.GREEN + trans("entitytip.plants_flowers", trans("tile.flower1.name")));
        return list;
    }
}
