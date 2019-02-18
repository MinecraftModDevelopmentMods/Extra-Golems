package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.GolemNames;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public final class EntityObsidianGolem extends GolemBase {

	public EntityObsidianGolem(final World world) {
		super(GolemEntityTypes.OBSIDIAN, world);
		this.setLootTableLoc(GolemNames.OBSIDIAN_GOLEM);
		this.setImmuneToFire(true);
	}

	@Override
	protected ResourceLocation applyTexture() {
		return makeTexture(ExtraGolems.MODID, GolemNames.OBSIDIAN_GOLEM);
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_STONE_STEP;
	}
}
