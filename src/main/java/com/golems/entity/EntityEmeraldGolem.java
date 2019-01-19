package com.golems.entity;

import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public final class EntityEmeraldGolem extends GolemBase {

	public EntityEmeraldGolem(final World world) {
		super(world);
		this.setLootTableLoc("golem_emerald");
		this.setBaseMoveSpeed(0.28D);
	}

	@Override
	protected ResourceLocation applyTexture() {
		return makeGolemTexture("emerald_block");
	}
	
	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_STONE_STEP;
	}
}
