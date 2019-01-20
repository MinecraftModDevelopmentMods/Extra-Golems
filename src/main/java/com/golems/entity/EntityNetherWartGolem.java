package com.golems.entity;

import com.golems.entity.ai.EntityAIPlaceRandomBlocksStrictly;
import com.golems.util.GolemConfigSet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockNetherWart;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.List;

public final class EntityNetherWartGolem extends GolemBase {

    public static final Block NETHERWART = Blocks.NETHER_WART_BLOCK;

    public static final String ALLOW_SPECIAL = "Allow Special: Plant Netherwart";
    public static final String FREQUENCY = "Netherwart Frequency";
    //public static final String DROP_NETHERWART_BLOCK = "Drop Netherwart Blocks";

    public EntityNetherWartGolem(final World world) {
        super(world);
        this.setCanSwim(true);
        this.setLootTableLoc("golem_nether_wart");
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.26D);
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        final IBlockState[] flowers = {
                Blocks.NETHER_WART.getDefaultState().withProperty(BlockNetherWart.AGE, 0),
                Blocks.NETHER_WART.getDefaultState().withProperty(BlockNetherWart.AGE, 1),
                Blocks.NETHER_WART.getDefaultState().withProperty(BlockNetherWart.AGE, 2)};
        final Block[] soils = {Blocks.SOUL_SAND};
        GolemConfigSet cfg = getConfig(this);
        final boolean spawn = cfg.getBoolean(ALLOW_SPECIAL);
        final int freq = cfg.getInt(FREQUENCY);
        this.tasks.addTask(2,
                new EntityAIPlaceRandomBlocksStrictly(this, freq, flowers, soils, spawn));
    }

    @Override
    protected ResourceLocation applyTexture() {
        return makeGolemTexture("nether_wart");
    }

    @Override
    public SoundEvent getGolemSound() {
        return SoundEvents.BLOCK_WOOD_STEP;
    }

    @Override
    public List<String> addSpecialDesc(final List<String> list) {
        if (getConfig(this).getBoolean(EntityNetherWartGolem.ALLOW_SPECIAL))
            list.add(TextFormatting.RED + trans("entitytip.plants_warts"));
        return list;
    }
}
