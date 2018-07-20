package com.golems.integration;

import java.util.LinkedList;
import java.util.List;

import com.golems.entity.EntityBedrockGolem;
import com.golems.entity.EntityBookshelfGolem;
import com.golems.entity.EntityCoalGolem;
import com.golems.entity.EntityCraftingGolem;
import com.golems.entity.EntityEndstoneGolem;
import com.golems.entity.EntityIceGolem;
import com.golems.entity.EntityLapisGolem;
import com.golems.entity.EntityLeafGolem;
import com.golems.entity.EntityMagmaGolem;
import com.golems.entity.EntityMelonGolem;
import com.golems.entity.EntityMushroomGolem;
import com.golems.entity.EntityNetherBrickGolem;
import com.golems.entity.EntityNetherWartGolem;
import com.golems.entity.EntityRedstoneGolem;
import com.golems.entity.EntitySlimeGolem;
import com.golems.entity.EntitySpongeGolem;
import com.golems.entity.EntityTNTGolem;
import com.golems.entity.GolemBase;
import com.golems.entity.GolemLightProvider;
import com.golems.entity.GolemMultiTextured;
import com.golems.main.Config;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.text.TextFormatting;

/**
 * Base class to get in-game information about types of golems. Currently used for Waila and The One
 * Probe integration.
 * 
 * @author sky01
 **/
public abstract class GolemDescriptionManager {

	protected boolean showSpecial;
	protected boolean showMultiTexture;
	protected boolean showAttack;
	protected boolean showFireproof;
	protected boolean showKnockbackResist;

	public GolemDescriptionManager() {
		this.showAttack = true;
		this.showMultiTexture = true;
		this.showSpecial = true;
		this.showFireproof = true;
		this.showKnockbackResist = false;
	}

	/**
	 * Checks the passed golem for various characteristics, making a String for each one
	 * 
	 * @return a LinkedList containing all descriptions that apply to the passed golem
	 **/
	public List<String> getEntityDescription(GolemBase golem) {
		List<String> list = new LinkedList<>();
		if (showAttack) {
			float attack = golem.getBaseAttackDamage();
			String sAttack = TextFormatting.GRAY + trans("entitytip.attack") + " : "
					+ TextFormatting.WHITE + attack;
			list.add(sAttack);
		}

		// add right-click-texture to tip if possible
		if (this.showMultiTexture
				&& (golem instanceof GolemMultiTextured || golem.doesInteractChangeTexture())) {
			String sColor = TextFormatting.BLUE + trans("entitytip.click_change_texture");
			list.add(sColor);
		}

		// add fire immunity to tip if possible
		if (this.showFireproof && golem.isImmuneToFire()
				&& !(golem instanceof EntityBedrockGolem)) {
			String sFire = TextFormatting.GOLD + trans("entitytip.is_fireproof");
			list.add(sFire);
		}

		// add knockback resist to tip if possible
		if (this.showKnockbackResist
				&& golem.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE)
						.getBaseValue() > 0.8999D) {
			String sResist = TextFormatting.GRAY + trans("entitytip.knockback_resist");
			list.add(sResist);
		}

