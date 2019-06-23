package com.mcmoddev.golems.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mcmoddev.golems.entity.base.GolemMultiColorized;
import com.mcmoddev.golems.entity.base.GolemMultiTextured;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.config.ExtraGolemsConfig;
import com.mcmoddev.golems.util.config.GolemContainer;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
/**
 * This class will be used to easily connect
 * golems and their blocks and other info
 * to use in the Golem Book.
 **/
public class GolemBookEntry {

	private final Block[] BLOCKS;
	private final String GOLEM_NAME;
	private ResourceLocation IMAGE = null;
	private final boolean MULTI_TEXTURE;
	private final boolean FIREPROOF = false; // TODO
	private final int HEALTH;
	private final float ATTACK;
	private final List<ITextComponent> SPECIALS = new ArrayList<>();

	public GolemBookEntry(@Nonnull GolemContainer container) {
		// initialize fields based on golem attributes
		final EntityType<?> golemType = container.getEntityType();
		this.GOLEM_NAME = golemType.getTranslationKey();
		this.MULTI_TEXTURE = ExtraGolemsConfig.enableTextureInteract()
				&& (GolemMultiTextured.class.isAssignableFrom(container.getEntityClass()) 
					|| GolemMultiColorized.class.isAssignableFrom(container.getEntityClass()));
		//this.FIREPROOF = (container.isImmuneToFire() && !(golem instanceof EntityBedrockGolem));
		this.HEALTH = (int) container.getHealth();
		this.ATTACK = (float) container.getAttack();
		container.addDescription(SPECIALS);

		// set the block and block name if it exists
		this.BLOCKS = container.getBuildingBlocks().toArray(new Block[0]);
		
		// find the image to add to the book
		String img = (ExtraGolems.MODID + ":textures/gui/screenshots/").concat(golemType.getRegistryName().getPath()).concat(".png");
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
	 * @return true if building blocks were found for this golem
	 **/
	public boolean hasBlocks() {
		return this.BLOCKS != null && this.BLOCKS.length > 0;
	}
	
	/**
	 * @return the Block at [index % arrayLen]
	 * or Blocks.AIR if none is found.
	 **/
	public Block getBlock(final int index) {
		return hasBlocks() ? this.BLOCKS[index % this.BLOCKS.length] : Blocks.AIR;
	}

	/**
	 * @return the Blocks in this entry
	 **/
	public Block[] getBlocks() {
		return this.BLOCKS;
	}
	
	/**
	 * @return the Block in this entry
	 **/
	public String getBlockName(final Block b) {
		return trans(b.getTranslationKey());
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
		for (ITextComponent s : this.SPECIALS) {
			page.append("\n" + s.getFormattedText().replaceAll(TextFormatting.WHITE.toString(), TextFormatting.BLACK.toString()));
		}
		
		return page.toString();
	}
	
	/** Helper method for translating text into local language using {@code I18n} **/
	protected static String trans(final String s, final Object... strings) {
		return I18n.format(s, strings);
	}
}
