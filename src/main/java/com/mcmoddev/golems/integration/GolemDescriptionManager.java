package com.mcmoddev.golems.integration;

import java.util.LinkedList;
import java.util.List;

import com.mcmoddev.golems.container.behavior.GolemBehaviors;
import com.mcmoddev.golems.entity.GolemBase;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.ai.attributes.Attributes;

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
  public List<Component> getEntityDescription(final GolemBase golem) {    
    List<Component> list = new LinkedList<>();
    // add attack damage to tip enabled (usually checks if sneaking)
    if (showAttack) {
      double attack = (golem.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
      list.add(new TranslatableComponent("entitytip.attack").withStyle(ChatFormatting.GRAY).append(": ")
          .append(new TextComponent(Double.toString(attack)).withStyle(ChatFormatting.WHITE)));
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
    if ((!golem.isBaby() && showSpecial) || (golem.isBaby() && showSpecialChild)) {
      list.addAll(golem.getContainer().getDescriptions());
    }
    return list;
  }
  
  /**
   * Adds information about the amount of fuel in the golem's inventory
   * @param g the golem
   * @param list the description list
   */
  protected void addFuelInfo(final GolemBase g, final List<Component> list) {
    // determine fuel percentage
    final float percentFuel = g.getFuelPercentage() * 100.0F;
    final ChatFormatting color;
    if (percentFuel < 6) {
      color = ChatFormatting.RED;
    } else if (percentFuel < 16) {
      color = ChatFormatting.YELLOW;
    } else {
      color = ChatFormatting.WHITE;
    }
    // if sneaking, show exact value, otherwise show percentage value
    final String fuelString;
    if(extended) {
      fuelString = String.format("%d / %d", g.getFuel(), g.getMaxFuel());
    } else {
      fuelString = String.format("%.1f", percentFuel) + "%";
    }
    // actually add the description
    list.add(new TranslatableComponent("entitytip.fuel").withStyle(ChatFormatting.GRAY).append(": ")
        .append(new TextComponent(fuelString).withStyle(color)));
  }
  
  /**
   * Adds information about the number of arrows in the golem's inventory
   * @param g the golem
   * @param list the description list
   */
  protected void addArrowsInfo(final GolemBase g, final List<Component> list) {
    // determine number of arrows available
    final int arrows = g.getArrowsInInventory();
    if(arrows > 0 && extended) {
       final ChatFormatting color = ChatFormatting.WHITE;
      // if sneaking, show exact value, otherwise show percentage value
      final String arrowString = String.valueOf(arrows);
      // actually add the description
      list.add(new TranslatableComponent("entitytip.arrows").withStyle(ChatFormatting.GRAY).append(": ")
          .append(new TextComponent(arrowString).withStyle(color)));
    }
   
  }

  /** @return whether TRUE if the user is currently holding the SHIFT key **/
  protected static boolean isShiftDown() {
    return Screen.hasShiftDown();
  }

}
