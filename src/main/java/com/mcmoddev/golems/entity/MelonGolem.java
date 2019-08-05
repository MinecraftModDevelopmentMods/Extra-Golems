package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.entity.ai.PlaceBlocksGoal;
import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.GolemNames;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public final class MelonGolem extends GolemBase {

	public static final String ALLOW_SPECIAL = "Allow Special: Plant Flowers";
	public static final String FREQUENCY = "Flower Frequency";
	public static final String ALLOW_HEALING = "Allow Special: Random Healing";
	
	public MelonGolem(final EntityType<? extends GolemBase> entityType, final World world) {
		super(entityType, world);
		this.enableSwim();
	}

	@Override
	protected ResourceLocation applyTexture() {
		return makeTexture(ExtraGolems.MODID, GolemNames.MELON_GOLEM);
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_STONE_STEP;
	}

	/**
	 * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
	 * use this to react to sunlight and start to burn.
	 */
	@Override
	public void livingTick() {
		super.livingTick();
		if (!this.getConfigBool(ALLOW_HEALING)) return;
		// heals randomly (about every 20 sec)
		if (rand.nextInt(450) == 0) {
			this.addPotionEffect(new EffectInstance(Effects.REGENERATION, 50, 1));
		}
	}

	/* Create an EntityAIPlaceRandomBlocks */
	@Override
	protected void registerGoals() {
		super.registerGoals();
		final Block[] soils = { Blocks.DIRT, Blocks.GRASS, Blocks.MYCELIUM, Blocks.PODZOL };
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
	}
}
