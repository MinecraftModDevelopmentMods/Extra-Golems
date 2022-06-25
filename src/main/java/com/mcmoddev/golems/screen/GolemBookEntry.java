package com.mcmoddev.golems.screen;

import com.mcmoddev.golems.container.GolemContainer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to easily connect golems and their blocks and other
 * info to use in the Golem Book.
 **/
public class GolemBookEntry {

	private final Block[] buildingBlocks;
	private final String nameString;
	private ResourceLocation imageLoc = null;
	private final int health;
	private final float attack;

	private final MutableComponent name;
	private final MutableComponent page;

	private final List<Component> specials = new ArrayList<>();

	public GolemBookEntry(@Nonnull ResourceLocation golemName, @Nonnull GolemContainer container) {
		// initialize fields based on entity attributes
		this.nameString = "entity." + golemName.getNamespace() + ".golem." + golemName.getPath();
		this.health = (int) container.getAttributes().getHealth();
		this.attack = (float) container.getAttributes().getAttack();

		// add descriptions from container
		specials.addAll(container.getDescriptions());

		// set the block and block name if it exists
		this.buildingBlocks = container.getAllBlocks().toArray(new Block[0]);

		// find the image to add to the book
		final String modid = golemName.getNamespace();
		final String name = golemName.getPath();
		String img = (modid + ":textures/gui/info_book/").concat(name).concat(".png");
		try {
			this.imageLoc = Minecraft.getInstance().getResourceManager().getResource(new ResourceLocation(img)).getLocation();
			// System.out.println("Image found, yay! Loading " + img.toString() + " for " + this.GOLEM_NAME);
		} catch (IOException e) {
			// System.out.println("No image found, skipping " + img.toString() + " for " + this.GOLEM_NAME);
		}

		// create the mutable text components
		this.name = new TranslatableComponent(nameString);
		this.page = makePage();
	}

	/**
	 * @return the unlocalized version of this entity's name
	 **/
	public String getGolemNameRaw() {
		return this.nameString;
	}

	/**
	 * @return true if building blocks were found for this entity
	 **/
	public boolean hasBlocks() {
		return this.buildingBlocks != null && this.buildingBlocks.length > 0;
	}

	/**
	 * @return the Block at [index % arrayLen] or Blocks.AIR if none is found
	 **/
	public Block getBlock(final int index) {
		return hasBlocks() ? this.buildingBlocks[index % this.buildingBlocks.length] : Blocks.AIR;
	}

	/**
	 * @return the Blocks in this entry
	 **/
	public Block[] getBlocks() {
		return this.buildingBlocks;
	}

	/**
	 * @return the attack power of this entity
	 **/
	public float getAttack() {
		return this.attack;
	}

	/**
	 * @return the number of special descriptions added by this entity
	 **/
	public int getDescriptionSize() {
		return specials.size();
	}

	/**
	 * @return the localized version of this entity's name
	 **/
	public MutableComponent getGolemName() {
		return name;
	}

	/**
	 * @return all Golem Stats as one MutableComponent
	 **/
	public MutableComponent getDescriptionPage() {
		return page;
	}

	/**
	 * @return Whether or not an image was found to add to the page
	 **/
	public boolean hasImage() {
		return this.imageLoc != null;
	}

	/**
	 * @return the ResourceLocation of an image to include, if it exists .
	 * @see #hasImage()
	 **/
	@Nullable
	public ResourceLocation getImageResource() {
		return this.imageLoc;
	}

	/**
	 * Concatenates the entity's stats and specials into a single TextComponent
	 **/
	private MutableComponent makePage() {
		TextComponent page = new TextComponent("");
		// ADD (ROUNDED) HEALTH TIP
		page.append("\n")
				.append(new TranslatableComponent("entitytip.health").append(": ").withStyle(ChatFormatting.GRAY))
				.append(new TextComponent(String.valueOf(this.health)).withStyle(ChatFormatting.BLACK))
				.append(new TextComponent(" \u2764").withStyle(ChatFormatting.DARK_RED));
		// ADD ATTACK POWER TIP
		page.append("\n")
				.append(new TranslatableComponent("entitytip.attack").append(": ").withStyle(ChatFormatting.GRAY))
				.append(new TextComponent(String.valueOf(this.attack)).withStyle(ChatFormatting.BLACK))
				.append(" \u2694").append("\n");
		// ADD SPECIALS
		for (Component s : this.specials) {
			page.append("\n").append(s);
		}

		return page;
	}
}
