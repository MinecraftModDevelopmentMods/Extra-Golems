package com.mcmoddev.golems.client.menu.guide_book;

import com.mcmoddev.golems.container.GolemContainer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class is used to easily connect golems and their blocks and other
 * info to use in the Golem Book.
 **/
public class GolemBookEntry {

	public static final int IMAGE_WIDTH = 100;
	public static final int IMAGE_HEIGHT = 50;

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
		ResourceLocation img = new ResourceLocation(modid, "textures/gui/info_book/".concat(name).concat(".png"));
		Optional<Resource> imageResource = Minecraft.getInstance().getResourceManager().getResource(img);
		if(imageResource.isPresent()) {
			this.imageLoc = img;
		} else {
			this.imageLoc = null;
		}
		// create the mutable text components
		this.name = Component.translatable(nameString);
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
		MutableComponent page = Component.literal("");
		// ADD (ROUNDED) HEALTH TIP
		page.append("\n")
				.append(Component.translatable("entitytip.health").append(": ").withStyle(ChatFormatting.GRAY))
				.append(Component.literal(String.valueOf(this.health)).withStyle(ChatFormatting.BLACK))
				.append(Component.literal(" \u2764").withStyle(ChatFormatting.DARK_RED));
		// ADD ATTACK POWER TIP
		page.append("\n")
				.append(Component.translatable("entitytip.attack").append(": ").withStyle(ChatFormatting.GRAY))
				.append(Component.literal(String.valueOf(this.attack)).withStyle(ChatFormatting.BLACK))
				.append(" \u2694").append("\n");
		// ADD SPECIALS
		for (Component s : this.specials) {
			page.append("\n").append(s);
		}

		return page;
	}
}
