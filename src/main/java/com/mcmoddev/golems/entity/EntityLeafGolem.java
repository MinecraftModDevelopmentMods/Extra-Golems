package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.entity.base.GolemColorized;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.GolemNames;

import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public final class EntityLeafGolem extends GolemColorized {

	public static final String ALLOW_SPECIAL = "Allow Special: Regeneration";

	private static final ResourceLocation TEXTURE_BASE =
			GolemBase.makeTexture(ExtraGolems.MODID, GolemNames.LEAF_GOLEM);
	private static final ResourceLocation TEXTURE_OVERLAY = GolemBase
		.makeTexture(ExtraGolems.MODID, GolemNames.LEAF_GOLEM + "_grayscale");

	public EntityLeafGolem(final World world) {
		super(EntityLeafGolem.class, world, 0x5F904A, TEXTURE_BASE, TEXTURE_OVERLAY);
		this.setCanSwim(true);
	}

	/**
	 * Called frequently so the entity can update its state every tick as required. For example,
	 * zombies and skeletons use this to react to sunlight and start to burn.
	 */
	@Override
	public void livingTick() {
		super.livingTick();
		if (this.getConfigBool(ALLOW_SPECIAL) && this.getActivePotionEffect(MobEffects.REGENERATION) == null
			&& rand.nextInt(40) == 0) {
			this.addPotionEffect(
				new PotionEffect(MobEffects.REGENERATION, 200 + 20 * (1 + rand.nextInt(8)), rand.nextInt(2)));
		}

		if (this.ticksExisted % 10 == 2 && this.world.isRemote) {
			Biome biome = this.world.getBiome(this.getPosition());
			long color = biome.getFoliageColor(this.getPosition());
			this.setColor(color);
		}

		// slow falling for this entity
		if (this.motionY < -0.05D) {
			this.motionY *= 4.0D / 5.0D;
		}
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_GRASS_STEP;
	}
}
