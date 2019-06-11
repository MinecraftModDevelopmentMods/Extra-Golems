package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.GolemNames;

import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public final class EntityBookshelfGolem extends GolemBase {

	public static final String ALLOW_SPECIAL = "Allow Special: Potion Effects";
	/*private static final Potion[] goodEffects = {MobEffects.FIRE_RESISTANCE, MobEffects.REGENERATION,
		MobEffects.STRENGTH, MobEffects.ABSORPTION, MobEffects.LUCK, MobEffects.INSTANT_HEALTH,
		MobEffects.RESISTANCE, MobEffects.INVISIBILITY, MobEffects.SPEED,
		MobEffects.JUMP_BOOST};
	*/ // TODO Effects.<SRG name>

	public EntityBookshelfGolem(final World world) {
		super(GolemNames.BOOKSHELF_GOLEM, world);
	}

	protected ResourceLocation applyTexture() {
		return makeTexture(ExtraGolems.MODID, GolemNames.BOOKSHELF_GOLEM);
	}

	/**
	 * Called frequently so the entity can update its state every tick as required. For example,
	 * zombies and skeletons use this to react to sunlight and start to burn.
	 */

	@Override
	public void livingTick() {
		super.livingTick();
		if (getConfigBool(ALLOW_SPECIAL) && this.getActivePotionEffects().isEmpty()
			&& rand.nextInt(40) == 0) {
			final Potion potion = goodEffects[rand.nextInt(goodEffects.length)];
			final int len = potion.isInstant() ? 1 : 200 + 100 * (1 + rand.nextInt(5));
			this.addPotionEffect(new PotionEffect(potion, len, rand.nextInt(2)));
		}
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_WOOD_STEP;
	}
}
