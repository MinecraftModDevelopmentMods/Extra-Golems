package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.GolemConfigSet;
import com.mcmoddev.golems.util.GolemNames;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.List;

public final class EntityNetherBrickGolem extends GolemBase {

	public static final String ALLOW_FIRE_SPECIAL = "Allow Special: Burn Enemies";

	public EntityNetherBrickGolem(final World world) {
		super(EntityNetherBrickGolem.class, world);
		this.setImmuneToFire(true);
		this.setLootTableLoc(GolemNames.NETHERBRICK_GOLEM);
		this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.28D);
	}

	@Override
	protected ResourceLocation applyTexture() {
		return makeTexture(ExtraGolems.MODID, GolemNames.NETHERBRICK_GOLEM);
	}

	/**
	 * Attack by lighting on fire as well.
	 */
	@Override
	public boolean attackEntityAsMob(final Entity entity) {
		if (super.attackEntityAsMob(entity)) {
			final GolemConfigSet cfg = getConfig(this);
			if (cfg.getBoolean(ALLOW_FIRE_SPECIAL)) {
				entity.setFire(2 + rand.nextInt(5));
			}
			return true;
		}
		return false;
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_STONE_STEP;
	}

	@Override
	public List<String> addSpecialDesc(final List<String> list) {
		if (getConfig(this).getBoolean(EntityNetherBrickGolem.ALLOW_FIRE_SPECIAL))
			list.add(TextFormatting.RED + trans("entitytip.lights_mobs_on_fire"));
		return list;
	}
}
