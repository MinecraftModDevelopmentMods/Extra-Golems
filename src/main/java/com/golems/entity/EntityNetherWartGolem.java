package com.golems.entity;

import com.golems.entity.ai.EntityAIPlaceRandomBlocksStrictly;
import com.golems.main.Config;
import com.golems.main.ExtraGolems;
import com.golems.util.GolemConfigSet;
import com.golems.util.GolemNames;

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
		super(world);
		this.setCanSwim(true);
		this.setLootTableLoc(GolemNames.NETHERWART_GOLEM);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.26D);
	}
	
	/**
	 * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
	 * use this to react to sunlight and start to burn.
	 */
	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		// heals randomly, but only at night or in the nether
		if((!this.getEntityWorld().isDaytime() || this.getEntityWorld().provider.isNether()) 
				&& rand.nextInt(Config.RANDOM_HEAL_TIMER) == 0 && getConfig(this).getBoolean(ALLOW_HEALING)) {
			this.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 20, 2));
		}
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
		return makeTexture(ExtraGolems.MODID, GolemNames.NETHERWART_GOLEM);
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_WOOD_STEP;
	}

	@Override
	public List<String> addSpecialDesc(final List<String> list) {
		if (getConfig(this).getBoolean(EntityNetherWartGolem.ALLOW_SPECIAL)) {
			list.add(TextFormatting.RED + trans("entitytip.plants_warts"));
		}
		if(getConfig(this).getBoolean(ALLOW_HEALING)) {
			String sHeals = TextFormatting.RED + trans("entitytip.heals");
			list.add(sHeals);
		}
		return list;
	}
}
