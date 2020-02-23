package com.golems.entity;

import com.golems.main.ExtraGolems;
import com.golems.util.GolemNames;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public final class EntityEmeraldGolem extends GolemBase {

	public EntityEmeraldGolem(final World world) {
		super(world);
		this.setLootTableLoc(GolemNames.EMERALD_GOLEM);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.28D);
		this.addHealItem(new ItemStack(Items.EMERALD), 0.25D);
	}

	@Override
	protected ResourceLocation applyTexture() {
		return makeTexture(ExtraGolems.MODID, GolemNames.EMERALD_GOLEM);
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_STONE_STEP;
	}
}
