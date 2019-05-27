package com.mcmoddev.golems.integration;

import java.util.LinkedList;
import java.util.List;

import com.mcmoddev.golems.entity.EntityBedrockGolem;
import com.mcmoddev.golems.entity.base.GolemBase;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

/**
 * Base class to get in-game information about types of golems. Currently used for Waila and The One
 * Probe integration.
 *
 * @author sky01
 **/
public abstract class GolemDescriptionManager {

	protected boolean showSpecial = true;
	protected boolean showSpecialChild = false;
	protected boolean showMultiTexture = true;
	protected boolean showAttack = true;
	protected boolean showFireproof = true;
	protected boolean showKnockbackResist = false;

	public GolemDescriptionManager() {
		// empty constructor
	}

	/**
	 * Checks the passed golem for various characteristics, making a String for each one.
	 * Use this from a child class in order to populate your descriptions.
	 * @return a LinkedList containing all descriptions that apply to the passed golem
	 **/
	@SuppressWarnings("WeakerAccess")
	public List<ITextComponent> getEntityDescription(final GolemBase golem) {
		List<ITextComponent> list = new LinkedList<>();
		if (showAttack) {
			list.add(new TextComponentTranslation("entitytip.attack")
					.applyTextStyle(TextFormatting.GRAY)
					.appendSibling(new TextComponentString(Float.toString(golem.getBaseAttackDamage()))
							.applyTextStyle(TextFormatting.WHITE)));
		}

		// add right-click-texture to tip if possible
		if (this.showMultiTexture && (golem.canInteractChangeTexture())) {
			list.add(new TextComponentTranslation("entitytip.click_change_texture")
					.applyTextStyle(TextFormatting.BLUE));
		}

		// add fire immunity to tip if possible
		if (this.showFireproof && golem.isImmuneToFire()
			&& !(golem instanceof EntityBedrockGolem)) {
			list.add(new TextComponentTranslation("entitytip.is_fireproof")
					.applyTextStyle(TextFormatting.GOLD));
		}

		// add knockback resist to tip if possible
		if (this.showKnockbackResist
			&& golem.getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE)
			.getBaseValue() > 0.8999D) {
			list.add(new TextComponentTranslation("attribute.name.generic.knockbackResistance")
					.applyTextStyle(TextFormatting.GRAY));
		}

		// add special information
		if (showSpecial || (golem.isChild() && showSpecialChild)) {
			golem.getGolemContainer().addDescription(list);
		}
		return list;
	}

	/**
	 * Helper method for translation.
	 **/
	protected static String trans(final String s, final Object... strings) {
		return I18n.format(s, strings);
	}
}
