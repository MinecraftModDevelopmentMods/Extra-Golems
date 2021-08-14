package com.mcmoddev.golems.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;

import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.GolemItems;
import com.mcmoddev.golems.blocks.BlockUtilityGlow;
import com.mcmoddev.golems.entity.ai.PlaceUtilityBlockGoal;
import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.entity.base.GolemMultiTextured;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.Level;

public final class RedstoneLampGolem extends GolemMultiTextured {

  public static final String ALLOW_SPECIAL = "Allow Special: Emit Light";

  public static final String[] TEXTURE_NAMES = { "redstone_lamp_on", "redstone_lamp" };
  public static final String[] LOOT_TABLE_NAMES = { "lit", "unlit" };

  public static final BiPredicate<GolemBase, BlockPos> LIT_PRED = (golem, pos) -> golem.isProvidingLight();

  public RedstoneLampGolem(final EntityType<? extends GolemBase> entityType, final Level world) {
    super(entityType, world, "minecraft", TEXTURE_NAMES, ExtraGolems.MODID, LOOT_TABLE_NAMES);
  }
  
  @Override
  protected void registerGoals() {
    super.registerGoals();
    if(this.getConfigBool(ALLOW_SPECIAL)) {
      final BlockState state = GolemItems.UTILITY_LIGHT.defaultBlockState().setValue(BlockUtilityGlow.LIGHT_LEVEL, 15);
      this.goalSelector.addGoal(9, new PlaceUtilityBlockGoal(this, state, BlockUtilityGlow.UPDATE_TICKS, 
          true, LIT_PRED));
    }
  }

  @Override
  public boolean canInteractChangeTexture() {
    // always allow interact
    return true;
  }

  @Override
  public boolean isProvidingLight() {
    // only allow light if correct texture data
    return this.getTextureNum() == 0;
  }

  @Override
  public float getBrightness() {
    return isProvidingLight() ? 1.0F : super.getBrightness();
  }

  @Override
  public ItemStack getCreativeReturn(final HitResult target) {
    return new ItemStack(Blocks.REDSTONE_LAMP);
  }

  @Override
  public Map<Block, Byte> getTextureBytes() {
    return new HashMap<>();
  }
}
