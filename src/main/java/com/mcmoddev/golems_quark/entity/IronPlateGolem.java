package com.mcmoddev.golems_quark.entity;

import java.util.HashMap;
import java.util.Map;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.entity.base.GolemMultiTextured;
import com.mcmoddev.golems_quark.QuarkGolemsEntities;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

public final class IronPlateGolem extends GolemMultiTextured {

  public static final String[] PLATE_TYPES = { "normal", "rusty" };

  public IronPlateGolem(final EntityType<? extends GolemBase> entityType, final World world) {
    super(entityType, world, QuarkGolemsEntities.MODID, PLATE_TYPES);
  }

  @Override
  public void onBuilt(BlockState body, BlockState legs, BlockState arm1, BlockState arm2) {
    byte textureNum = "quark:iron_plate".equals(body.getBlock().getRegistryName().toString()) ? (byte) 0 : (byte) 1;
    textureNum %= this.getNumTextures();
    this.setTextureNum(textureNum);
  }

  @Override
  public ItemStack getCreativeReturn(final RayTraceResult target) {
    final Block plateNormal = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("quark", "iron_plate"));
    final Block plateRusty = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("quark", "rusty_iron_plate"));
    if(plateNormal != null && plateRusty != null) {
      return new ItemStack(this.getTextureNum() == 0 ? plateNormal : plateRusty);
    }
    return ItemStack.EMPTY;
  }

  @Override
  public Map<Block, Byte> getTextureBytes() {
    return new HashMap<>();
  }
}
