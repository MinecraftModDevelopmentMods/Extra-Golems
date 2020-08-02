package com.mcmoddev.golems.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mcmoddev.golems.entity.base.GolemMultiColorized;
import com.mcmoddev.golems.entity.base.GolemMultiTextured;
import com.mcmoddev.golems.util.config.ExtraGolemsConfig;
import com.mcmoddev.golems.util.config.GolemContainer;
import com.mcmoddev.golems.util.config.GolemContainer.SwimMode;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
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
  private final boolean canInteractChangeTexture;
  private final boolean canSwim;
  private final boolean isFireproof;
  private final int health;
  private final float attack;
  private final List<ITextComponent> specials = new ArrayList<>();

  public GolemBookEntry(@Nonnull GolemContainer container) {
    // initialize fields based on golem attributes
    final EntityType<?> golemType = container.getEntityType();
    this.golemName = golemType.getTranslationKey();
    this.canInteractChangeTexture = ExtraGolemsConfig.enableTextureInteract() && (GolemMultiTextured.class.isAssignableFrom(container.getEntityClass())
        || GolemMultiColorized.class.isAssignableFrom(container.getEntityClass()));
    this.canSwim = container.getSwimMode() == SwimMode.SWIM;
    this.isFireproof = golemType.isImmuneToFire();
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
      // System.out.println("Image found, yay! Loading " + img.toString() + " for " + this.GOLEM_NAME);
    } catch (IOException e) {
      // System.out.println("No image found, skipping " + img.toString() + " for " + this.GOLEM_NAME);
    }
  }

  /**
   * @return the localized version of this golem's name
   **/
  public IFormattableTextComponent getGolemName() {
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
   * @return all Golem Stats as one String
   **/
  public IFormattableTextComponent getDescriptionPage() {
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
   * Concatenates the golem's stats and specials into a single STring
   **/
  private IFormattableTextComponent makePage() {
    // 1.16 mappings: 
    // applyTextStyle -> func_240699_a_(TextFormatting) or func_240701_a_(TextFormatting...)
    // appendSibling -> func_230529_a_(ITextComponent)
    // appendText -> func_240702_b_(String)
    StringTextComponent page = new StringTextComponent("");
    // ADD (ROUNDED) HEALTH TIP
    page.func_240702_b_("\n")
        .func_230529_a_(trans("entitytip.health").func_240702_b_(": ").func_240699_a_(TextFormatting.GRAY))
        .func_230529_a_(wrap(String.valueOf(this.health)).func_240699_a_(TextFormatting.BLACK)) 
        .func_230529_a_(wrap(" \u2764").func_240699_a_(TextFormatting.DARK_RED));
    // ADD ATTACK POWER TIP
    page.func_240702_b_("\n")
        .func_230529_a_(trans("entitytip.attack").func_240702_b_(": ").func_240699_a_(TextFormatting.GRAY))
        .func_230529_a_(wrap(String.valueOf(this.attack)).func_240699_a_(TextFormatting.BLACK)) 
        .func_240702_b_(" \u2764").func_240702_b_("\n");
    // ADD FIREPROOF TIP
    if (this.isFireproof) {
      page.func_240702_b_("\n")
          .func_230529_a_(trans("entitytip.is_fireproof").func_240699_a_(TextFormatting.GOLD));
    }
    // ADD INTERACT-TEXTURE TIP
    if (this.canInteractChangeTexture) {
      page.func_240702_b_("\n")
          .func_230529_a_(trans("entitytip.click_change_texture").func_240699_a_(TextFormatting.BLUE));
    }
    // ADD SWIMMING TIP
    if(this.canSwim) {
      page.func_240702_b_("\n")
          .func_230529_a_(trans("entitytip.advanced_swim").func_240699_a_(TextFormatting.AQUA));
    }
    // ADD SPECIALS
    for (ITextComponent s : this.specials) {
      page.func_240702_b_("\n")
          .func_230529_a_(wrap(s.getString().replaceAll(TextFormatting.WHITE.toString(), TextFormatting.BLACK.toString())));
    }

    return page;
  }

  /**
   * Helper method for translating text into local language
   **/
  protected static IFormattableTextComponent trans(final String s, final Object... strings) {
    return new TranslationTextComponent(s, strings);
  }
  
  protected static IFormattableTextComponent wrap(final String s) {
    return new StringTextComponent(s);
  }
}
