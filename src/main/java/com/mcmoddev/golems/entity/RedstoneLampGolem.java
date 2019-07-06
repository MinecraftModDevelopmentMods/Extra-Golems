package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.blocks.BlockUtilityGlow;
import com.mcmoddev.golems.entity.ai.PlaceUtilityBlockGoal;
import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.entity.base.GolemMultiTextured;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.main.GolemItems;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public final class RedstoneLampGolem extends GolemMultiTextured {

	public static final String ALLOW_SPECIAL = "Allow Special: Emit Light";

	public static final String LAMP_PREFIX = "redstone_lamp";
	public static final String[] VARIANTS = { "lit", "unlit" };

	public static final BiPredicate<GolemBase, BlockState> LIT_PRED =
			(golem, toReplace) -> golem.isProvidingLight();

	public RedstoneLampGolem(final EntityType<? extends GolemBase> entityType, final World world) {
		super(entityType, world, ExtraGolems.MODID, LAMP_PREFIX, VARIANTS);
		this.setCanTakeFallDamage(true);
		final BlockState state = GolemItems.UTILITY_LIGHT.getDefaultState().with(BlockUtilityGlow.LIGHT_LEVEL, 15);
		this.goalSelector.addGoal(9, new PlaceUtilityBlockGoal(this, state, BlockUtilityGlow.UPDATE_TICKS,
				this.getConfigBool(ALLOW_SPECIAL), PlaceUtilityBlockGoal.getDefaultBiPred(state).and(LIT_PRED)));
	}

	@Override
	public boolean canInteractChangeTexture() {
		// always allow interact
		return true;
	}

	@Override
	public boolean isProvidingLight() {
		// only allow light if correct texture data
		return this.getTextureNum() == 0;
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
		return isProvidingLight() ? 15728880 : super.getBrightnessForRender();
	}

	@Override
	public float getBrightness() {
		return isProvidingLight() ? 1.0F : super.getBrightness();
	}
}
