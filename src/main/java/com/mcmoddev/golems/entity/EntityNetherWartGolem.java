package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.entity.ai.EntityAIPlaceRandomBlocksStrictly;
import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.GolemNames;

import net.minecraft.block.Block;
import net.minecraft.block.BlockNetherWart;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public final class EntityNetherWartGolem extends GolemBase {

	public static final String ALLOW_SPECIAL = "Allow Special: Plant Netherwart";
	public static final String FREQUENCY = "Netherwart Frequency";
	public static final String ALLOW_HEALING = "Allow Special: Random Healing";

	public EntityNetherWartGolem(final World world) {
		super(EntityNetherWartGolem.class, world);
		this.setImmuneToFire(true);
		this.setCanSwim(true);
		this.setLootTableLoc(GolemNames.NETHERWART_GOLEM);
	}
	
	/**
	 * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
	 * use this to react to sunlight and start to burn.
	 */
	@Override
	public void livingTick() {
		super.livingTick();
		// heals randomly, but only at night or in the nether (least to most expensive)
		if ((!this.getEntityWorld().isDaytime() || this.getEntityWorld().dimension.isNether())
				&& this.getConfigBool(ALLOW_HEALING) && rand.nextInt(450) == 0 ) {
			this.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 50, 1));
		}
	}

	@Override
	protected void initEntityAI() {
		super.initEntityAI();
		final IBlockState[] flowers = {
			Blocks.NETHER_WART.getDefaultState().with(BlockNetherWart.AGE, 0),
			Blocks.NETHER_WART.getDefaultState().with(BlockNetherWart.AGE, 1),
			Blocks.NETHER_WART.getDefaultState().with(BlockNetherWart.AGE, 2)};
		final Block[] soils = {Blocks.SOUL_SAND};
		final boolean allow = this.getConfigBool(ALLOW_SPECIAL);
		final int freq = this.getConfigInt(FREQUENCY);
		this.tasks.addTask(2,
			new EntityAIPlaceRandomBlocksStrictly(this, freq, flowers, soils, allow));
	}

	@Override
	protected ResourceLocation applyTexture() {
		return makeTexture(ExtraGolems.MODID, GolemNames.NETHERWART_GOLEM);
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_WOOD_STEP;
	}
}
