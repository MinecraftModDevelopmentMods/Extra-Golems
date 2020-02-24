package com.golems.entity;

import com.golems.main.ExtraGolems;
import com.golems.util.GolemConfigSet;
import com.golems.util.GolemNames;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;

public final class EntityLeafGolem extends GolemColorized {

	public static final String ALLOW_SPECIAL = "Allow Special: Regeneration";

	private static final ResourceLocation TEXTURE_BASE = 
			GolemBase.makeTexture(ExtraGolems.MODID, GolemNames.LEAF_GOLEM);
	private static final ResourceLocation TEXTURE_OVERLAY = GolemBase
		.makeTexture(ExtraGolems.MODID, GolemNames.LEAF_GOLEM + "_grayscale");

	public EntityLeafGolem(final World world) {
		super(world, 0x5F904A, TEXTURE_BASE, TEXTURE_OVERLAY);
		this.setCanSwim(true);
		this.setLootTableLoc(GolemNames.LEAF_GOLEM);
		this.addHealItem(new ItemStack(Blocks.LEAVES, 1, OreDictionary.WILDCARD_VALUE), 0.75D);
		this.addHealItem(new ItemStack(Blocks.LEAVES2, 1, OreDictionary.WILDCARD_VALUE), 0.75D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.31D);
	}

	/**
	 * Called frequently so the entity can update its state every tick as required. For example,
	 * zombies and skeletons use this to react to sunlight and start to burn.
	 */
	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		GolemConfigSet cfg = getConfig(this);
		if (cfg.getBoolean(ALLOW_SPECIAL)
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
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_GRASS_STEP;
	}

	@Override
	public List<String> addSpecialDesc(final List<String> list) {
		if (getConfig(this).getBoolean(EntityLeafGolem.ALLOW_SPECIAL)) {
			list.add(TextFormatting.DARK_GREEN + trans("effect.regeneration") + " " 
				   + TextFormatting.DARK_GREEN + trans("enchantment.level.1"));
		}
		return list;
	}
}
