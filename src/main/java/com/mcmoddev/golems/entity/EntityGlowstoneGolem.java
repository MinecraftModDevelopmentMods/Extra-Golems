package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.blocks.BlockUtilityGlow;
import com.mcmoddev.golems.entity.ai.EntityAIUtilityBlock;
import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.main.GolemItems;
import com.mcmoddev.golems.util.GolemNames;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public final class EntityGlowstoneGolem extends GolemBase {

	public static final String ALLOW_SPECIAL = "Allow Special: Emit Light";

	/**
	 * Float value between 0.0F and 1.0F that determines light level
	 **/
	private final float brightness;

	public EntityGlowstoneGolem(final World world) {
		super(EntityGlowstoneGolem.class, world);
		int lightInt = 15;
		this.brightness = 1.0F;
		final IBlockState state = GolemItems.blockLightSource.getDefaultState().with(BlockUtilityGlow.LIGHT_LEVEL, lightInt);
		this.tasks.addTask(9, new EntityAIUtilityBlock(this, state, BlockUtilityGlow.UPDATE_TICKS, this.getConfigBool(ALLOW_SPECIAL)));
		this.setImmuneToFire(true);
		this.setCanTakeFallDamage(true);
		this.setCanSwim(true);
		this.setLootTableLoc(GolemNames.GLOWSTONE_GOLEM);
	}

	@Override
	protected ResourceLocation applyTexture() {
		return makeTexture(ExtraGolems.MODID, GolemNames.GLOWSTONE_GOLEM);
	}
	
	@Override
	public boolean isProvidingLight() {
		return true;
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_GLASS_STEP;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.BLOCK_GLASS_BREAK;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public int getBrightnessForRender() {
		return (int) (15728880F * this.brightness);
	}

	@Override
	public float getBrightness() {
		return this.brightness;
	}
}
