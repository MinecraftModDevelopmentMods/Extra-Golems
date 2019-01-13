package com.golems.entity;

import com.golems.main.Config;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public final class EntityObsidianGolem extends GolemBase {

	public EntityObsidianGolem(final World world) {
		super(world, Config.OBSIDIAN.getBaseAttack(), Blocks.OBSIDIAN);
	}

	@Override
	protected ResourceLocation applyTexture() {
		return makeGolemTexture("obsidian");
	}

	@Override
	protected void applyAttributes() {
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH)
				.setBaseValue(Config.OBSIDIAN.getMaxHealth());
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.22D);
	}

//	@Override
//	public void addGolemDrops(final List<WeightedItem> dropList, final boolean recentlyHit, final int lootingLevel) {
//		final int size = 1 + this.rand.nextInt(2 + (lootingLevel > 2 ? 2 : lootingLevel));
//		this.addDrop(dropList, new ItemStack(Blocks.OBSIDIAN, size), 100);
//	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_STONE_STEP;
	}
}
