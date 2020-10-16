package com.mcmoddev.golems.entity;

import java.util.HashMap;
import java.util.Map;

import com.mcmoddev.golems.entity.ai.PassiveEffectsGoal;
import com.mcmoddev.golems.entity.ai.PlaceBlocksGoal;
import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.entity.base.GolemMultiTextured;
import com.mcmoddev.golems.main.ExtraGolems;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public final class MushroomGolem extends GolemMultiTextured {

  public static final String ALLOW_SPECIAL = "Allow Special: Plant Mushrooms";
  public static final String FREQUENCY = "Mushroom Frequency";
  public static final String ALLOW_HEALING = "Allow Special: Random Healing";

  public static final String[] SHROOM_TYPES = { "red", "brown" };

  public MushroomGolem(final EntityType<? extends GolemBase> entityType, final World world) {
    super(entityType, world, ExtraGolems.MODID, SHROOM_TYPES, SHROOM_TYPES);
    for (int n = 0, len = SHROOM_TYPES.length; n < len; n++) {
      // initialize textures
      this.textures[n] = new ResourceLocation(ExtraGolems.MODID, this.getGolemContainer().getName() + "/" + SHROOM_TYPES[n] + ".png");
    }
  }

  @Override
  protected void registerGoals() {
    super.registerGoals();
    final boolean allowed = this.getConfigBool(ALLOW_SPECIAL);
    int freq = allowed ? this.getConfigInt(FREQUENCY) : -100;
    freq += this.rand.nextInt(Math.max(10, freq / 2));
    final BlockState[] mushrooms = { Blocks.BROWN_MUSHROOM.getDefaultState(), Blocks.RED_MUSHROOM.getDefaultState() };
    final Block[] soils = { Blocks.DIRT, Blocks.GRASS_BLOCK, Blocks.MYCELIUM, Blocks.PODZOL, Blocks.NETHERRACK, Blocks.SOUL_SAND };
    this.goalSelector.addGoal(2, new PlaceBlocksGoal(this, freq, mushrooms, soils, allowed));
    if (this.getConfigBool(ALLOW_HEALING)) {
      this.goalSelector.addGoal(4, new PassiveEffectsGoal(this, Effects.REGENERATION, 50, 60, 1, 1,
          g -> !g.getEntityWorld().isDaytime() && g.getEntityWorld().getRandom().nextInt(450) == 0));
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
  public ItemStack getCreativeReturn(final RayTraceResult target) {
    return this.getTextureNum() == 0 ? new ItemStack(Blocks.RED_MUSHROOM_BLOCK) : new ItemStack(Blocks.BROWN_MUSHROOM_BLOCK);
  }

  @Override
  public Map<Block, Byte> getTextureBytes() {
    return new HashMap<>();
  }
}
