package com.mcmoddev.golems_quark.entity;

import java.util.HashMap;
import java.util.Map;

import com.mcmoddev.golems.blocks.BlockUtilityGlow;
import com.mcmoddev.golems.entity.ai.PlaceUtilityBlockGoal;
import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.entity.base.GolemMultiColorized;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.main.GolemItems;
import com.mcmoddev.golems.util.GolemNames;
import com.mcmoddev.golems.util.GolemTextureBytes;
import com.mcmoddev.golems_quark.QuarkGolemsEntities;
import com.mcmoddev.golems_quark.util.QuarkGolemNames;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.world.module.underground.CaveCrystalUndergroundBiomeModule;

public final class CaveCrystalGolem extends GolemMultiColorized {
  
  public static final String ALLOW_SPECIAL = "Allow Special: Emit Light";

  private static final Integer[] CRYSTAL_COLORS = {
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

  private static final ResourceLocation TEXTURE_BASE = GolemBase.makeTexture(ExtraGolems.MODID, GolemNames.STAINEDGLASS_GOLEM);
  private static final ResourceLocation TEXTURE_OVERLAY = GolemBase.makeTexture(QuarkGolemsEntities.MODID, QuarkGolemNames.CAVECRYSTAL_GOLEM + "_grayscale");

  public CaveCrystalGolem(final EntityType<? extends GolemBase> entityType, final World world) {
    super(entityType, world, QuarkGolemsEntities.MODID, TEXTURE_BASE, TEXTURE_OVERLAY, CRYSTAL_COLORS);
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
    if (this.world.isRemote && rand.nextInt(3) == 0) {
      final Vector3d pos = this.getPositionVec();
      double px = pos.x + (this.rand.nextDouble() - 0.5D) * (double) this.getWidth();
      double py = pos.y + this.rand.nextDouble() * (double) this.getHeight() - 0.25D;
      double pz = pos.z + (this.rand.nextDouble() - 0.5D) * (double) this.getWidth();
      this.world.addParticle(ParticleTypes.AMBIENT_ENTITY_EFFECT, px, py, pz, this.colorRed, this.colorGreen, this.colorBlue);
    /*this.world.addParticle(ParticleTypes.CRIT, 
        pos.x + (this.rand.nextDouble() - 0.5D) * (double) this.getWidth(),
        pos.y + this.rand.nextDouble() * (double) this.getHeight() - 0.25D, 
        pos.z + (this.rand.nextDouble() - 0.5D) * (double) this.getWidth(),
        (this.rand.nextDouble() - 0.5D), 
        -this.rand.nextDouble() * 0.25D, 
        (this.rand.nextDouble() - 0.5D));*/
    }    
  }
  
  @Override
  public boolean isProvidingLight() {
    return true;
  }

  @Override
  public boolean hasTransparency() {
    return true;
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
    if(false /*TODO ModuleLoader.INSTANCE.isModuleEnabled(CaveCrystalUndergroundBiomeModule.class)*/) {
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
