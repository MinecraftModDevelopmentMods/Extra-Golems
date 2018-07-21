package com.golems.entity;

import java.util.List;

import com.golems.main.Config;
import com.golems.util.WeightedItem;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public final class EntityLeafGolem extends GolemColorized {

	public static final String ALLOW_SPECIAL = "Allow Special: Regeneration";

	private static final ResourceLocation TEXTURE_BASE = GolemBase.makeGolemTexture("leaves");
	private static final ResourceLocation TEXTURE_OVERLAY = GolemBase
			.makeGolemTexture("leaves_grayscale");

	public EntityLeafGolem(final World world) {
		super(world, Config.LEAF.getBaseAttack(), new ItemStack(Blocks.LEAVES), 0x5F904A,
				TEXTURE_BASE, TEXTURE_OVERLAY);
		this.setCanSwim(true);
	}

	@Override
	protected void applyAttributes() {
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH)
				.setBaseValue(Config.LEAF.getMaxHealth());
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.31D);
	}

	/**
	 * Called frequently so the entity can update its state every tick as required. For example,
	 * zombies and skeletons use this to react to sunlight and start to burn.
	 */
	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		if (Config.LEAF.getBoolean(ALLOW_SPECIAL)
				&& this.getActivePotionEffect(MobEffects.REGENERATION) == null
				&& rand.nextInt(40) == 0) {
			this.addPotionEffect(
					new PotionEffect(MobEffects.REGENERATION, 200 + 20 * (1 + rand.nextInt(8)), 1));
		}

		if (this.ticksExisted % 10 == 2 && this.world.isRemote) {
			Biome biome = this.world.getBiome(this.getPosition());
			long color = biome.getFoliageColorAtPos(this.getPosition());
			this.setColor(color);
		}

		// slow falling for this entity
		if (this.motionY < -0.1D) {
			this.motionY *= 4.0D / 5.0D;
		}
	}

	@Override
	public void addGolemDrops(final List<WeightedItem> dropList, final boolean recentlyHit, final int lootingLevel) {
		this.addDrop(dropList, new ItemStack(Blocks.LEAVES, lootingLevel + 1), 100);
		this.addDrop(dropList, Blocks.SAPLING, 0, 1, 1, 20 + lootingLevel * 10);
		this.addDrop(dropList, Items.APPLE, 0, 1, 1, 15 + lootingLevel * 10);
		this.addDrop(dropList, Items.STICK, 0, 1, 2, 5 + lootingLevel * 10);
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_GRASS_STEP;
	}
}
