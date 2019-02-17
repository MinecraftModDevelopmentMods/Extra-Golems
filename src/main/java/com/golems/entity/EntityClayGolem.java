package com.golems.entity;

import com.golems.main.ExtraGolems;
import com.golems.util.GolemNames;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public final class EntityClayGolem extends GolemBase {

	public EntityClayGolem(final World world) {
		super(GolemEntityTypes.CLAY, world);
		this.setLootTableLoc(GolemNames.CLAY_GOLEM);
	}

	@Override
	protected ResourceLocation applyTexture() {
		return makeTexture(ExtraGolems.MODID, GolemNames.CLAY_GOLEM);
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_GRAVEL_STEP;
	}
}
