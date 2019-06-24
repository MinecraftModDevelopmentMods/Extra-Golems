package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.entity.ai.PlaceBlocksGoal;
import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.entity.base.GolemMultiTextured;
import com.mcmoddev.golems.main.ExtraGolems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public final class EntityMushroomGolem extends GolemMultiTextured {

	public static final String ALLOW_SPECIAL = "Allow Special: Plant Mushrooms";
	public static final String FREQUENCY = "Mushroom Frequency";
	public static final String ALLOW_HEALING = "Allow Special: Random Healing";

	public static final String SHROOM_PREFIX = "shroom";
	public static final String[] SHROOM_TYPES = { "red", "brown" };

	public EntityMushroomGolem(final EntityType<? extends GolemBase> entityType, final World world) {
		super(entityType, world, ExtraGolems.MODID, SHROOM_PREFIX, SHROOM_TYPES);
		this.setCanSwim(true);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		final boolean allowed = this.getConfigBool(ALLOW_SPECIAL);
		int freq = allowed ? this.getConfigInt(FREQUENCY) : -100;
		freq += this.rand.nextInt(Math.max(10, freq / 2));
		final BlockState[] mushrooms = { Blocks.BROWN_MUSHROOM.getDefaultState(),
			Blocks.RED_MUSHROOM.getDefaultState() };
		final Block[] soils =
			{ Blocks.DIRT, Blocks.GRASS, Blocks.MYCELIUM, Blocks.PODZOL, Blocks.NETHERRACK, Blocks.SOUL_SAND };
		this.goalSelector.addGoal(2,
			new PlaceBlocksGoal(this, freq, mushrooms, soils, allowed));
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_GRASS_STEP;
	}

	/**
	 * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
	 * use this to react to sunlight and start to burn.
	 */
	@Override
	public void livingTick() {
		super.livingTick();
		// heals randomly, but only at night
		//Note how it goes from least expensive to most expensive
		if (!this.getEntityWorld().isDaytime() && this.getConfigBool(ALLOW_HEALING) && rand.nextInt(450) == 0) {
			this.addPotionEffect(new EffectInstance(Effects.REGENERATION, 50, 1));
		}
	}

	@Override
	public void onBuilt(BlockState body, BlockState legs, BlockState arm1, BlockState arm2) {
		// use block type to give this golem the right texture (defaults to brown mushroom)
		byte textureNum = body.getBlock() == Blocks.RED_MUSHROOM_BLOCK ? (byte) 0
			: (byte) 1;
		textureNum %= this.getNumTextures();
		this.setTextureNum(textureNum);
	}
}
