package com.mcmoddev.golems.integration;

import java.util.LinkedList;
import java.util.List;

import com.mcmoddev.golems.entity.DispenserGolem;
import com.mcmoddev.golems.entity.FurnaceGolem;
import com.mcmoddev.golems.entity.GolemBase;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;

/**
 * Base class to get in-game information about types of golems. Currently used
 * for Waila and The One Probe integration.
 *
 * @author skyjay1
 **/
public abstract class GolemDescriptionManager {

  protected boolean showSpecial = true;
  protected boolean showSpecialChild = false;
  protected boolean showAttack = true;

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
  public List<MutableComponent> getEntityDescription(final GolemBase golem) {    
    List<MutableComponent> list = new LinkedList<>();
    // add attack damage to tip enabled (usually checks if sneaking)
    if (showAttack) {
      double attack = (golem.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
      list.add(new TranslatableComponent("entitytip.attack").withStyle(ChatFormatting.GRAY).append(": ")
          .append(new TextComponent(Double.toString(attack)).withStyle(ChatFormatting.WHITE)));
    }
    // add special descriptions
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
    if ((!golem.isBaby() && showSpecial) || (golem.isBaby() && showSpecialChild)) {
      golem.getGolemContainer().addDescription(list);
    }
    return list;
  }
  
  protected void addFurnaceGolemInfo(final GolemBase g, final List<MutableComponent> list) {
    // add fuel amount if this is a furnace golem
    final int fuel = g.getFuel();
    final int percentFuel = (int) Math.ceil(g.getFuelPercentage() * 100F);
    final ChatFormatting color;
    if (percentFuel < 6) {
      color = ChatFormatting.RED;
    } else if (percentFuel < 16) {
      color = ChatFormatting.YELLOW;
    } else {
      color = ChatFormatting.WHITE;
    }
    // if sneaking, show exact value, otherwise show percentage value
    final String fuelString = isShiftDown() ? Integer.toString(fuel) : (Integer.toString(percentFuel) + "%");
    // actually add the description
    list.add(new TranslatableComponent("entitytip.fuel").withStyle(ChatFormatting.GRAY).append(": ")
        .append(new TextComponent(fuelString).withStyle(color)));
  }
  
  protected void addDispenserGolemInfo(final GolemBase g, final List<MutableComponent> list) {
    // add fuel amount if this is a furnace golem
    final int arrows = g.getArrowsInInventory();
    if(arrows > 0 && isShiftDown()) {
       final ChatFormatting color = ChatFormatting.WHITE;
      // if sneaking, show exact value, otherwise show percentage value
      final String arrowString = Integer.toString(arrows);
      // actually add the description
      list.add(new TranslatableComponent("entitytip.arrows").withStyle(ChatFormatting.GRAY).append(": ")
          .append(new TextComponent(arrowString).withStyle(color)));
    }
   
  }

  /**
   * @return whether the user is currently holding the SHIFT key
   **/
  protected static boolean isShiftDown() {
    return Screen.hasShiftDown();
  }

}
