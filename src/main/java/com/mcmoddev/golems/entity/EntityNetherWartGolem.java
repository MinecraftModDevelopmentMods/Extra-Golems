package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.entity.ai.PlaceBlocksGoal;
import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.GolemNames;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public final class EntityNetherWartGolem extends GolemBase {

	public static final String ALLOW_SPECIAL = "Allow Special: Plant Netherwart";
	public static final String FREQUENCY = "Netherwart Frequency";
	public static final String ALLOW_HEALING = "Allow Special: Random Healing";

	public EntityNetherWartGolem(final EntityType<? extends GolemBase> entityType, final World world) {
		super(entityType, world);
		this.setCanSwim(true);
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
			&& this.getConfigBool(ALLOW_HEALING) && rand.nextInt(450) == 0) {
			this.addPotionEffect(new EffectInstance(Effects.REGENERATION, 50, 1));
		}
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		final BlockState[] flowers = {
			Blocks.NETHER_WART.getDefaultState().with(NetherWartBlock.AGE, 0),
			Blocks.NETHER_WART.getDefaultState().with(NetherWartBlock.AGE, 1),
			Blocks.NETHER_WART.getDefaultState().with(NetherWartBlock.AGE, 2) };
		final Block[] soils = { Blocks.SOUL_SAND };
		final boolean allow = this.getConfigBool(ALLOW_SPECIAL);
		final int freq = this.getConfigInt(FREQUENCY);
		this.goalSelector.addGoal(2,
			new PlaceBlocksGoal(this, freq, flowers, soils, allow));
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
