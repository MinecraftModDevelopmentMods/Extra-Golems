package com.golems.gui;

import com.golems.entity.EntityBedrockGolem;
import com.golems.entity.GolemBase;
import com.golems.entity.GolemMultiTextured;
import com.golems.util.GolemLookup;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;

/**
 * This class will be used to easily connect
 * golems and their blocks and other info
 * to use in the Golem Book.
 **/
public class GolemBookEntry {

	private final Block BLOCK;
	private final String GOLEM_NAME;
	private final boolean MULTI_TEXTURE;
	private final boolean FIREPROOF;
	private final int HEALTH;
	private final float ATTACK;
	private final List<String> SPECIALS;

	private String SEARCHABLE;

	private final String PAGE;

	public GolemBookEntry(GolemBase golem) {
		// initialize fields based on golem
		this.GOLEM_NAME = golem.getName();
		this.MULTI_TEXTURE = (golem instanceof GolemMultiTextured || golem.doesInteractChangeTexture());
		this.FIREPROOF = (golem.isImmuneToFire() && !(golem instanceof EntityBedrockGolem));
		this.HEALTH = (int) golem.getMaxHealth();
		this.ATTACK = golem.getBaseAttackDamage();
		this.SPECIALS = golem.addSpecialDesc(new ArrayList<String>());

		// initialize the block and search-string
		this.SEARCHABLE = GOLEM_NAME;
		// set the block and block name if it exists
		Block b = GolemLookup.getBuildingBlock(golem.getClass());
		if (b != null) {
			this.BLOCK = b;
			this.SEARCHABLE += " - " + b.getLocalizedName();
		} else this.BLOCK = Blocks.AIR;
		// add golem's special descriptions to the searchable string
		final List<String> specials = golem.addSpecialDesc(new ArrayList<String>());
		StringBuilder sb = new StringBuilder();
		for (String s : specials) {
			sb.append(" - ");
			sb.append(TextFormatting.getTextWithoutFormattingCodes(s));
		}
		SEARCHABLE = sb.toString();
		// lowercase string for searching
		this.SEARCHABLE = this.SEARCHABLE.toLowerCase();

		// make the page for this entry
		this.PAGE = makePage();
	}

	/**
	 * Temporarily here until we make parts of the page separately
	 **/
	public String getPageString() {
		return this.PAGE;
	}

	/**
	 * Temporarily here until we make parts of the page separately
	 **/
	private String makePage() {
		StringBuilder page = new StringBuilder();
		// ADD BLOCK TIP
		page.append(TextFormatting.GRAY).append(I18n.format("itemGroup.buildingBlocks")).append(": ").append(TextFormatting.BLACK).append(BLOCK.getLocalizedName()).append("\n");
		// ADD NAME TIP
		page.append("\n").append(TextFormatting.GRAY).append(trans("entitytip.name")).append(": ").append(TextFormatting.BLACK).append(this.GOLEM_NAME).append("\n");
		// ADD HEALTH (ROUNDED) TIP
		page.append("\n").append(TextFormatting.GRAY).append(trans("entitytip.health")).append(": ").append(TextFormatting.BLACK).append(this.HEALTH).append(TextFormatting.DARK_RED).append(" \u2764").append(TextFormatting.BLACK);
		// ADD ATTACK POWER TIP
		page.append("\n").append(TextFormatting.GRAY).append(trans("entitytip.attack")).append(": ").append(TextFormatting.BLACK).append(this.ATTACK).append(" \u2694").append("\n");
		// ADD FIREPROOF TIP
		if (this.FIREPROOF) {
			page.append("\n").append(TextFormatting.GOLD).append(trans("entitytip.is_fireproof"));
		}
		// ADD INTERACT-TEXTURE TIP
		if (this.MULTI_TEXTURE) {
			page.append("\n").append(TextFormatting.BLUE).append(trans("entitytip.click_change_texture"));
		}
		// ADD SPECIALS
		for (String s : this.SPECIALS) {
			page.append("\n").append(s.replaceAll(TextFormatting.WHITE.toString(), TextFormatting.BLACK.toString()));
		}

		return page.toString();
	}

	/**
	 * @return the localized version of this golem's name
	 **/
	public String getGolemName() {
		return GOLEM_NAME;
	}

	/**
	 * @return the Block in this entry
	 **/
	public Block getBlock() {
		return this.BLOCK;
	}

	/**
	 * For use if the Golem Book gets a Search bar.
	 * Contains the golem's name, building block, and
	 * any special abilities, all localized and lowercased
	 *
	 * @return an all-lowercase String to search for input
	 **/
	public String getSearchableString() {
		return SEARCHABLE;
	}

	/**
	 * Helper method for translating text into local language using {@code I18n}
	 **/
	protected static String trans(final String s, final Object... strings) {
		return I18n.format(s, strings);
	}
}
