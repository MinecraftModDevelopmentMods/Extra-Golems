package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.entity.ai.PassiveEffectsGoal;
import com.mcmoddev.golems.entity.ai.PlaceBlocksGoal;
import com.mcmoddev.golems.entity.base.GolemBase;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;

public final class MelonGolem extends GolemBase {

	public static final String ALLOW_SPECIAL = "Allow Special: Plant Flowers";
	public static final String FREQUENCY = "Flower Frequency";
	public static final String ALLOW_HEALING = "Allow Special: Random Healing";
	
	public MelonGolem(final EntityType<? extends GolemBase> entityType, final World world) {
		super(entityType, world);
	}
	
	/* Create an EntityAIPlaceRandomBlocks */
	@Override
	protected void registerGoals() {
		super.registerGoals();
		final Block[] soils = { Blocks.DIRT, Blocks.GRASS_BLOCK, Blocks.MYCELIUM, Blocks.PODZOL };
		// init list and AI for planting flowers
		final BlockState[] flowers = {
				Blocks.POPPY.getDefaultState(),
				Blocks.DANDELION.getDefaultState(),
				Blocks.BLUE_ORCHID.getDefaultState(),
				Blocks.ALLIUM.getDefaultState(),
				Blocks.AZURE_BLUET.getDefaultState(),
				Blocks.RED_TULIP.getDefaultState(),
				Blocks.ORANGE_TULIP.getDefaultState(),
				Blocks.WHITE_TULIP.getDefaultState(),
				Blocks.PINK_TULIP.getDefaultState(),
				Blocks.OXEYE_DAISY.getDefaultState() };
		// get other parameters for the AI
		final int freq = this.getConfigInt(FREQUENCY);
		final boolean allowed = this.getConfigBool(ALLOW_SPECIAL);
		this.goalSelector.addGoal(2, new PlaceBlocksGoal(this, freq, flowers, soils, allowed));
		// healing goal
		if(this.getConfigBool(ALLOW_HEALING)) {
			this.goalSelector.addGoal(4, new PassiveEffectsGoal(this, Effects.REGENERATION, 50, 60, 1, 1, g -> g.getEntityWorld().getRandom().nextInt(450) == 0));
		}
	}
}
