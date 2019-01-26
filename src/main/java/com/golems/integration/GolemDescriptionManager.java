package com.golems.integration;

import com.golems.entity.*;
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
	 * Use this from a child class in order to populate your descriptions.
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
			final String sResist = TextFormatting.GRAY + trans("attribute.name.generic.knockbackResistance");
			list.add(sResist);
		}

		// add special information
		if (showSpecial) {
			golem.addSpecialDesc(list);
		}
		return list;
	}

	/**
	 * Helper method for translation.
	 **/
	protected static String trans(final String s, final Object... strings) {
		return I18n.format(s, strings);
	}

	@Deprecated
	protected List<String> addSpecial(List<String> list, GolemBase golem) {
		return list;
	}
}
