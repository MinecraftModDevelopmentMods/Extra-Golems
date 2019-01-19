package com.golems.entity;

import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public final class EntitySandstoneGolem extends GolemBase {

	public EntitySandstoneGolem(final World world) {
		super(world);
		this.setLootTableLoc("golem_sandstone");
		this.setBaseMoveSpeed(0.28D);
	}

	protected ResourceLocation applyTexture() {
		return makeGolemTexture("sandstone");
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_STONE_STEP;
	}
}
