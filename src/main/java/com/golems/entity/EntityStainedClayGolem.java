package com.golems.entity;

import java.util.List;

import com.golems.main.Config;
import com.golems.util.WeightedItem;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public final class EntityStainedClayGolem extends GolemColorizedMultiTextured {

	public static final String DROP_META = "Drop Metadata";

	private static final ResourceLocation TEXTURE_BASE = GolemBase.makeGolemTexture("stained_clay");
	private static final ResourceLocation TEXTURE_OVERLAY = GolemBase
			.makeGolemTexture("stained_clay_grayscale");

	public EntityStainedClayGolem(final World world) {
		super(world, Config.STAINED_CLAY.getBaseAttack(), Blocks.STAINED_HARDENED_CLAY,
				TEXTURE_BASE, TEXTURE_OVERLAY, ItemDye.DYE_COLORS);
	}

	@Override
	protected void applyAttributes() {
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH)
				.setBaseValue(Config.STAINED_CLAY.getMaxHealth());
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.20D);
	}

	@Override
	public void addGolemDrops(final List<WeightedItem> dropList, final boolean recentlyHit, final int lootingLevel) {
		final int keyReturn = Config.STAINED_CLAY.getInt(DROP_META);
		final int meta = keyReturn < 0 ? 15 - this.getTextureNum() : keyReturn;
		final int size = 1 + lootingLevel + rand.nextInt(3);
		this.addDrop(dropList,
				new ItemStack(Blocks.STAINED_HARDENED_CLAY, size > 4 ? 4 : size, meta), 100);
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_STONE_STEP;
	}
}
