package com.mcmoddev.golems.integration;

import java.util.LinkedList;
import java.util.List;

import com.mcmoddev.golems.golem_stats.behavior.GolemBehaviors;
import com.mcmoddev.golems.entity.GolemBase;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * Base class to get in-game information about types of golems. Intended to use
 * for Waila and The One Probe integration.
 *
 * @author skyjay1
 **/
public abstract class GolemDescriptionManager {

  protected boolean showSpecial = true;
  protected boolean showSpecialChild = false;
  protected boolean showAttack = true;
  protected boolean extended = false;

  public GolemDescriptionManager() {
    // empty constructor
  }

  /**
   * Collects descriptions that apply to the given golem
   * @param golem the golem entity
   * @return a List containing all descriptions that apply to the passed entity
   **/
  @SuppressWarnings("WeakerAccess")
  public List<ITextComponent> getEntityDescription(final GolemBase golem) {    
    List<ITextComponent> list = new LinkedList<>();
    // add attack damage to tip enabled (usually checks if sneaking)
    if (showAttack) {
      double attack = (golem.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
      list.add(new TranslationTextComponent("entitytip.attack").mergeStyle(TextFormatting.GRAY).appendString(": ")
          .appendSibling(new StringTextComponent(Double.toString(attack)).mergeStyle(TextFormatting.WHITE)));
    }
    // add special descriptions
    if(showSpecial) {
      // add fuel amount if this golem consumes fuel
      if (golem.getContainer().hasBehavior(GolemBehaviors.USE_FUEL)) {
        addFuelInfo(golem, list);
      }
      // add arrow amount if this golem shoots arrows
      if(golem.getContainer().hasBehavior(GolemBehaviors.SHOOT_ARROWS)) {
        addArrowsInfo(golem, list);
      }
    }

    // add all other descriptions information
    if ((!golem.isChild() && showSpecial) || (golem.isChild() && showSpecialChild)) {
      list.addAll(golem.getContainer().getDescriptions());
    }
    return list;
  }
  
  /**
   * Adds information about the amount of fuel in the golem's inventory
   * @param g the golem
   * @param list the description list
   */
  protected void addFuelInfo(final GolemBase g, final List<ITextComponent> list) {
    // determine fuel percentage
    final float percentFuel = g.getFuelPercentage() * 100.0F;
    final TextFormatting color;
    if (percentFuel < 6) {
      color = TextFormatting.RED;
    } else if (percentFuel < 16) {
      color = TextFormatting.YELLOW;
    } else {
      color = TextFormatting.WHITE;
    }
    // if sneaking, show exact value, otherwise show percentage value
    final String fuelString;
    if(extended) {
      fuelString = String.format("%d / %d", g.getFuel(), g.getMaxFuel());
    } else {
      fuelString = String.format("%.1f", percentFuel) + "%";
    }
    // actually add the description
    list.add(new TranslationTextComponent("entitytip.fuel").mergeStyle(TextFormatting.GRAY).appendString(": ")
        .appendSibling(new StringTextComponent(fuelString).mergeStyle(color)));
  }
  
  /**
   * Adds information about the number of arrows in the golem's inventory
   * @param g the golem
   * @param list the description list
   */
  protected void addArrowsInfo(final GolemBase g, final List<ITextComponent> list) {
    // determine number of arrows available
    final int arrows = g.getArrowsInInventory();
    if(arrows > 0 && extended) {
       final TextFormatting color = TextFormatting.WHITE;
      // if sneaking, show exact value, otherwise show percentage value
      final String arrowString = String.valueOf(arrows);
      // actually add the description
      list.add(new TranslationTextComponent("entitytip.arrows").mergeStyle(TextFormatting.GRAY).appendString(": ")
          .appendSibling(new StringTextComponent(arrowString).mergeStyle(color)));
    }
   
  }

  /** @return whether TRUE if the user is currently holding the SHIFT key **/
  protected static boolean isShiftDown() {
    return Screen.hasShiftDown();
  }

}
