package com.golems.entity;

import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public final class EntityStrawGolem extends GolemBase {

	public EntityStrawGolem(final World world) {
		super(world);
		this.setCanSwim(true);
		this.setLootTableLoc("golem_straw");
		this.setBaseMoveSpeed(0.35D);
	}

	protected ResourceLocation applyTexture() {
		return makeGolemTexture("straw");
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_GRAVEL_STEP;
	}
}
