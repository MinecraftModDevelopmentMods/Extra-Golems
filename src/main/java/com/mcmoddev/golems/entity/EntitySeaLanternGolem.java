package com.mcmoddev.golems.entity;

import java.util.function.Predicate;

import com.mcmoddev.golems.blocks.BlockUtility;
import com.mcmoddev.golems.blocks.BlockUtilityGlow;
import com.mcmoddev.golems.entity.ai.EntityAIPlaceSingleBlock;
import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.main.GolemItems;
import com.mcmoddev.golems.util.GolemNames;

import net.minecraft.block.BlockFlowingFluid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.SoundEvents;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public final class EntitySeaLanternGolem extends GolemBase {

	public static final String ALLOW_SPECIAL = "Allow Special: Emit Light";
//	public static final String FREQUENCY = "Light Frequency";
	public static final Predicate<IBlockState> WATER_PRED = toReplace -> 
		!(toReplace.getBlock() instanceof BlockUtility) && toReplace.getMaterial() == Material.WATER
		&& toReplace.get(BlockFlowingFluid.LEVEL) == 0;

	private static final float BRIGHTNESS = 1.0F;
	private static final int BRIGHTNESS_INT = (int) (BRIGHTNESS * 15.0F);

	public EntitySeaLanternGolem(final World world) {
		super(EntitySeaLanternGolem.class, world);
		this.canDrown = false;
		this.setLootTableLoc(GolemNames.SEALANTERN_GOLEM);
		this.getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.2D);
	}

	@Override
	protected void initEntityAI() {
		super.initEntityAI();
		// lights above and below water... need to add to different lists to run concurrently
		final boolean allow = this.getConfigBool(ALLOW_SPECIAL);
		final int freq = BlockUtilityGlow.UPDATE_TICKS;
		this.tasks.addTask(8, new EntityAIPlaceSingleBlock(this, GolemItems.blockLightSource.getDefaultState()
			.with(BlockUtilityGlow.LIGHT_LEVEL, BRIGHTNESS_INT).with(BlockStateProperties.WATERLOGGED, true), 
			freq, allow, WATER_PRED));
		this.targetTasks.addTask(8, new EntityAIPlaceSingleBlock(this, GolemItems.blockLightSource.getDefaultState()
			.with(BlockUtilityGlow.LIGHT_LEVEL, BRIGHTNESS_INT), freq, allow));
	}

	@Override
	protected float getWaterSlowDown() {
		return 0.95F;
	}

	@Override
	protected ResourceLocation applyTexture() {
		return makeTexture(ExtraGolems.MODID, GolemNames.SEALANTERN_GOLEM);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public int getBrightnessForRender() {
		return (int) (15728880F * EntitySeaLanternGolem.BRIGHTNESS);
	}

	@Override
	public float getBrightness() {
		return EntitySeaLanternGolem.BRIGHTNESS;
	}
	
	@Override
	public boolean doesProvideLight() {
		return true;
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_GLASS_STEP;
	}
}
