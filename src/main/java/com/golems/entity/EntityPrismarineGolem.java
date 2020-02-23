package com.golems.entity;

import java.util.List;

import com.golems.main.ExtraGolems;
import com.golems.util.GolemNames;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public final class EntityPrismarineGolem extends GolemBase {

	public EntityPrismarineGolem(final World world) {
		super(world);
		this.setLootTableLoc(GolemNames.PRISMARINE_GOLEM);
		this.addHealItem(new ItemStack(Items.PRISMARINE_SHARD), 0.25D);
	}

	@Override
	protected ResourceLocation applyTexture() {
		return makeTexture(ExtraGolems.MODID, GolemNames.PRISMARINE_GOLEM);
	}

	/**
	 * Called frequently so the entity can update its state every tick as required. For example,
	 * zombies and skeletons use this to react to sunlight and start to burn.
	 */
	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		if (this.isInWater()) {
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.41D);
		} else {
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.24D);
		}
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_STONE_STEP;
	}
	
	@Override
	public List<String> addSpecialDesc(final List<String> list) {
		list.add(TextFormatting.AQUA + trans("entitytip.breathes_underwater"));
		return list;
	}
}
