package com.golems.integration;

import com.golems.entity.*;
import com.golems.main.Config;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.text.TextFormatting;

import java.util.LinkedList;
import java.util.List;

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
	 * Checks the passed golem for various characteristics, making a String for each one.
	 *
	 * @return a LinkedList containing all descriptions that apply to the passed golem
	 **/
	@SuppressWarnings("WeakerAccess")
	public List<String> getEntityDescription(final GolemBase golem) {
		List<String> list = new LinkedList<>();
		if (showAttack) {
			list.add(TextFormatting.GRAY + trans("entitytip.attack") + " : "
					+ TextFormatting.WHITE + golem.getBaseAttackDamage());
		}

		// add right-click-texture to tip if possible
		if (this.showMultiTexture
				&& (golem instanceof GolemMultiTextured || golem.doesInteractChangeTexture())) {
			list.add(TextFormatting.BLUE + trans("entitytip.click_change_texture"));
		}

		// add fire immunity to tip if possible
		if (this.showFireproof && golem.isImmuneToFire()
				&& !(golem instanceof EntityBedrockGolem)) {
			list.add(TextFormatting.GOLD + trans("entitytip.is_fireproof"));
		}

		// add knockback resist to tip if possible
		if (this.showKnockbackResist
				&& golem.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE)
						.getBaseValue() > 0.8999D) {
			final String sResist = TextFormatting.GRAY + trans("entitytip.knockback_resist");
			list.add(sResist);
		}

		// add special information
		if (showSpecial) {
			list = addSpecial(list, golem);
		}
		return list;
	}

	/**
	 * Helper method for translation.
	 **/
	protected String trans(final String s, final Object... strings) {
		return I18n.format(s, strings);
	}

	protected List<String> addSpecial(List<String> list, GolemBase golem) {



		/////// BEGIN SPECIFIC CLASS CHECKS ////////

		// add indestructible to tip if possible
		if (golem.getClass() == EntityBedrockGolem.class) {
			list.add(TextFormatting.WHITE + "" + TextFormatting.BOLD
					+ trans("entitytip.indestructible"));
		}

		// add potion effect to tip if possible
		else if (golem.getClass() == EntityBookshelfGolem.class && Config.BOOKSHELF.getBoolean(EntityBookshelfGolem.ALLOW_SPECIAL)) {
			list.add(TextFormatting.LIGHT_PURPLE + trans("entitytip.grants_self_potion_effects"));
		}

		// add blinding effect to tip if possible
		else if (golem.getClass() == EntityCoalGolem.class && Config.COAL.getBoolean(EntityCoalGolem.ALLOW_SPECIAL)) {
			list.add(TextFormatting.GRAY + trans("entitytip.blinds_creatures"));
		}

		// add interaction to tip if possible
		else if (golem.getClass() == EntityCraftingGolem.class && Config.ENDSTONE.getBoolean(EntityEndstoneGolem.ALLOW_SPECIAL)) {
			list.add(TextFormatting.BLUE + trans("entitytip.click_open_crafting"));
		}

		// add teleporting to tip if possible
		else if (golem.getClass() == EntityEndstoneGolem.class && Config.ENDSTONE.getBoolean(EntityEndstoneGolem.ALLOW_SPECIAL)) {
			list.add(TextFormatting.DARK_AQUA + trans("entitytip.can_teleport"));

		}

		// add freezing to tip if possible
		else if (golem.getClass() == EntityIceGolem.class && Config.ICE.getBoolean(EntityIceGolem.ALLOW_SPECIAL)) {
			list.add(TextFormatting.AQUA + trans("entitytip.freezes_blocks"));
		}

		// add potion effects to tip if possible
		else if (golem.getClass() == EntityLapisGolem.class && Config.LAPIS.getBoolean(EntityLapisGolem.ALLOW_SPECIAL)) {
			list.add( TextFormatting.LIGHT_PURPLE
					+ trans("entitytip.attacks_use_potion_effects"));
		}

		// add potion effects to tip if possible
		else if (golem.getClass() == EntityLeafGolem.class && Config.LEAF.getBoolean(EntityLeafGolem.ALLOW_SPECIAL)) {
			list.add(TextFormatting.DARK_GREEN + trans("entitytip.has_regen_1"));
		}

		// add fire to tip if possible
		else if (golem.getClass() == EntityMagmaGolem.class) {
			if (Config.MAGMA.getBoolean(EntityMagmaGolem.ALLOW_LAVA_SPECIAL)) {
				list.add(TextFormatting.RED
						+ trans("entitytip.slowly_melts", trans("tile.stonebrick.name")));
			}
			if (Config.MAGMA.getBoolean(EntityMagmaGolem.ALLOW_FIRE_SPECIAL)) {
				list.add(TextFormatting.RED + trans("entitytip.lights_mobs_on_fire"));
			}
		}

		// add planting to tip if possible
		else if (golem.getClass() == EntityMelonGolem.class && Config.MELON.getBoolean(EntityMelonGolem.ALLOW_SPECIAL)) {
			list.add(TextFormatting.GREEN + trans("entitytip.plants_flowers"));
		}

		// add planting to tip if possible
		else if (golem.getClass() == EntityMushroomGolem.class && Config.MUSHROOM.getBoolean(EntityMushroomGolem.ALLOW_SPECIAL)) {
			list.add(TextFormatting.DARK_GREEN + trans("entitytip.plants_shrooms"));
		}

		// add netherbrick specials to tip if possible
		else if (golem.getClass() == EntityNetherBrickGolem.class && Config.NETHERBRICK.getBoolean(EntityNetherWartGolem.ALLOW_SPECIAL)) {
			list.add(TextFormatting.RED + trans("entitytip.lights_mobs_on_fire"));
		}

		// add planting to tip if possible
		else if (golem.getClass() == EntityNetherWartGolem.class && Config.NETHERWART.getBoolean(EntityNetherWartGolem.ALLOW_SPECIAL)) {
			list.add(TextFormatting.RED + trans("entitytip.plants_warts"));

		}

		// add knockback to tip if possible
		else if (golem.getClass() == EntitySlimeGolem.class && Config.SLIME.getBoolean(EntitySlimeGolem.ALLOW_SPECIAL)) {
			list.add(TextFormatting.GREEN + trans("entitytip.has_knockback"));
		}

		// add water-drying to tip if possible
		else if (golem.getClass() == EntitySpongeGolem.class && Config.SPONGE.getBoolean(EntitySpongeGolem.ALLOW_SPECIAL)) {
			list.add(TextFormatting.YELLOW + trans("entitytip.absorbs_water"));
		}

		// add boom to tip if possible
		else if (golem.getClass() == EntityTNTGolem.class && Config.TNT.getBoolean(EntityTNTGolem.ALLOW_SPECIAL)) {
			list.add(TextFormatting.RED + trans("entitytip.explodes"));
		}

		// add immunity to drowning if possible
		else if (golem.getClass() == EntitySeaLanternGolem.class && Config.SEA_LANTERN.getBoolean(EntitySeaLanternGolem.ALLOW_SPECIAL)) {
			list.add(TextFormatting.AQUA + trans("entitytip.breathes_underwater"));
		}
		return list;
	}

}
