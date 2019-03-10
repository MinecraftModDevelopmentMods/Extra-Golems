package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.entity.ai.EntityAIPlaceRandomBlocksStrictly;
import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.GolemNames;
import net.minecraft.block.Block;
import net.minecraft.block.BlockNetherWart;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.List;

public final class EntityNetherWartGolem extends GolemBase {

	public static final Block NETHERWART = Blocks.NETHER_WART_BLOCK;

	public static final String ALLOW_SPECIAL = "Allow Special: Plant Netherwart";
	public static final String FREQUENCY = "Netherwart Frequency";
	public static final String ALLOW_HEALING = "Allow Special: Random Healing";

	public EntityNetherWartGolem(final World world) {
		super(EntityNetherWartGolem.class, world);
		this.setCanSwim(true);
		this.setLootTableLoc(GolemNames.NETHERWART_GOLEM);
		this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.26D);
	}
	
	/**
	 * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
	 * use this to react to sunlight and start to burn.
	 */
	@Override
	public void livingTick() {
		super.livingTick();
		// heals randomly, but only at night or in the nether (least to most expensive)
		if (container.canUseSpecial && (!this.getEntityWorld().isDaytime() || this.getEntityWorld().dimension.isNether())
				&& rand.nextInt(450) == 0 ) {
			this.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 20, 2));
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
		final boolean spawn = container.canUseSpecial;
		//TODO: reimpl config
		final int freq = 880;
		this.tasks.addTask(2,
			new EntityAIPlaceRandomBlocksStrictly(this, freq, flowers, soils, spawn));
	}

	@Override
	protected ResourceLocation applyTexture() {
		return makeTexture(ExtraGolems.MODID, GolemNames.NETHERWART_GOLEM);
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_WOOD_STEP;
	}

	@Override
	public List<String> addSpecialDesc(final List<String> list) {
		if (container.canUseSpecial) {
			list.add(TextFormatting.RED + trans("entitytip.plants_warts"));
		}
		//TODO: reimpl config
		if(container.canUseSpecial) {
			String sHeals = TextFormatting.RED + trans("entitytip.heals");
			list.add(sHeals);
		}
		return list;
	}
}
