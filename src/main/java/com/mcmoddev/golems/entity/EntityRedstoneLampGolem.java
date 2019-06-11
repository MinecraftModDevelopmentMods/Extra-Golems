package com.mcmoddev.golems.entity;

import java.util.function.BiPredicate;

import com.mcmoddev.golems.blocks.BlockUtilityGlow;
import com.mcmoddev.golems.entity.ai.EntityAIUtilityBlock;
import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.entity.base.GolemMultiTextured;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.main.GolemItems;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public final class EntityRedstoneLampGolem extends GolemMultiTextured {

	public static final String ALLOW_SPECIAL = "Allow Special: Emit Light";
	
	public static final String LAMP_PREFIX = "redstone_lamp";
	public static final String[] VARIANTS = { "lit", "unlit" };
	
	public static final BiPredicate<GolemBase, IBlockState> LIT_PRED = 
			(golem, toReplace) -> golem.isProvidingLight();

	public EntityRedstoneLampGolem(final World world) {
		super(EntityRedstoneLampGolem.class, world, LAMP_PREFIX, VARIANTS);
		this.setCanTakeFallDamage(true);
		final IBlockState state = GolemItems.UTILITY_LIGHT.getDefaultState().with(BlockUtilityGlow.LIGHT_LEVEL, 15);
		this.tasks.addTask(9, new EntityAIUtilityBlock(this, state, BlockUtilityGlow.UPDATE_TICKS, 
				this.getConfigBool(ALLOW_SPECIAL), EntityAIUtilityBlock.getDefaultBiPred(state).and(LIT_PRED)));
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
	public String getModId() {
		return ExtraGolems.MODID;
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
