package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.entity.ai.PlaceBlocksGoal;
import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.entity.base.GolemMultiTextured;
import com.mcmoddev.golems.main.ExtraGolems;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public final class MushroomGolem extends GolemMultiTextured {

	public static final String ALLOW_SPECIAL = "Allow Special: Plant Mushrooms";
	public static final String FREQUENCY = "Mushroom Frequency";
	public static final String ALLOW_HEALING = "Allow Special: Random Healing";

	public static final String[] SHROOM_TYPES = { "red", "brown" };
	
	private boolean allowHealing;

	public MushroomGolem(final EntityType<? extends GolemBase> entityType, final World world) {
		super(entityType, world, ExtraGolems.MODID, SHROOM_TYPES);
		allowHealing = this.getConfigBool(ALLOW_HEALING);
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
				{ Blocks.DIRT, Blocks.GRASS_BLOCK, Blocks.MYCELIUM, Blocks.PODZOL, Blocks.NETHERRACK, Blocks.SOUL_SAND };
		this.goalSelector.addGoal(2,
				new PlaceBlocksGoal(this, freq, mushrooms, soils, allowed));
	}

	/**
	 * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
	 * use this to react to sunlight and start to burn.
	 */
	@Override
	public void livingTick() {
		super.livingTick();
		// heals randomly, but only at night
		if (allowHealing && !this.getEntityWorld().isDaytime() && rand.nextInt(450) == 0) {
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
	
	@Override
	public ItemStack getCreativeReturn(final RayTraceResult target) {
		return this.getTextureNum() == 0 ? new ItemStack(Blocks.RED_MUSHROOM_BLOCK) : new ItemStack(Blocks.BROWN_MUSHROOM_BLOCK);
	}
}
