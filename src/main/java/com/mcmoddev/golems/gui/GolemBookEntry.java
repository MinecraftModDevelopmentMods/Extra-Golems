package com.mcmoddev.golems.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mcmoddev.golems.util.GolemContainer;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

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
  private final List<ITextComponent> specials = new ArrayList<>();

  public GolemBookEntry(@Nonnull GolemContainer container) {
    // initialize fields based on golem attributes
    final EntityType<?> golemType = container.getEntityType();
    this.golemName = golemType.getTranslationKey();
    this.health = (int) container.getHealth();
    this.attack = (float) container.getAttack();
    container.addDescription(specials);

    // set the block and block name if it exists
    this.buildingBlocks = container.getBuildingBlocks().toArray(new Block[0]);

    // find the image to add to the book
    final String modid = container.getRegistryName().getNamespace();
    final String name = container.getRegistryName().getPath();
    String img = (modid + ":textures/gui/screenshots/").concat(name).concat(".png");
    try {
      this.imageLoc = Minecraft.getInstance().getResourceManager().getResource(new ResourceLocation(img)).getLocation();
    } catch (IOException e) { }
  }

  /**
   * @return the localized version of this golem's name
   **/
  public ITextComponent getGolemName() {
    return trans(this.golemName);
  }

  /**
   * @return the unlocalized version of this golem's name
   **/
  public String getGolemNameRaw() {
    return this.golemName;
  }

  /**
   * @return true if building blocks were found for this golem
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
    return trans(b.getTranslationKey()).getString();
  }

  /**
   * @return the attack power of this golem
   **/
  public float getAttack() {
    return this.attack;
  }

  /**
   * @return the number of special descriptions added by this golem
   **/
  public int getDescriptionSize() {
    return specials.size();
  }

  /**
   * @return all Golem Stats as one StringTextComponent
   **/
  public ITextComponent getDescriptionPage() {
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
   * Concatenates the golem's stats and specials into a single StringTextComponent
   **/
  private ITextComponent makePage() {
    StringTextComponent page = new StringTextComponent("");
    // ADD (ROUNDED) HEALTH TIP
    page.appendText("\n")
        .appendSibling(trans("entitytip.health").appendText(": ").applyTextStyle(TextFormatting.GRAY))
        .appendSibling(wrap(String.valueOf(this.health)).applyTextStyle(TextFormatting.BLACK)) 
        .appendSibling(wrap(" \u2764").applyTextStyle(TextFormatting.DARK_RED));
    // ADD ATTACK POWER TIP
    page.appendText("\n")
        .appendSibling(trans("entitytip.attack").appendText(": ").applyTextStyle(TextFormatting.GRAY))
        .appendSibling(wrap(String.valueOf(this.attack)).applyTextStyle(TextFormatting.BLACK)) 
        .appendText(" \u2694").appendText("\n");
    // ADD SPECIALS
    for (ITextComponent s : this.specials) {
      page.appendText("\n").appendSibling(s);
    }

    return page;
  }

  /**
   * Helper method for translating text into local language
   **/
  protected static ITextComponent trans(final String s, final Object... strings) {
    return new TranslationTextComponent(s, strings);
  }
  
  protected static ITextComponent wrap(final String s) {
    return new StringTextComponent(s);
  }
}
