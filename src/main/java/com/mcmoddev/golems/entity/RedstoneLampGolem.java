package com.mcmoddev.golems.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;

import com.mcmoddev.golems.blocks.BlockUtilityGlow;
import com.mcmoddev.golems.entity.ai.PlaceUtilityBlockGoal;
import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.entity.base.GolemMultiTextured;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.main.GolemItems;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public final class RedstoneLampGolem extends GolemMultiTextured {

  public static final String ALLOW_SPECIAL = "Allow Special: Emit Light";

  public static final String[] VARIANTS = { "lit", "unlit" };

  public static final BiPredicate<GolemBase, BlockState> LIT_PRED = (golem, toReplace) -> golem.isProvidingLight();

  public RedstoneLampGolem(final EntityType<? extends GolemBase> entityType, final World world) {
    super(entityType, world, ExtraGolems.MODID, VARIANTS);
    final BlockState state = GolemItems.UTILITY_LIGHT.getDefaultState().with(BlockUtilityGlow.LIGHT_LEVEL, 15);
    this.goalSelector.addGoal(9, new PlaceUtilityBlockGoal(this, state, BlockUtilityGlow.UPDATE_TICKS, this.getConfigBool(ALLOW_SPECIAL),
        PlaceUtilityBlockGoal.getDefaultBiPred(state).and(LIT_PRED)));
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
  public ItemStack getCreativeReturn(final RayTraceResult target) {
    return new ItemStack(Blocks.REDSTONE_LAMP);
  }

  @Override
  public Map<Block, Byte> getTextureBytes() {
    return new HashMap<>();
  }
}
