package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.blocks.BlockUtilityPower;
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

public final class EntityRedstoneGolem extends GolemBase {

	public static final String ALLOW_SPECIAL = "Allow Special: Redstone Power";

	public EntityRedstoneGolem(final World world) {
		super(EntityRedstoneGolem.class, world);
		final IBlockState state = GolemItems.UTILITY_POWER.getDefaultState().with(BlockUtilityPower.POWER_LEVEL, 15);
		final int freq = BlockUtilityPower.UPDATE_TICKS;
		final boolean allow = this.getConfigBool(ALLOW_SPECIAL);
		this.tasks.addTask(9, new EntityAIUtilityBlock(this, state, freq, allow));
	}

	@Override
	protected ResourceLocation applyTexture() {
		return makeTexture(ExtraGolems.MODID, GolemNames.REDSTONE_GOLEM);
	}
	

	@Override
	public boolean isProvidingPower() {
		return true;
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_STONE_STEP;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public int getBrightnessForRender() {
		return super.getBrightnessForRender() + 64;
	}
}
