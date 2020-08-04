package com.mcmoddev.golems.integration;

import java.util.LinkedList;
import java.util.List;

import com.mcmoddev.golems.entity.BedrockGolem;
import com.mcmoddev.golems.entity.DispenserGolem;
import com.mcmoddev.golems.entity.FurnaceGolem;
import com.mcmoddev.golems.entity.RedstoneLampGolem;
import com.mcmoddev.golems.entity.base.GolemBase;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * Base class to get in-game information about types of golems. Currently used
 * for Waila and The One Probe integration.
 *
 * @author skyjay1
 **/
public abstract class GolemDescriptionManager {

  protected boolean showSpecial = true;
  protected boolean showSpecialChild = false;
  protected boolean showMultiTexture = true;
  protected boolean showAttack = true;
  protected boolean showFireproof = true;
  protected boolean showKnockbackResist = false;

  public GolemDescriptionManager() {
    // empty constructor
  }

  /**
   * Checks the passed golem for various characteristics, making a String for each
   * one. Use this from a child class in order to populate your descriptions.
   *
   * @return a LinkedList containing all descriptions that apply to the passed
   *         golem
   **/
  @SuppressWarnings("WeakerAccess")
  public List<ITextComponent> getEntityDescription(final GolemBase golem) {    
    List<ITextComponent> list = new LinkedList<>();
    // add attack damage to tip enabled (usually checks if sneaking)
    if (showAttack) {
      double attack = (golem.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
      list.add(new TranslationTextComponent("entitytip.attack").mergeStyle(TextFormatting.GRAY).append(new StringTextComponent(": "))
          .append(new StringTextComponent(Double.toString(attack)).mergeStyle(TextFormatting.WHITE)));
    }

    // add right-click-texture to tip if possible
    if (this.showMultiTexture && golem.canInteractChangeTexture() && !(golem instanceof RedstoneLampGolem)) {
      list.add(new TranslationTextComponent("entitytip.click_change_texture").mergeStyle(TextFormatting.BLUE));
    }

    // add fire immunity to tip if possible
    if (this.showFireproof && golem.getType().isImmuneToFire() && !(golem instanceof BedrockGolem)) {
      list.add(new TranslationTextComponent("entitytip.is_fireproof").mergeStyle(TextFormatting.GOLD));
    }

    // add knockback resist to tip if possible
    if (this.showKnockbackResist && golem.getAttribute(Attributes.KNOCKBACK_RESISTANCE).getBaseValue() > 0.8999D) {
      list.add(new TranslationTextComponent("attribute.name.generic.knockbackResistance").mergeStyle(TextFormatting.GRAY));
    }

    if(showSpecial) {
      // add fuel amount if this is a furnace golem
      if (golem instanceof FurnaceGolem) {
        addFurnaceGolemInfo((FurnaceGolem)golem, list);
      }
      // add arrow amount if this is a dispenser golem
      if(golem instanceof DispenserGolem) {
        addDispenserGolemInfo((DispenserGolem)golem, list);
      }
    }

    // add special information
    if ((!golem.isChild() && showSpecial) || (golem.isChild() && showSpecialChild)) {
      golem.getGolemContainer().addDescription(list);
    }
    return list;
  }
  
  protected void addFurnaceGolemInfo(final FurnaceGolem g, final List<ITextComponent> list) {
    // add fuel amount if this is a furnace golem
    final int fuel = g.getFuel();
    final int percentFuel = (int) Math.ceil(g.getFuelPercentage() * 100F);
    final TextFormatting color;
    if (percentFuel < 6) {
      color = TextFormatting.RED;
    } else if (percentFuel < 16) {
      color = TextFormatting.YELLOW;
    } else {
      color = TextFormatting.WHITE;
    }
    // if sneaking, show exact value, otherwise show percentage value
    final String fuelString = isShiftDown() ? Integer.toString(fuel) : (Integer.toString(percentFuel) + "%");
    // actually add the description
    list.add(new TranslationTextComponent("entitytip.fuel").mergeStyle(TextFormatting.GRAY).append(new StringTextComponent(": "))
        .append(new StringTextComponent(fuelString).mergeStyle(color)));
  }
  
  protected void addDispenserGolemInfo(final DispenserGolem g, final List<ITextComponent> list) {
    // add fuel amount if this is a furnace golem
    final int arrows = g.getArrowsInInventory();
    if(arrows > 0 && isShiftDown()) {
       final TextFormatting color = TextFormatting.WHITE;
      // if sneaking, show exact value, otherwise show percentage value
      final String arrowString = Integer.toString(arrows);
      // actually add the description
      list.add(new TranslationTextComponent("entitytip.arrows").mergeStyle(TextFormatting.GRAY).append(new StringTextComponent(": "))
          .append(new StringTextComponent(arrowString).mergeStyle(color)));
    }
   
  }

  /**
   * @return whether the user is currently holding the SHIFT key
   **/
  protected static boolean isShiftDown() {
    return Screen.hasShiftDown();
  }

}
