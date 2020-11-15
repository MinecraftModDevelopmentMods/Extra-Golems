package com.mcmoddev.golems.entity;

import java.util.Map;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.entity.base.GolemMultiTextured;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.GolemTextureBytes;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public final class ConcreteGolem extends GolemMultiTextured {

  public static final String ALLOW_RESIST = "Allow Special: Resistance";
  
  public static final String[] TEXTURE_NAMES = { "black_concrete", "orange_concrete", "magenta_concrete", "light_blue_concrete", 
      "yellow_concrete", "lime_concrete", "pink_concrete", "gray_concrete", "light_gray_concrete", "cyan_concrete", "purple_concrete", 
      "blue_concrete", "brown_concrete", "green_concrete", "red_concrete", "white_concrete" };
  
  public static final String[] LOOT_TABLE_NAMES = { "black", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "light_gray",
      "cyan", "purple", "blue", "brown", "green", "red", "white" };

  private boolean resist;
  
  public ConcreteGolem(final EntityType<? extends GolemBase> entityType, final World world) {
    super(entityType, world, "minecraft", TEXTURE_NAMES, ExtraGolems.MODID, LOOT_TABLE_NAMES);
    this.setPathPriority(PathNodeType.WATER, -0.8F);
    resist = getConfigBool(ALLOW_RESIST);
  }

  @Override
  protected void damageEntity(DamageSource source, float amount) {
    if (resist && !source.isDamageAbsolute()) {
      amount *= 0.6F;
      if (source.isFireDamage()) {
        // additional fire resistance
        amount *= 0.85F;
      }
    }
    super.damageEntity(source, amount);
  }

  @Override
  public Map<Block, Byte> getTextureBytes() {
    return GolemTextureBytes.CONCRETE;
  }

  @Override
  public ItemStack getCreativeReturn(final RayTraceResult target) {
    return new ItemStack(GolemTextureBytes.getByByte(GolemTextureBytes.CONCRETE, (byte) this.getTextureNum()));
  }
}
