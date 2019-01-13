package com.golems.entity;

import com.golems.main.Config;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public final class EntityGoldGolem extends GolemBase {

	public EntityGoldGolem(final World world) {
		super(world, Config.GOLD.getBaseAttack(), Blocks.GOLD_BLOCK);
	}

	protected ResourceLocation applyTexture() {
		return makeGolemTexture("gold");
	}

	@Override
	protected void applyAttributes() {
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH)
				.setBaseValue(Config.GOLD.getMaxHealth());
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.19D);
		this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.9D);
	}

//	@Override
//	public void addGolemDrops(final List<WeightedItem> dropList, final boolean recentlyHit, final int lootingLevel) {
//		final int size = 6 + this.rand.nextInt(8 + lootingLevel * 4);
//		this.addDrop(dropList, new ItemStack(Items.GOLD_INGOT, size), 100);
//	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_STONE_STEP;
	}
}
