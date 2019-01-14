package com.golems.entity;

import com.golems.main.Config;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public final class EntityQuartzGolem extends GolemBase {

	public EntityQuartzGolem(final World world) {
		super(world, Config.QUARTZ.getBaseAttack(), Blocks.QUARTZ_BLOCK);
		this.setLootTableLoc("golem_quartz");
	}

	@Override
	protected ResourceLocation applyTexture() {
		return makeGolemTexture("quartz");
	}

	@Override
	protected void applyAttributes() {
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH)
				.setBaseValue(Config.QUARTZ.getMaxHealth());
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.29D);
	}

//	@Override
//	public void addGolemDrops(final List<WeightedItem> dropList, final boolean recentlyHit, final int lootingLevel) {
//		final int size = 4 + this.rand.nextInt(8 + lootingLevel * 2);
//		this.addDrop(dropList, new ItemStack(Items.QUARTZ, size > 16 ? 16 : size), 100);
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
