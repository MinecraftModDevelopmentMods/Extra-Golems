package com.mcmoddev.golems.entity;

import java.util.Map;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.entity.base.GolemMultiColorized;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.GolemTextureBytes;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public final class ConcreteGolem extends GolemMultiColorized {

  public static final String ALLOW_RESIST = "Allow Special: Resistance";
  
  public ConcreteGolem(final EntityType<? extends GolemBase> entityType, final World world) {
    super(entityType, world, ExtraGolems.MODID, DYE_COLORS);
    this.setPathPriority(PathNodeType.WATER, -0.8F);
  }

  @Override
  protected void damageEntity(DamageSource source, float amount) {
    if (this.getConfigBool(ALLOW_RESIST) && !source.isDamageAbsolute()) {
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
