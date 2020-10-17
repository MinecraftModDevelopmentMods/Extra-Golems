package com.mcmoddev.golems_quark.entity;

import java.util.HashMap;
import java.util.Map;

import com.mcmoddev.golems.blocks.BlockUtilityGlow;
import com.mcmoddev.golems.entity.ai.PlaceUtilityBlockGoal;
import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.entity.base.GolemMultiTextured;
import com.mcmoddev.golems.main.GolemItems;
import com.mcmoddev.golems.util.GolemRenderSettings;
import com.mcmoddev.golems.util.GolemTextureBytes;
import com.mcmoddev.golems_quark.QuarkGolemsEntities;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.world.module.underground.CaveCrystalUndergroundBiomeModule;

public final class CaveCrystalGolem extends GolemMultiTextured {
  
  public static final String ALLOW_SPECIAL = "Allow Special: Emit Light";

  private static final String[] TEXTURE_NAMES = { "red_crystal", "orange_crystal", "yellow_crystal", "green_crystal", "blue_crystal", "indigo_crystal", "violet_crystal", "white_crystal", "black_crystal" };
  private static final String[] LOOT_TABLE_NAMES = { "red", "orange", "yellow", "green", "blue", "indigo", "violet", "white", "black" };

  private static final int[] CRYSTAL_COLORS = {
      0xff0000, // red
      0xff8000, // orange
      0xffff00, // yellow
      0x00ff00, // green
      0x00ffff, // blue
      0x0000ff, // indigo
      0xff00ff, // violet
      0xffffff, // white
      0x000000  // black
    };
  
  private static final Map<Block, Byte> textureBytes = new HashMap<>();

  public CaveCrystalGolem(final EntityType<? extends GolemBase> entityType, final World world) {
    super(entityType, world, QuarkGolemsEntities.QUARK, TEXTURE_NAMES, QuarkGolemsEntities.MODID, LOOT_TABLE_NAMES);
  }
  
  @Override
  protected void registerGoals() {
    super.registerGoals();
    int lightInt = 11;
    final BlockState state = GolemItems.UTILITY_LIGHT.getDefaultState().with(BlockUtilityGlow.LIGHT_LEVEL, lightInt);
    this.goalSelector.addGoal(9, new PlaceUtilityBlockGoal(this, state, BlockUtilityGlow.UPDATE_TICKS, 
        this.getConfigBool(ALLOW_SPECIAL), true, null));
  }
  
  @Override
  public void livingTick() {
    super.livingTick();
    if (this.world.isRemote() && rand.nextInt(3) == 0) {
      final Vec3d pos = this.getPositionVec();
      double px = pos.x + (this.rand.nextDouble() - 0.5D) * (double) this.getWidth();
      double py = pos.y + this.rand.nextDouble() * (double) this.getHeight() - 0.25D;
      double pz = pos.z + (this.rand.nextDouble() - 0.5D) * (double) this.getWidth();
      final Vector3f colors = GolemRenderSettings.unpackColor(CRYSTAL_COLORS[this.getTextureNum()]);
      this.world.addParticle(ParticleTypes.AMBIENT_ENTITY_EFFECT, px, py, pz, colors.getX(), colors.getY(), colors.getZ());
    }    
  }

  @Override
  public ItemStack getCreativeReturn(final RayTraceResult target) {
    return new ItemStack(GolemTextureBytes.getByByte(textureBytes, (byte) this.getTextureNum()));
  }

  @Override
  public Map<Block, Byte> getTextureBytes() {
    // we have to do this late because not all blocks are loaded initially
    if(textureBytes.isEmpty()) {
      fillTextureBytes();
    }
    return textureBytes;
  }
  
  private static void fillTextureBytes() {
    // fills a map with Block-Byte references to correctly build the golem
    if(ModuleLoader.INSTANCE.isModuleEnabled(CaveCrystalUndergroundBiomeModule.class)) {
      textureBytes.put(ForgeRegistries.BLOCKS.getValue(new ResourceLocation("quark", "red_crystal")), (byte)0);
      textureBytes.put(ForgeRegistries.BLOCKS.getValue(new ResourceLocation("quark", "orange_crystal")), (byte)1);
      textureBytes.put(ForgeRegistries.BLOCKS.getValue(new ResourceLocation("quark", "yellow_crystal")), (byte)2);
      textureBytes.put(ForgeRegistries.BLOCKS.getValue(new ResourceLocation("quark", "green_crystal")), (byte)3);
      textureBytes.put(ForgeRegistries.BLOCKS.getValue(new ResourceLocation("quark", "blue_crystal")), (byte)4);
      textureBytes.put(ForgeRegistries.BLOCKS.getValue(new ResourceLocation("quark", "indigo_crystal")), (byte)5);
      textureBytes.put(ForgeRegistries.BLOCKS.getValue(new ResourceLocation("quark", "violet_crystal")), (byte)6);
      textureBytes.put(ForgeRegistries.BLOCKS.getValue(new ResourceLocation("quark", "white_crystal")), (byte)7);
      textureBytes.put(ForgeRegistries.BLOCKS.getValue(new ResourceLocation("quark", "black_crystal")), (byte)8);
    }
  }
}
