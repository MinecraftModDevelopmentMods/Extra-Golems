package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.blocks.BlockUtilityGlow;
import com.mcmoddev.golems.entity.ai.EntityAIUtilityBlock;
import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.main.GolemItems;
import com.mcmoddev.golems.util.GolemNames;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public final class EntitySeaLanternGolem extends GolemBase {

	public static final String ALLOW_SPECIAL = "Allow Special: Emit Light";

	private static final float BRIGHTNESS = 1.0F;
	private static final int BRIGHTNESS_INT = (int) (BRIGHTNESS * 15.0F);

	public EntitySeaLanternGolem(final World world) {
		super(EntitySeaLanternGolem.class, world);
		this.canDrown = false;
		this.getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.2D);
	}

	@Override
	protected void initEntityAI() {
		super.initEntityAI();
		final boolean allow = this.getConfigBool(ALLOW_SPECIAL);
		final int freq = BlockUtilityGlow.UPDATE_TICKS;
		this.tasks.addTask(8, new EntityAIUtilityBlock(this, GolemItems.UTILITY_LIGHT.getDefaultState()
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
	public boolean isProvidingLight() {
		return true;
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_GLASS_STEP;
	}
}
