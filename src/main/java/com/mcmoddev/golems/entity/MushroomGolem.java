package com.mcmoddev.golems.entity;

import java.util.HashMap;
import java.util.Map;

import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.entity.ai.PassiveEffectsGoal;
import com.mcmoddev.golems.entity.ai.PlaceBlocksGoal;
import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.entity.base.GolemMultiTextured;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.Level;

public final class MushroomGolem extends GolemMultiTextured {

  public static final String ALLOW_SPECIAL = "Allow Special: Plant Mushrooms";
  public static final String FREQUENCY = "Mushroom Frequency";
  public static final String ALLOW_HEALING = "Allow Special: Random Healing";

  public static final String[] TEXTURE_NAMES = { "red_mushroom_block", "brown_mushroom_block" };
  public static final String[] LOOT_TABLE_NAMES = { "red", "brown" };

  public MushroomGolem(final EntityType<? extends GolemBase> entityType, final Level world) {
    super(entityType, world, "minecraft", TEXTURE_NAMES, ExtraGolems.MODID, LOOT_TABLE_NAMES);
  }

  @Override
  protected void registerGoals() {
    super.registerGoals();
    final boolean allowed = this.getConfigBool(ALLOW_SPECIAL);
    int freq = allowed ? this.getConfigInt(FREQUENCY) : -100;
    freq += this.random.nextInt(Math.max(10, freq / 2));
    final BlockState[] mushrooms = { Blocks.BROWN_MUSHROOM.defaultBlockState(), Blocks.RED_MUSHROOM.defaultBlockState() };
    final Block[] soils = { Blocks.DIRT, Blocks.GRASS_BLOCK, Blocks.MYCELIUM, Blocks.PODZOL, Blocks.NETHERRACK, Blocks.SOUL_SAND };
    this.goalSelector.addGoal(2, new PlaceBlocksGoal(this, freq, mushrooms, soils, allowed));
    if (this.getConfigBool(ALLOW_HEALING)) {
      this.goalSelector.addGoal(4, new PassiveEffectsGoal(this, MobEffects.REGENERATION, 50, 60, 1, 1,
          g -> !g.getCommandSenderWorld().isDay() && g.getCommandSenderWorld().getRandom().nextInt(450) == 0));
    }
  }

  @Override
  public void onBuilt(BlockState body, BlockState legs, BlockState arm1, BlockState arm2) {
    // use block to give this golem the right texture (defaults to brown mushroom)
    byte textureNum = body.getBlock() == Blocks.RED_MUSHROOM_BLOCK ? (byte) 0 : (byte) 1;
    textureNum %= this.getNumTextures();
    this.setTextureNum(textureNum);
  }

  @Override
  public ItemStack getCreativeReturn(final HitResult target) {
    return this.getTextureNum() == 0 ? new ItemStack(Blocks.RED_MUSHROOM_BLOCK) : new ItemStack(Blocks.BROWN_MUSHROOM_BLOCK);
  }

  @Override
  public Map<Block, Byte> getTextureBytes() {
    return new HashMap<>();
  }
}
