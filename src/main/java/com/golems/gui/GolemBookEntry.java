package com.golems.gui;

import java.util.ArrayList;
import java.util.List;

import com.golems.entity.EntityBedrockGolem;
import com.golems.entity.GolemBase;
import com.golems.entity.GolemMultiTextured;
import com.golems.util.GolemLookup;

import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.util.text.TextFormatting;
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
		
	public GolemBookEntry(GolemBase golem) {
		// initialize fields based on golem
		this.GOLEM_NAME = golem.getName();
		this.MULTI_TEXTURE = (golem instanceof GolemMultiTextured || golem.doesInteractChangeTexture());
		this.FIREPROOF = (golem.isImmuneToFire() && !(golem instanceof EntityBedrockGolem));
		this.HEALTH = (int)golem.getMaxHealth();
		this.ATTACK = golem.getBaseAttackDamage();
		this.SPECIALS = golem.addSpecialDesc(new ArrayList<String>());
		
		
		// set the block and block name if it exists
		Block b = GolemLookup.getBuildingBlock(golem.getClass());
		// Blocks.AIR means there is no building block
		this.BLOCK = b != null ? b : Blocks.AIR;
		// add golem's special descriptions to the searchable string
		// initialize the block and search-string
		final StringBuilder searchable = new StringBuilder();
		final List<String> specials = golem.addSpecialDesc(new ArrayList<String>());
		
		for(String s : specials) {
			searchable.append("-" + TextFormatting.getTextWithoutFormattingCodes(s));
		}
		// lowercase string for searching
		this.SEARCHABLE = searchable.toString().toLowerCase();
	}
	
	/** @return the localized version of this golem's name **/
	public String getGolemName() {
		return trans(this.GOLEM_NAME);
	}
	
	/** @return the unlocalized version of this golem's name **/
	public String getGolemNameRaw() {
		return this.GOLEM_NAME;
	}
	
	/** @return the Block in this entry **/
	public Block getBlock() {
		return this.BLOCK;
	}
	
	/** @return all Golem Stats as one String **/
	public String getDescriptionPage() {
		// re-make each time for real-time localization
		return makePage();
	}
	
	/** Temporarily here until we make parts of the page separately **/
	private String makePage() {
		StringBuilder page = new StringBuilder();
		// ADD HEALTH (ROUNDED) TIP
		page.append("\n" + TextFormatting.GRAY + trans("entitytip.health") + ": " + TextFormatting.BLACK
				+ this.HEALTH + TextFormatting.DARK_RED + " \u2764" + TextFormatting.BLACK);
		// ADD ATTACK POWER TIP
		page.append("\n" + TextFormatting.GRAY + trans("entitytip.attack") + ": "
				+ TextFormatting.BLACK + this.ATTACK + " \u2694" + "\n");
		// ADD FIREPROOF TIP
		if (this.FIREPROOF) {
			page.append("\n" + TextFormatting.GOLD + trans("entitytip.is_fireproof"));
		}
		// ADD INTERACT-TEXTURE TIP
		if (this.MULTI_TEXTURE) {
			page.append("\n" + TextFormatting.BLUE + trans("entitytip.click_change_texture"));
		}
		// ADD SPECIALS
		for(String s : this.SPECIALS) {
			page.append("\n" + s.replaceAll(TextFormatting.WHITE.toString(), TextFormatting.BLACK.toString()));
		}
		
		return page.toString();
	}
	
	/** 
	 * For use if the Golem Book gets a Search bar.
	 * Contains the golem's name, building block, and
	 * any special abilities, all localized and lowercased
	 * @return an all-lowercase String to search for input
	 **/
	public String getSearchableString() {
		return SEARCHABLE + "-" + getGolemName().toLowerCase() + "-" + getBlock().getLocalizedName().toLowerCase();
	}
	
	/** Helper method for translating text into local language using {@code I18n} **/
	protected static String trans(final String s, final Object... strings) {
		return I18n.format(s, strings);
	}
	
	@Override
	public String toString() {
		return "[Block=" + this.BLOCK.getLocalizedName() + "; Golem=" + trans(this.GOLEM_NAME)
			+ "; Desc=" + this.getDescriptionPage().replaceAll("\n", "; ");
	}
}
