package com.golems.entity;

import com.golems.main.Config;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public final class EntityGlassGolem extends GolemBase {

	public EntityGlassGolem(final World world) {
		super(world, Config.GLASS.getBaseAttack(), Blocks.GLASS);
		this.setCanTakeFallDamage(true);
		this.setLootTableLoc("golem_glass");
	}

	protected ResourceLocation applyTexture() {
		return makeGolemTexture("glass");
	}

	@Override
	protected void applyAttributes() {
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH)
				.setBaseValue(Config.GLASS.getMaxHealth());
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30D);
	}

//	@Override
//	public void addGolemDrops(final List<WeightedItem> dropList, final boolean recentlyHit, final int lootingLevel) {
//		this.addDrop(dropList, Blocks.GLASS, 0, lootingLevel, lootingLevel + 1, 90);
//	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_GLASS_STEP;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.BLOCK_GLASS_BREAK;
	}
}
