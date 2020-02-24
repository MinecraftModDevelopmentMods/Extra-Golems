package com.golems.integration;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.golems.entity.EntityBedrockGolem;
import com.golems.entity.EntityRedstoneLampGolem;
import com.golems.entity.GolemBase;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Base class to get in-game information about types of golems. Currently used
 * for Waila and The One Probe integration.
 *
 * @author sky01
 **/
public abstract class GolemDescriptionManager {

  protected boolean showSpecial;
  protected boolean showMultiTexture;
  protected boolean showAttack;
  protected boolean showFireproof;
  protected boolean showKnockbackResist;
  protected boolean showHealItems;

  public GolemDescriptionManager() {
    this.showAttack = true;
    this.showMultiTexture = true;
    this.showSpecial = true;
    this.showFireproof = true;
    this.showKnockbackResist = false;
    this.showHealItems = false;
  }

  /**
   * Checks the passed golem for various characteristics, making a String for each
   * one. Use this from a child class in order to populate your descriptions.
   * 
   * @return a LinkedList containing all descriptions that apply to the passed
   *         golem
   **/
  @SuppressWarnings("WeakerAccess")
  public List<String> getEntityDescription(final GolemBase golem) {
    List<String> list = new LinkedList<>();
    if (showAttack) {
      final float attackAmount = (float) golem.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getBaseValue();
      list.add(TextFormatting.GRAY + trans("entitytip.attack") + " : " + TextFormatting.WHITE + attackAmount);
    }

    // add right-click-texture to tip if possible
    if (this.showMultiTexture && golem.doesInteractChangeTexture() && !(golem instanceof EntityRedstoneLampGolem)) {
      list.add(TextFormatting.BLUE + trans("entitytip.click_change_texture"));
    }

    // add fire immunity to tip if possible
    if (this.showFireproof && golem.isImmuneToFire() && !(golem instanceof EntityBedrockGolem)) {
      list.add(TextFormatting.GOLD + trans("entitytip.is_fireproof"));
    }

    // add knockback resist to tip if possible
    if (this.showKnockbackResist
        && golem.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).getBaseValue() > 0.8999D) {
      final String sResist = TextFormatting.GRAY + trans("attribute.name.generic.knockbackResistance");
      list.add(sResist);
    }

    // add heal item list to tip if possible
    if (this.showHealItems) {
      list.add(TextFormatting.RED + trans("entitytip.heal_items"));
      // use a set to avoid duplicate entries
      final Set<String> set = new HashSet<>();
      final String prefix = TextFormatting.RED + "\u2764" + TextFormatting.GRAY + " ";
      // iterate through all healing items
      for (final ItemStack s : golem.getHealItems()) {
        // for wildcard items, iterate through subtypes and add each one's name
        if (s.getMetadata() == OreDictionary.WILDCARD_VALUE) {
          final Item i = s.getItem();
          final NonNullList<ItemStack> subItems = NonNullList.create();
          i.getSubItems(i.getCreativeTab(), subItems);
          for (final ItemStack s2 : subItems) {
            set.add(prefix + i.getItemStackDisplayName(s2));
          }
        } else {
          // for non-wildcard items, just add the itemstack name
          set.add(prefix + s.getDisplayName());
        }
      }
      // add all of the itemstack names to the master list
      list.addAll(set);
    }

    // add special information
    if (showSpecial) {
      golem.addSpecialDesc(list);
    }
    return list;
  }

  /**
   * Helper method for translation.
   **/
  protected static String trans(final String s, final Object... strings) {
    return I18n.format(s, strings);
  }
}
