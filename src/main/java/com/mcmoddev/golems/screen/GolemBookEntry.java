package com.mcmoddev.golems.screen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.container.GolemContainer;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;


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
  
  private final IFormattableTextComponent name;
  private final IFormattableTextComponent page;
  
  private final List<ITextComponent> specials = new ArrayList<>();

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
    this.name = new TranslationTextComponent(nameString);
    this.page = makePage();
  }

  /** @return the unlocalized version of this entity's name **/
  public String getGolemNameRaw() { return this.nameString; }

  /** @return true if building blocks were found for this entity **/
  public boolean hasBlocks() { return this.buildingBlocks != null && this.buildingBlocks.length > 0; }

  /** @return the Block at [index % arrayLen] or Blocks.AIR if none is found  **/
  public Block getBlock(final int index) { return hasBlocks() ? this.buildingBlocks[index % this.buildingBlocks.length] : Blocks.AIR; }

  /** @return the Blocks in this entry **/
  public Block[] getBlocks() { return this.buildingBlocks; }

  /** @return the attack power of this entity **/
  public float getAttack() { return this.attack; }

  /** @return the number of special descriptions added by this entity **/
  public int getDescriptionSize() { return specials.size(); }

  /** @return the localized version of this entity's name **/
  public IFormattableTextComponent getGolemName() { return name; }
  
  /** @return all Golem Stats as one IFormattableTextComponent **/
  public IFormattableTextComponent getDescriptionPage() { return page; }

  /** @return Whether or not an image was found to add to the page **/
  public boolean hasImage() { return this.imageLoc != null; }

  /**
   * @return the ResourceLocation of an image to include, if it exists .
   * @see #hasImage()
   **/
  @Nullable
  public ResourceLocation getImageResource() { return this.imageLoc; }

  /** Concatenates the entity's stats and specials into a single TextComponent **/
  private IFormattableTextComponent makePage() {
    IFormattableTextComponent page = new StringTextComponent("");
    // ADD (ROUNDED) HEALTH TIP
    page.appendString("\n")
        .appendSibling(new  TranslationTextComponent("entitytip.health").appendString(": ").mergeStyle(TextFormatting.GRAY))
        .appendSibling(new StringTextComponent(String.valueOf(this.health)).mergeStyle(TextFormatting.BLACK))
        .appendSibling(new StringTextComponent(" \u2764").mergeStyle(TextFormatting.DARK_RED));
    // ADD ATTACK POWER TIP
    page.appendString("\n")
        .appendSibling(new  TranslationTextComponent("entitytip.attack").appendString(": ").mergeStyle(TextFormatting.GRAY))
        .appendSibling(new StringTextComponent(String.valueOf(this.attack)).mergeStyle(TextFormatting.BLACK))
        .appendString(" \u2694").appendString("\n");
    // ADD SPECIALS
    for (ITextComponent s : this.specials) {
      page.appendString("\n").appendSibling(s);
    }

    return page;
  }
}
