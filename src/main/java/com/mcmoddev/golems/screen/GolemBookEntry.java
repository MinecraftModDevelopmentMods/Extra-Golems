package com.mcmoddev.golems.screen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mcmoddev.golems.container.GolemContainer;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EntityType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;

/**
 * This class will be used to easily connect golems and their blocks and other
 * info to use in the Golem Book.
 **/
public class GolemBookEntry {

  private final Block[] buildingBlocks;
  private final String golemName;
  private ResourceLocation imageLoc = null;
  private final int health;
  private final float attack;
  private final List<MutableComponent> specials = new ArrayList<>();

  public GolemBookEntry(@Nonnull GolemContainer container) {
    // initialize fields based on entity attributes
    this.golemName = container.getMaterial().toString();
    this.health = (int) container.getAttributes().getHealth();
    this.attack = (float) container.getAttributes().getAttack();
    // TODO custom descriptions
    //container.addDescription(specials);

    // set the block and block name if it exists
    this.buildingBlocks = container.getAllBlocks().toArray(new Block[0]);

    // find the image to add to the book
    final String modid = container.getMaterial().getNamespace();
    final String name = container.getMaterial().getPath();
    String img = (modid + ":textures/gui/screenshots/").concat(name).concat(".png");
    try {
      this.imageLoc = Minecraft.getInstance().getResourceManager().getResource(new ResourceLocation(img)).getLocation();
      // System.out.println("Image found, yay! Loading " + img.toString() + " for " + this.GOLEM_NAME);
    } catch (IOException e) {
      // System.out.println("No image found, skipping " + img.toString() + " for " + this.GOLEM_NAME);
    }
  }

  /**
   * @return the localized version of this entity's name
   **/
  public MutableComponent getGolemName() {
    return trans(this.golemName);
  }

  /**
   * @return the unlocalized version of this entity's name
   **/
  public String getGolemNameRaw() {
    return this.golemName;
  }

  /**
   * @return true if building blocks were found for this entity
   **/
  public boolean hasBlocks() {
    return this.buildingBlocks != null && this.buildingBlocks.length > 0;
  }

  /**
   * @return the Block at [index % arrayLen] or Blocks.AIR if none is found.
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
   * @return the Block in this entry
   **/
  public String getBlockName(final Block b) {
    return trans(b.getDescriptionId()).getString();
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
   * @return all Golem Stats as one StringTextComponent
   **/
  public MutableComponent getDescriptionPage() {
    // re-make each time for real-time localization
    return makePage();
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
   * Concatenates the entity's stats and specials into a single StringTextComponent
   **/
  private MutableComponent makePage() {
    TextComponent page = new TextComponent("");
    // ADD (ROUNDED) HEALTH TIP
    page.append("\n")
        .append(trans("entitytip.health").append(": ").withStyle(ChatFormatting.GRAY))
        .append(wrap(String.valueOf(this.health)).withStyle(ChatFormatting.BLACK)) 
        .append(wrap(" \u2764").withStyle(ChatFormatting.DARK_RED));
    // ADD ATTACK POWER TIP
    page.append("\n")
        .append(trans("entitytip.attack").append(": ").withStyle(ChatFormatting.GRAY))
        .append(wrap(String.valueOf(this.attack)).withStyle(ChatFormatting.BLACK)) 
        .append(" \u2694").append("\n");
    // ADD SPECIALS
    for (Component s : this.specials) {
      page.append("\n").append(s);
    }

    return page;
  }

  /**
   * Helper method for translating text into local language
   **/
  protected static MutableComponent trans(final String s, final Object... strings) {
    return new TranslatableComponent(s, strings);
  }
  
  protected static MutableComponent wrap(final String s) {
    return new TextComponent(s);
  }
}
