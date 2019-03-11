package com.mcmoddev.golems.gui;

import com.mcmoddev.golems.entity.EntityBedrockGolem;
import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.entity.base.GolemMultiTextured;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.config.GolemRegistrar;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityType;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
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
	private ResourceLocation IMAGE = null;
	private final boolean MULTI_TEXTURE;
	private final boolean FIREPROOF;
	private final int HEALTH;
	private final float ATTACK;
	private final List<String> SPECIALS;

	public GolemBookEntry(@Nonnull GolemBase golem) {
		// initialize fields based on golem attributes
		EntityType<?> golemType = GolemRegistrar.getContainer(golem.getClass()).entityType;
		this.GOLEM_NAME = golemType.getTranslationKey();
		this.MULTI_TEXTURE = (golem instanceof GolemMultiTextured || golem.doesInteractChangeTexture());
		this.FIREPROOF = (golem.isImmuneToFire() && !(golem instanceof EntityBedrockGolem));
		this.HEALTH = (int) golem.getMaxHealth();
		this.ATTACK = golem.getBaseAttackDamage();
		this.SPECIALS = golem.addSpecialDesc(new ArrayList<String>());

		// set the block and block name if it exists
		Block b = GolemRegistrar.getContainer(golem.getClass()).getPrimaryBuildingBlock();
		// Blocks.AIR means there is no building block
		this.BLOCK = b != null ? b : Blocks.AIR;
		
		// find the image to add to the book
		String img = (ExtraGolems.MODID + ":textures/gui/screenshots/").concat(golemType.getTranslationKey()).concat(".png");
		try {
			this.IMAGE = Minecraft.getInstance().getResourceManager().getResource(new ResourceLocation(img)).getLocation();
			//System.out.println("Image found, yay! Loading " + img.toString() + " for " + this.GOLEM_NAME);
		} catch (IOException e) {
			//System.out.println("No image found, skipping " + img.toString() + " for " + this.GOLEM_NAME);
		}
	}

	/**
	 * @return the localized version of this golem's name
	 **/
	public String getGolemName() {
		return trans(this.GOLEM_NAME);
	}

	/**
	 * @return the unlocalized version of this golem's name
	 **/
	public String getGolemNameRaw() {
		return this.GOLEM_NAME;
	}

	/**
	 * @return the Block in this entry
	 **/
	public Block getBlock() {
		return this.BLOCK;
	}
	
	/**
	 * @return the Block in this entry
	 **/
	public String getBlockName() {
		return this.BLOCK.getNameTextComponent().toString();
	}
	
	/**
	 * @return the attack power of this golem
	 **/
	public float getAttack() {
		return this.ATTACK;
	}
	
	/** 
	 * @return the number of special descriptions added by this golem 
	 **/
	public int getDescriptionSize() {
		return SPECIALS.size();
	}

	/**
	 * @return all Golem Stats as one String
	 **/
	public String getDescriptionPage() {
		// re-make each time for real-time localization
		return makePage();
	}
	
	/** 
	 * @return Whether or not an image was found to add to the page
	 **/
	public boolean hasImage() {
		return this.IMAGE != null;
	}
	
	/** 
	 * @return the ResourceLocation of an image to include, if it exists .
	 * @see #hasImage()
	 **/
	@Nullable
	public ResourceLocation getImageResource() {
		return this.IMAGE;
	}

	/**
	 * Concatenates the golem's stats and specials into a single STring
	 **/
	private String makePage() {
		StringBuilder page = new StringBuilder();
		// ADD (ROUNDED) HEALTH TIP
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
		for (String s : this.SPECIALS) {
			page.append("\n" + s.replaceAll(TextFormatting.WHITE.toString(), TextFormatting.BLACK.toString()));
		}
		
		return page.toString();
	}
	
	/** Helper method for translating text into local language using {@code I18n} **/
	protected static String trans(final String s, final Object... strings) {
		return I18n.format(s, strings);
	}

	@Override
	public String toString() {
		return "[Block=" + this.BLOCK.getTranslationKey() + "; Golem=" + trans(this.GOLEM_NAME)
			+ "; Desc=" + this.getDescriptionPage().replaceAll("\n", "; ");
	}
}
