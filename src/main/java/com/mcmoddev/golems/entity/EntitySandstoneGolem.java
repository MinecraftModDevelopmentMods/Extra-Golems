package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.GolemNames;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public final class EntitySandstoneGolem extends GolemBase {

	public EntitySandstoneGolem(final World world) {
		super(GolemNames.SANDSTONE_GOLEM, world);
		this.getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.1D);
	}

	protected ResourceLocation applyTexture() {
		return makeTexture(ExtraGolems.MODID, GolemNames.SANDSTONE_GOLEM);
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_STONE_STEP;
	}
}