		// add special information
		if (showSpecial) {
			// add glowing to tip if possible
			if (golem instanceof GolemLightProvider) {
				String sLight = TextFormatting.YELLOW + trans("entitytip.lights_area");
				list.add(sLight);
			}

			/////// BEGIN SPECIFIC CLASS CHECKS ////////

			// add indestructible to tip if possible
			if (golem.getClass() == EntityBedrockGolem.class) {
				String sIndestructible = TextFormatting.WHITE + "" + TextFormatting.BOLD
						+ trans("entitytip.indestructible");
				list.add(sIndestructible);
			}

			// add potion effect to tip if possible
			if (golem.getClass() == EntityBookshelfGolem.class) {
				String sPotion = TextFormatting.LIGHT_PURPLE
						+ trans("entitytip.grants_self_potion_effects");
				if (Config.BOOKSHELF.getBoolean(EntityBookshelfGolem.ALLOW_SPECIAL)) {
					list.add(sPotion);
				}
			}

			// add blinding effect to tip if possible
			if (golem.getClass() == EntityCoalGolem.class) {
				String sPotion = TextFormatting.GRAY + trans("entitytip.blinds_creatures");
				if (Config.COAL.getBoolean(EntityCoalGolem.ALLOW_SPECIAL)) {
					list.add(sPotion);
				}
			}

			// add interaction to tip if possible
			if (golem.getClass() == EntityCraftingGolem.class) {
				String sCraft = TextFormatting.BLUE + trans("entitytip.click_open_crafting");
				list.add(sCraft);
			}

			// add teleporting to tip if possible
			if (golem.getClass() == EntityEndstoneGolem.class) {
				String sTeleport = TextFormatting.DARK_AQUA + trans("entitytip.can_teleport");
				if (Config.ENDSTONE.getBoolean(EntityEndstoneGolem.ALLOW_SPECIAL)) {
					list.add(sTeleport);
				}
			}

			// add freezing to tip if possible
			if (golem.getClass() == EntityIceGolem.class) {
				String sFreeze = TextFormatting.AQUA + trans("entitytip.freezes_blocks");
				if (Config.ICE.getBoolean(EntityIceGolem.ALLOW_SPECIAL)) {
					list.add(sFreeze);
				}
			}

			// add potion effects to tip if possible
			if (golem.getClass() == EntityLapisGolem.class) {
				String sPotion = TextFormatting.LIGHT_PURPLE
						+ trans("entitytip.attacks_use_potion_effects");
				if (Config.LAPIS.getBoolean(EntityLapisGolem.ALLOW_SPECIAL)) {
					list.add(sPotion);
				}
			}

			// add potion effects to tip if possible
			if (golem.getClass() == EntityLeafGolem.class) {
				String sPotion = TextFormatting.DARK_GREEN + trans("entitytip.has_regen_1");
				if (Config.LEAF.getBoolean(EntityLeafGolem.ALLOW_SPECIAL)) {
					list.add(sPotion);
				}
			}

			// add fire to tip if possible
			if (golem.getClass() == EntityMagmaGolem.class) {
				String sLava = TextFormatting.RED
						+ trans("entitytip.slowly_melts", trans("tile.stonebrick.name"));
				String sFire = TextFormatting.RED + trans("entitytip.lights_mobs_on_fire");
				if (Config.MAGMA.getBoolean(EntityMagmaGolem.ALLOW_LAVA_SPECIAL)) {
					list.add(sLava);
				}
				if (Config.MAGMA.getBoolean(EntityMagmaGolem.ALLOW_FIRE_SPECIAL)) {
					list.add(sFire);
				}
			}

			// add planting to tip if possible
			if (golem.getClass() == EntityMelonGolem.class) {
				String sPlant = TextFormatting.GREEN + trans("entitytip.plants_flowers");
				if (Config.MELON.getBoolean(EntityMelonGolem.ALLOW_SPECIAL)) {
					list.add(sPlant);
				}
			}

			// add planting to tip if possible
			if (golem.getClass() == EntityMushroomGolem.class) {
				String sPlant = TextFormatting.DARK_GREEN + trans("entitytip.plants_shrooms");
				if (Config.MUSHROOM.getBoolean(EntityMushroomGolem.ALLOW_SPECIAL)) {
					list.add(sPlant);
				}
			}

			// add netherbrick specials to tip if possible
			if (golem.getClass() == EntityNetherBrickGolem.class) {
				String sFire = TextFormatting.RED + trans("entitytip.lights_mobs_on_fire");
				if (Config.NETHERBRICK.getBoolean(EntityNetherBrickGolem.ALLOW_FIRE_SPECIAL)) {
					list.add(sFire);
				}
			}

			// add planting to tip if possible
			if (golem.getClass() == EntityNetherWartGolem.class) {
				String sPlant = TextFormatting.RED + trans("entitytip.plants_warts");
				if (Config.NETHERWART.getBoolean(EntityNetherWartGolem.ALLOW_SPECIAL)) {
					list.add(sPlant);
				}
			}

			// add redstone power to tip if possible
			if (golem.getClass() == EntityRedstoneGolem.class) {
				String sRed = TextFormatting.RED + trans("entitytip.emits_redstone_signal");
				if (Config.REDSTONE.getBoolean(EntityRedstoneGolem.ALLOW_SPECIAL)) {
					list.add(sRed);
				}
			}

			// add knockback to tip if possible
			if (golem.getClass() == EntitySlimeGolem.class) {
				String sKnock = TextFormatting.GREEN + trans("entitytip.has_knockback");
				if (Config.SLIME.getBoolean(EntitySlimeGolem.ALLOW_SPECIAL)) {
					list.add(sKnock);
				}
			}

			// add water-drying to tip if possible
			if (golem.getClass() == EntitySpongeGolem.class) {
				String sWater = TextFormatting.YELLOW + trans("entitytip.absorbs_water");
				if (Config.SPONGE.getBoolean(EntitySpongeGolem.ALLOW_SPECIAL)) {
					list.add(sWater);
				}
			}

			// add boom to tip if possible
			if (golem.getClass() == EntityTNTGolem.class) {
				String sBoom = TextFormatting.RED + trans("entitytip.explodes");
				if (Config.TNT.getBoolean(EntityTNTGolem.ALLOW_SPECIAL)) {
					list.add(sBoom);
				}
			}
		}
		return list;
	}

	/**
	 * Helper method for translation
	 **/
	protected String trans(String s, Object... strings) {
		return I18n.format(s, strings);
	}
}
